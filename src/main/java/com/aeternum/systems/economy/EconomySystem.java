package com.aeternum.systems.economy;

import com.aeternum.AeternumMod;
import com.aeternum.data.PlayerData;
import com.aeternum.registry.ModAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * ECONOMY SYSTEM — Aeternum
 *
 * Currency: AURUM (base unit = 1 Aurum coin)
 *   1 Aurum Coin    = 1 AU
 *   1 Aurum Stack   = 100 AU
 *   1 Aurum Ingot   = 1,000 AU
 *   1 Aurum Bar     = 10,000 AU
 *   1 Aurum Crown   = 100,000 AU
 *
 * Features:
 *   - Wallet (carried currency, lost on death if configured)
 *   - Bank (safe storage, requires visiting a bank block)
 *   - Player-to-player transfers
 *   - Territory tax system (clan owners earn % of all transactions)
 *   - Tax refund system (players get back % of taxes paid after time period)
 *   - Transaction history
 *   - Market listings (player shop system)
 *   - NPC merchant prices affected by karma
 */
public class EconomySystem {

    // Tax configuration (configurable via config)
    public static final double BASE_TAX_RATE = 0.05;         // 5% base tax
    public static final double TERRITORY_OWNER_SHARE = 0.60; // 60% of tax goes to territory owner
    public static final double TAX_REFUND_PERCENT = 0.20;    // 20% refund after refund period
    public static final long TAX_REFUND_PERIOD_TICKS = 72000L; // 1 hour in ticks

    // Market listings: UUID of seller -> list of listings
    private static final Map<UUID, List<MarketListing>> marketListings = new HashMap<>();

    // Transaction history (kept in world saved data in production)
    private static final List<Transaction> transactionHistory = new ArrayList<>();

    // ===== TRANSFER MONEY =====

    /**
     * Transfer Aurum from one player's wallet to another.
     */
    public static TransactionResult walletTransfer(ServerPlayer from, ServerPlayer to, long amount, MinecraftServer server) {
        if (amount <= 0) return new TransactionResult(false, "Amount must be positive.");
        PlayerData fromData = from.getData(ModAttachments.PLAYER_DATA.get());
        PlayerData toData = to.getData(ModAttachments.PLAYER_DATA.get());

        // Apply tax
        long tax = (long)(amount * BASE_TAX_RATE);
        long totalCost = amount + tax;

        if (fromData.getWalletBalance() < totalCost) {
            return new TransactionResult(false, "Insufficient funds. Need " + totalCost + " AU (inc. " + tax + " AU tax).");
        }

        fromData.setWalletBalance(fromData.getWalletBalance() - totalCost);
        fromData.addTaxPaid(tax);
        toData.receiveToWallet(amount);

        // Distribute tax to territory owner (if applicable)
        distributeTaxToTerritory(from, tax, server);

        recordTransaction(from.getUUID(), to.getUUID(), amount, tax, TransactionType.WALLET_TRANSFER);

        from.sendSystemMessage(Component.literal("§aTransferred §e" + amount + " AU §ato §b" + to.getName().getString() + "§a. Tax: §e" + tax + " AU"));
        to.sendSystemMessage(Component.literal("§aReceived §e" + amount + " AU §afrom §b" + from.getName().getString()));

        return new TransactionResult(true, "Transfer successful.");
    }

    /**
     * Deposit money from wallet to bank.
     */
    public static TransactionResult depositToBank(ServerPlayer player, long amount) {
        if (amount <= 0) return new TransactionResult(false, "Amount must be positive.");
        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());

        if (data.getWalletBalance() < amount) {
            return new TransactionResult(false, "Not enough Aurum in wallet.");
        }

        data.setWalletBalance(data.getWalletBalance() - amount);
        data.setBankBalance(data.getBankBalance() + amount);

        player.sendSystemMessage(Component.literal("§aDeposited §e" + amount + " AU §ato your bank account."));
        player.sendSystemMessage(Component.literal("§7Bank balance: §e" + data.getBankBalance() + " AU"));

        return new TransactionResult(true, "Deposit successful.");
    }

    /**
     * Withdraw money from bank to wallet.
     */
    public static TransactionResult withdrawFromBank(ServerPlayer player, long amount) {
        if (amount <= 0) return new TransactionResult(false, "Amount must be positive.");
        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());

        // 0.5% withdrawal fee (banking service cost)
        long fee = Math.max(1, (long)(amount * 0.005));
        long totalCost = amount + fee;

        if (data.getBankBalance() < totalCost) {
            return new TransactionResult(false, "Insufficient bank balance. Fee: " + fee + " AU");
        }

        data.setBankBalance(data.getBankBalance() - totalCost);
        data.receiveToWallet(amount);

        player.sendSystemMessage(Component.literal("§aWithdrew §e" + amount + " AU §afrom bank. Fee: §e" + fee + " AU"));

        return new TransactionResult(true, "Withdrawal successful.");
    }

    /**
     * Bank-to-bank transfer (for large transactions between players).
     * Both players must be online.
     */
    public static TransactionResult bankTransfer(ServerPlayer from, ServerPlayer to, long amount, MinecraftServer server) {
        if (amount <= 0) return new TransactionResult(false, "Amount must be positive.");
        PlayerData fromData = from.getData(ModAttachments.PLAYER_DATA.get());
        PlayerData toData = to.getData(ModAttachments.PLAYER_DATA.get());

        long tax = (long)(amount * BASE_TAX_RATE);
        long fee = Math.max(1, (long)(amount * 0.01)); // 1% bank transfer fee
        long totalCost = amount + tax + fee;

        if (fromData.getBankBalance() < totalCost) {
            return new TransactionResult(false, "Insufficient bank balance. Total needed: " + totalCost + " AU");
        }

        fromData.setBankBalance(fromData.getBankBalance() - totalCost);
        toData.setBankBalance(toData.getBankBalance() + amount);
        fromData.addTaxPaid(tax);

        distributeTaxToTerritory(from, tax, server);
        recordTransaction(from.getUUID(), to.getUUID(), amount, tax + fee, TransactionType.BANK_TRANSFER);

        from.sendSystemMessage(Component.literal("§aBank transfer of §e" + amount + " AU §ato " + to.getName().getString() + " completed. Tax+Fee: §e" + (tax + fee) + " AU"));
        to.sendSystemMessage(Component.literal("§a" + from.getName().getString() + " transferred §e" + amount + " AU §ato your bank."));

        return new TransactionResult(true, "Bank transfer successful.");
    }

    /**
     * Sell items on the market.
     */
    public static boolean listOnMarket(ServerPlayer seller, String itemDescription, long price, int quantity) {
        PlayerData data = seller.getData(ModAttachments.PLAYER_DATA.get());

        // Listing fee: 2% of total value or minimum 10 AU
        long listingFee = Math.max(10, (long)(price * quantity * 0.02));
        if (!data.payFromWallet(listingFee)) {
            seller.sendSystemMessage(Component.literal("§cCan't afford listing fee: " + listingFee + " AU"));
            return false;
        }

        marketListings.computeIfAbsent(seller.getUUID(), k -> new ArrayList<>())
            .add(new MarketListing(seller.getUUID(), seller.getName().getString(), itemDescription, price, quantity));

        seller.sendSystemMessage(Component.literal("§aListed §e" + quantity + "x " + itemDescription + " §afor §e" + price + " AU §aeach. Fee: §e" + listingFee + " AU"));
        return true;
    }

    /**
     * Buy from the market.
     */
    public static TransactionResult buyFromMarket(ServerPlayer buyer, UUID listingId, MinecraftServer server) {
        // Find and process the listing
        for (Map.Entry<UUID, List<MarketListing>> entry : marketListings.entrySet()) {
            for (MarketListing listing : entry.getValue()) {
                if (listing.getId().equals(listingId)) {
                    PlayerData buyerData = buyer.getData(ModAttachments.PLAYER_DATA.get());
                    long tax = (long)(listing.getPrice() * BASE_TAX_RATE);
                    long total = listing.getPrice() + tax;

                    if (buyerData.getWalletBalance() < total) {
                        return new TransactionResult(false, "Need " + total + " AU (inc. tax).");
                    }

                    buyerData.setWalletBalance(buyerData.getWalletBalance() - total);
                    buyerData.addTaxPaid(tax);

                    // Pay seller (check if online)
                    ServerPlayer seller = server.getPlayerList().getPlayer(entry.getKey());
                    if (seller != null) {
                        PlayerData sellerData = seller.getData(ModAttachments.PLAYER_DATA.get());
                        sellerData.receiveToWallet(listing.getPrice());
                        seller.sendSystemMessage(Component.literal("§a" + buyer.getName().getString() + " bought " + listing.getItemDescription() + " for §e" + listing.getPrice() + " AU"));
                    } else {
                        // Seller offline: send to bank
                        // In real implementation, use world saved data for offline players
                    }

                    distributeTaxToTerritory(buyer, tax, server);
                    entry.getValue().remove(listing);
                    buyer.sendSystemMessage(Component.literal("§aBought §e" + listing.getItemDescription() + " §afor §e" + listing.getPrice() + " AU"));
                    return new TransactionResult(true, "Purchase successful.");
                }
            }
        }
        return new TransactionResult(false, "Listing not found.");
    }

    /**
     * Process NPC trade with karma modifier.
     * Good karma = better prices (up to 20% discount)
     * Bad karma = worse prices (up to 30% markup)
     */
    public static long calculateNPCPrice(long basePrice, PlayerData data) {
        double karmaModifier = 1.0;
        int karma = data.getKarma();

        if (karma >= 8000) karmaModifier = 0.75;      // DIVINE: 25% discount
        else if (karma >= 5000) karmaModifier = 0.82;  // HOLY: 18% discount
        else if (karma >= 2000) karmaModifier = 0.88;  // VIRTUOUS: 12% discount
        else if (karma >= 500) karmaModifier = 0.93;   // GOOD: 7% discount
        else if (karma > -500) karmaModifier = 1.0;    // NEUTRAL: normal price
        else if (karma > -2000) karmaModifier = 1.10;  // SHADY: 10% markup
        else if (karma > -5000) karmaModifier = 1.20;  // WICKED: 20% markup
        else if (karma > -8000) karmaModifier = 1.30;  // CORRUPT: 30% markup
        else karmaModifier = 1.50;                      // ABYSSAL: 50% markup (if they trade at all)

        return (long)(basePrice * karmaModifier);
    }

    /**
     * Can this NPC trade with this player based on karma?
     */
    public static boolean villagerWillTrade(PlayerData data) {
        // Villagers refuse players with very bad karma
        return data.getKarma() > -2000;
    }

    public static boolean pillagerWillTrade(PlayerData data) {
        // Pillagers only trade with players who have negative karma
        return data.getKarma() < -1000;
    }

    // ===== TAX DISTRIBUTION =====
    private static void distributeTaxToTerritory(ServerPlayer player, long taxAmount, MinecraftServer server) {
        // Find territory owner at player's location
        // In full implementation, query TerritorySystem
        // For now, this is a stub
        long ownerShare = (long)(taxAmount * TERRITORY_OWNER_SHARE);
        // TerritorySystem.addTaxRevenue(player.blockPosition(), ownerShare, server);
    }

    // ===== TRANSACTION LOGGING =====
    private static void recordTransaction(UUID from, UUID to, long amount, long fees, TransactionType type) {
        transactionHistory.add(new Transaction(from, to, amount, fees, type, System.currentTimeMillis()));
        // Keep last 1000 transactions in memory; persist to disk in SavedData
        if (transactionHistory.size() > 1000) {
            transactionHistory.remove(0);
        }
    }

    public static List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    // ===== REWARD SYSTEMS =====

    /**
     * Give Aurum to a player for completing actions.
     */
    public static void reward(ServerPlayer player, long amount, String reason) {
        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        data.receiveToWallet(amount);
        player.sendSystemMessage(Component.literal("§a+" + amount + " §eAU §7(" + reason + ")"));
        AeternumMod.LOGGER.debug("Rewarded {} AU to {} for {}", amount, player.getName().getString(), reason);
    }

    // ===== INNER CLASSES =====

    public record TransactionResult(boolean success, String message) {}

    public enum TransactionType {
        WALLET_TRANSFER, BANK_TRANSFER, MARKET_SALE, NPC_TRADE, QUEST_REWARD,
        CLAN_TAX, TERRITORY_INCOME, BOSS_DROP, PROFESSION_SALE, PENALTY
    }

    public static class Transaction {
        private final UUID from;
        private final UUID to;
        private final long amount;
        private final long fees;
        private final TransactionType type;
        private final long timestamp;

        public Transaction(UUID from, UUID to, long amount, long fees, TransactionType type, long timestamp) {
            this.from = from; this.to = to; this.amount = amount;
            this.fees = fees; this.type = type; this.timestamp = timestamp;
        }

        public UUID getFrom() { return from; }
        public UUID getTo() { return to; }
        public long getAmount() { return amount; }
        public long getFees() { return fees; }
        public TransactionType getType() { return type; }
        public long getTimestamp() { return timestamp; }
    }

    public static class MarketListing {
        private final UUID id = UUID.randomUUID();
        private final UUID sellerUUID;
        private final String sellerName;
        private final String itemDescription;
        private final long price;
        private final int quantity;
        private final long listedAt = System.currentTimeMillis();

        public MarketListing(UUID sellerUUID, String sellerName, String itemDescription, long price, int quantity) {
            this.sellerUUID = sellerUUID;
            this.sellerName = sellerName;
            this.itemDescription = itemDescription;
            this.price = price;
            this.quantity = quantity;
        }

        public UUID getId() { return id; }
        public UUID getSellerUUID() { return sellerUUID; }
        public String getSellerName() { return sellerName; }
        public String getItemDescription() { return itemDescription; }
        public long getPrice() { return price; }
        public int getQuantity() { return quantity; }
    }
}

package com.aeternum.systems.siege;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║             CASTLE SIEGE SYSTEM — AETERNUM                  ║
 * ║  Inspirado en Lineage 2 Siege System                        ║
 * ╠══════════════════════════════════════════════════════════════╣
 * ║  Castles scattered across the world.                         ║
 * ║  Every 3 days: siege window opens for 2 hours.              ║
 * ║  Castle owner clan gains:                                    ║
 * ║    - Tax revenue from the surrounding territory             ║
 * ║    - Access to the castle's unique buffs & facilities        ║
 * ║    - Special castle skills (buff all members)                ║
 * ║    - NPC guards defending the castle                         ║
 * ║    - "Castle Lord" title for the clan leader                 ║
 * ║                                                              ║
 * ║  Siege mechanics:                                            ║
 * ║    - Attackers must register 24h before                      ║
 * ║    - Defenders spawn at the throne room                      ║
 * ║    - Attackers must capture the Throne Stone                 ║
 * ║    - Castle has 3 gates, each must be broken                 ║
 * ║    - NPC defenders respawn until the gate falls              ║
 * ║    - After 2 hours, whoever holds the throne wins           ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
public class CastleSystem {

    public enum CastleType {
        FRONTIER_FORT("Frontier Fort",          CastleTier.MINOR,  50_000,  0.03, "A small but strategic fort"),
        VALLEY_KEEP("Valley Keep",              CastleTier.MINOR,  75_000,  0.04, "Controls the trade valley"),
        IRONHOLD_CASTLE("Ironhold Castle",      CastleTier.MAJOR,  150_000, 0.06, "Ancient castle of iron"),
        SKYREACH_CITADEL("Skyreach Citadel",    CastleTier.MAJOR,  200_000, 0.07, "Built in the mountain peaks"),
        DARKWATER_FORTRESS("Darkwater Fortress",CastleTier.MAJOR,  180_000, 0.065,"Sits beside a cursed sea"),
        SUNSPIRE_CASTLE("Sunspire Castle",      CastleTier.GRAND,  350_000, 0.09, "The great castle of the east"),
        SHADOWMERE_KEEP("Shadowmere Keep",      CastleTier.GRAND,  400_000, 0.10, "Realm of darkness and power"),
        AETERNUM_CITADEL("Aeternum Citadel",   CastleTier.SUPREME,700_000, 0.15, "The supreme castle. Whoever holds it is King.");

        private final String displayName;
        private final CastleTier tier;
        private final long siegeRegistrationCost;
        private final double taxRate;
        private final String description;

        CastleType(String n, CastleTier t, long cost, double tax, String desc) {
            this.displayName = n; this.tier = t;
            this.siegeRegistrationCost = cost; this.taxRate = tax; this.description = desc;
        }

        public String getDisplayName() { return displayName; }
        public CastleTier getTier() { return tier; }
        public long getSiegeRegistrationCost() { return siegeRegistrationCost; }
        public double getTaxRate() { return taxRate; }
        public String getDescription() { return description; }
    }

    public enum CastleTier { MINOR, MAJOR, GRAND, SUPREME }

    // ============================
    //        CASTLE DATA
    // ============================
    public static class Castle {
        private final UUID id;
        private final CastleType type;
        private BlockPos thronePos;
        private BlockPos[] gatePositions;

        private UUID ownerClanId = null;
        private String ownerClanName = "None";
        private long capturedAt = 0;
        private long totalTaxCollected = 0;

        // Siege state
        private boolean siegeActive = false;
        private long siegeStartTime = 0;
        private long siegeEndTime = 0;
        private final Map<UUID, SiegeRegistration> registeredAttackers = new LinkedHashMap<>();
        private final Set<UUID> registeredDefenders = new HashSet<>();
        private UUID currentThroneHolder = null; // Clan holding throne during siege
        private final List<UUID> gatesDestroyed = new ArrayList<>();

        // Castle health
        private int throneHP = 10_000;
        private int maxThroneHP = 10_000;
        private int[] gateHP = {5000, 5000, 5000};

        // Castle-specific NPC guards (count increases with tier)
        private int activeGuardCount = 0;
        private final int maxGuards;

        // Castle buffs for owners (active while owning the castle)
        private final List<CastleBuff> castleBuffs;

        public Castle(UUID id, CastleType type, BlockPos thronePos) {
            this.id = id;
            this.type = type;
            this.thronePos = thronePos;
            this.maxGuards = switch (type.getTier()) {
                case MINOR -> 10;
                case MAJOR -> 25;
                case GRAND -> 50;
                case SUPREME -> 100;
            };
            this.castleBuffs = getDefaultBuffs(type);
        }

        private List<CastleBuff> getDefaultBuffs(CastleType t) {
            List<CastleBuff> buffs = new ArrayList<>();
            buffs.add(new CastleBuff("castle_protection", "Castle Protection", "+15% defense to all clan members"));
            buffs.add(new CastleBuff("castle_prosperity", "Castle Prosperity", "+20% Aurum gain for clan members"));
            if (t.getTier().ordinal() >= CastleTier.MAJOR.ordinal()) {
                buffs.add(new CastleBuff("lords_strength", "Lord's Strength", "+10% attack to all clan members"));
                buffs.add(new CastleBuff("castle_mana", "Castle Mana", "+25% energy regen for clan members"));
            }
            if (t.getTier().ordinal() >= CastleTier.GRAND.ordinal()) {
                buffs.add(new CastleBuff("grand_lord_aura", "Grand Lord's Aura", "All stats +15% for clan members"));
                buffs.add(new CastleBuff("legendary_protection", "Legendary Protection", "-20% damage taken from non-clan players in territory"));
            }
            if (t == CastleType.AETERNUM_CITADEL) {
                buffs.add(new CastleBuff("king_of_aeternum", "King of Aeternum", "ALL stats +25%, special KING title, server-wide recognition"));
                buffs.add(new CastleBuff("royal_decree", "Royal Decree", "Can declare server-wide taxes on 1 resource type"));
            }
            return buffs;
        }

        // ============================
        //      SIEGE MANAGEMENT
        // ============================
        public boolean registerAttacker(UUID clanId, String clanName, long paidAmount) {
            if (!siegeActive && !isRegistrationPeriod()) return false;
            if (clanId.equals(ownerClanId)) return false; // Owner can't attack own castle
            registeredAttackers.put(clanId, new SiegeRegistration(clanId, clanName, paidAmount));
            return true;
        }

        public void startSiege(MinecraftServer server) {
            if (registeredAttackers.isEmpty()) return;
            siegeActive = true;
            siegeStartTime = System.currentTimeMillis();
            siegeEndTime = siegeStartTime + 2 * 60 * 60 * 1000L; // 2 hours
            throneHP = maxThroneHP;
            gatesDestroyed.clear();
            currentThroneHolder = ownerClanId;

            broadcast(server, "§4§l⚔ SIEGE BEGINS: " + type.getDisplayName() + " ⚔ §cDefenders: " +
                ownerClanName + " §7| Duration: 2 hours");
        }

        public void damageThroneStone(UUID attackingClan, int damage, MinecraftServer server) {
            if (!siegeActive) return;
            throneHP = Math.max(0, throneHP - damage);
            if (throneHP <= 0) {
                captureThrone(attackingClan, server);
            }
        }

        private void captureThrone(UUID newOwnerClan, MinecraftServer server) {
            UUID previousHolder = currentThroneHolder;
            currentThroneHolder = newOwnerClan;
            throneHP = maxThroneHP; // Reset for potential re-capture

            broadcast(server, "§6[SIEGE] §e" + newOwnerClan + " §6has captured the Throne Stone of " + type.getDisplayName() + "!");
        }

        public void endSiege(MinecraftServer server) {
            if (!siegeActive) return;
            siegeActive = false;

            if (currentThroneHolder != null && !currentThroneHolder.equals(ownerClanId)) {
                // New owner!
                UUID previousOwner = ownerClanId;
                ownerClanId = currentThroneHolder;
                capturedAt = System.currentTimeMillis();

                broadcast(server, "§6§l★ CASTLE CAPTURED! ★");
                broadcast(server, "§e" + ownerClanName + " §6now controls §e" + type.getDisplayName() + "!");

                // Give "Castle Lord" title to the new leader
                // TitleSystem.grantTitle(...)
            } else {
                broadcast(server, "§a[SIEGE] Defenders held! " + ownerClanName + " retains " + type.getDisplayName() + "!");
            }

            registeredAttackers.clear();
            gatesDestroyed.clear();
        }

        public boolean isRegistrationPeriod() {
            // Registration period: 24 hours before siege
            return false; // Simplified - full impl tracks server time
        }

        public boolean isSiegeActive() { return siegeActive; }
        public UUID getOwnerClanId() { return ownerClanId; }
        public String getOwnerClanName() { return ownerClanName; }
        public void setOwnerClanName(String name) { this.ownerClanName = name; }
        public CastleType getType() { return type; }
        public List<CastleBuff> getCastleBuffs() { return Collections.unmodifiableList(castleBuffs); }
        public int getThroneHP() { return throneHP; }
        public int getMaxThroneHP() { return maxThroneHP; }
        public long getTotalTaxCollected() { return totalTaxCollected; }
        public void addTaxRevenue(long amount) { totalTaxCollected += amount; }
        public UUID getId() { return id; }

        private static void broadcast(MinecraftServer server, String msg) {
            server.getPlayerList().broadcastSystemMessage(Component.literal(msg), false);
        }
    }

    // ============================
    //     CASTLE BUFF
    // ============================
    public record CastleBuff(String id, String name, String description) {}

    // ============================
    //     SIEGE REGISTRATION
    // ============================
    public static class SiegeRegistration {
        private final UUID clanId;
        private final String clanName;
        private final long paidAmount;
        private final long registeredAt;

        public SiegeRegistration(UUID clanId, String clanName, long paid) {
            this.clanId = clanId; this.clanName = clanName;
            this.paidAmount = paid; this.registeredAt = System.currentTimeMillis();
        }

        public UUID getClanId() { return clanId; }
        public String getClanName() { return clanName; }
        public long getPaidAmount() { return paidAmount; }
    }

    // ============================
    //     CASTLE MANAGER
    // ============================
    private static final Map<UUID, Castle> castles = new LinkedHashMap<>();

    public static void initializeCastles() {
        // In real implementation: load from world saved data or config
        // Here we create default castles at predefined positions
        addCastle(new Castle(UUID.randomUUID(), CastleType.FRONTIER_FORT,   new BlockPos(500,   64, 500)));
        addCastle(new Castle(UUID.randomUUID(), CastleType.VALLEY_KEEP,     new BlockPos(-800,  70, 200)));
        addCastle(new Castle(UUID.randomUUID(), CastleType.IRONHOLD_CASTLE, new BlockPos(1500,  80, -500)));
        addCastle(new Castle(UUID.randomUUID(), CastleType.SKYREACH_CITADEL,new BlockPos(-200, 120, -1500)));
        addCastle(new Castle(UUID.randomUUID(), CastleType.DARKWATER_FORTRESS,new BlockPos(2000, 60,  1000)));
        addCastle(new Castle(UUID.randomUUID(), CastleType.SUNSPIRE_CASTLE, new BlockPos(-2500, 75, 500)));
        addCastle(new Castle(UUID.randomUUID(), CastleType.SHADOWMERE_KEEP, new BlockPos(3000,  65,-2000)));
        addCastle(new Castle(UUID.randomUUID(), CastleType.AETERNUM_CITADEL,new BlockPos(0,     90,  0)));
    }

    public static void addCastle(Castle c) { castles.put(c.getId(), c); }
    public static Map<UUID, Castle> getCastles() { return Collections.unmodifiableMap(castles); }
    public static Collection<Castle> getAllCastles() { return castles.values(); }

    public static Castle getNearestCastle(BlockPos pos) {
        return castles.values().stream()
            .min(Comparator.comparingDouble(c -> c.thronePos.distSqr(pos)))
            .orElse(null);
    }
}

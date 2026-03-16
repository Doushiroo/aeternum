package com.aeternum.systems.titles;

import com.aeternum.data.PlayerData;
import com.aeternum.registry.ModAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.*;

/**
 * TITLE SYSTEM — Aeternum
 *
 * Titles are earned through achievements, records, and actions.
 * Some are permanent (achievements), some are dynamic (rankings - lost when surpassed).
 * Titles grant passive effects while equipped.
 * Players can only display ONE title at a time (their choice).
 */
public class TitleSystem {

    public static final Map<String, AeternumTitle> ALL_TITLES = new LinkedHashMap<>();

    static {
        // ========== COMBAT TITLES (some dynamic - top players on server) ==========
        register(new AeternumTitle("the_slayer",         "§cThe Slayer",         "Kill 100 players.",             TitleCategory.COMBAT, false, TitleEffect.ATTACK_BOOST_5));
        register(new AeternumTitle("warlord",            "§4Warlord",            "Kill 500 players. Dynamic: Top 3 PvP kills on server.", TitleCategory.COMBAT, true, TitleEffect.ATTACK_BOOST_10));
        register(new AeternumTitle("death_incarnate",    "§4§lDeath Incarnate",  "Kill 2000 players. Dynamic: #1 PvP kills on server.", TitleCategory.COMBAT, true, TitleEffect.ATTACK_BOOST_15, TitleEffect.FEAR_AURA));
        register(new AeternumTitle("duelist",            "§eDuelist",            "Win 50 consecutive 1v1 fights.", TitleCategory.COMBAT, false, TitleEffect.CRIT_BOOST_5));
        register(new AeternumTitle("the_untouchable",    "§bThe Untouchable",    "Dodge 200 attacks in combat.",  TitleCategory.COMBAT, false, TitleEffect.DODGE_BOOST_5));
        register(new AeternumTitle("ironclad",           "§7Ironclad",           "Block 500 attacks.",            TitleCategory.COMBAT, false, TitleEffect.DEFENSE_BOOST_5));
        register(new AeternumTitle("glass_cannon",       "§dGlass Cannon",       "Deal 10,000 damage in one battle.", TitleCategory.COMBAT, false, TitleEffect.ATTACK_BOOST_20, TitleEffect.DEFENSE_PENALTY_10));
        register(new AeternumTitle("berserker_reborn",   "§cBerserker Reborn",   "Defeat 20 enemies while below 10% HP.", TitleCategory.COMBAT, false, TitleEffect.ATTACK_BOOST_10, TitleEffect.REGEN_ON_LOW_HP));
        register(new AeternumTitle("colossus",           "§8Colossus",           "Survive being hit for 1000+ damage in a single blow.", TitleCategory.COMBAT, false, TitleEffect.DEFENSE_BOOST_10));
        register(new AeternumTitle("executioner",        "§4Executioner",        "Execute 100 enemies at under 20% HP.", TitleCategory.COMBAT, false, TitleEffect.EXECUTE_BONUS));
        register(new AeternumTitle("headhunter",         "§cHeadhunter",         "Achieve 50 headshots.",         TitleCategory.COMBAT, false, TitleEffect.CRIT_BOOST_10));
        register(new AeternumTitle("one_man_army",       "§4One Man Army",       "Defeat 10 enemies simultaneously.", TitleCategory.COMBAT, false, TitleEffect.AOE_BOOST_10));
        register(new AeternumTitle("shadow",             "§8Shadow",             "Backstab 100 enemies.",         TitleCategory.COMBAT, false, TitleEffect.STEALTH_SPEED));
        register(new AeternumTitle("demon_slayer",       "§5Demon Slayer",       "Slay 50 demons.",               TitleCategory.COMBAT, false, TitleEffect.HOLY_DAMAGE_BONUS));
        register(new AeternumTitle("angel_hunter",       "§4Angel Hunter",       "Slay 10 angels. Very dark path.", TitleCategory.COMBAT, false, TitleEffect.DARK_DAMAGE_BONUS));

        // ========== BOSS TITLES ==========
        register(new AeternumTitle("boss_hunter",        "§aBoss Hunter",        "Defeat 10 bosses.",             TitleCategory.BOSSES, false, TitleEffect.BOSS_DAMAGE_BONUS));
        register(new AeternumTitle("leviathan_slayer",   "§9Leviathan's Bane",   "Defeat the Leviathan alone.",   TitleCategory.BOSSES, false, TitleEffect.WATER_POWER));
        register(new AeternumTitle("world_ender",        "§4§lWorld Ender",      "Defeat all Aeternum raid bosses.", TitleCategory.BOSSES, false, TitleEffect.ATTACK_BOOST_20, TitleEffect.DEFENSE_BOOST_10));
        register(new AeternumTitle("void_conqueror",     "§5Void Conqueror",     "Defeat the Void Lord.",         TitleCategory.BOSSES, false, TitleEffect.VOID_RESISTANCE));
        register(new AeternumTitle("dragonslayer",       "§6Dragonslayer",       "Defeat the Dragon King boss.",  TitleCategory.BOSSES, false, TitleEffect.FIRE_RESISTANCE, TitleEffect.ATTACK_BOOST_10));
        register(new AeternumTitle("angel_of_death",     "§4Angel of Death",     "Defeat the Archangel boss.",    TitleCategory.BOSSES, false, TitleEffect.DARK_DAMAGE_BONUS, TitleEffect.FEAR_AURA));

        // ========== EXPLORATION TITLES ==========
        register(new AeternumTitle("wanderer",           "§aWanderer",           "Travel 10,000 blocks.",         TitleCategory.EXPLORATION, false, TitleEffect.SPEED_BOOST_5));
        register(new AeternumTitle("world_walker",       "§2World Walker",       "Discover all biomes.",          TitleCategory.EXPLORATION, false, TitleEffect.SPEED_BOOST_10));
        register(new AeternumTitle("deep_diver",         "§9Deep Diver",         "Reach the ocean floor.",        TitleCategory.EXPLORATION, false, TitleEffect.WATER_BREATHING));
        register(new AeternumTitle("sky_pilgrim",        "§eSky Pilgrim",        "Reach the highest point in the world.", TitleCategory.EXPLORATION, false, TitleEffect.FALL_NEGATION));
        register(new AeternumTitle("dimension_walker",   "§dDimension Walker",   "Visit 3 dimensions.",           TitleCategory.EXPLORATION, false, TitleEffect.SPEED_BOOST_5));
        register(new AeternumTitle("first_contact",      "§bFirst Contact",      "Discover a new biome before any other player on the server.", TitleCategory.EXPLORATION, true, TitleEffect.LUCK_BOOST));

        // ========== ECONOMIC TITLES (some dynamic) ==========
        register(new AeternumTitle("merchant",           "§6Merchant",           "Complete 100 trades.",          TitleCategory.ECONOMY, false, TitleEffect.TRADE_DISCOUNT_5));
        register(new AeternumTitle("merchant_prince",    "§6Merchant Prince",    "Accumulate 1,000,000 AU.",      TitleCategory.ECONOMY, false, TitleEffect.TRADE_DISCOUNT_10));
        register(new AeternumTitle("tycoon",             "§6§lTycoon",           "Dynamic: Richest player on server.", TitleCategory.ECONOMY, true, TitleEffect.TRADE_DISCOUNT_15, TitleEffect.AURUM_AURA));
        register(new AeternumTitle("tax_evader",         "§cTax Evader",         "Find a way to avoid taxes (secret).", TitleCategory.ECONOMY, false, TitleEffect.TAX_REDUCTION));
        register(new AeternumTitle("philanthropist",     "§aPhilanthropist",     "Donate 500,000 AU to others.",  TitleCategory.ECONOMY, false, TitleEffect.KARMA_BOOST_PASSIVE));

        // ========== CLAN TITLES (some dynamic) ==========
        register(new AeternumTitle("warbringer",         "§4Warbringer",         "Declare 10 wars as a clan.",    TitleCategory.CLAN, false, TitleEffect.ATTACK_BOOST_5));
        register(new AeternumTitle("peacemaker",         "§aPeacemaker",         "End 5 wars through negotiation.", TitleCategory.CLAN, false, TitleEffect.KARMA_BOOST_PASSIVE));
        register(new AeternumTitle("the_general",        "§eThe General",        "Lead clan to victory in 10 wars.", TitleCategory.CLAN, false, TitleEffect.COMMAND_AURA));
        register(new AeternumTitle("overlord",           "§4§lOverlord",         "Dynamic: Largest clan on server.", TitleCategory.CLAN, true, TitleEffect.COMMAND_AURA, TitleEffect.ATTACK_BOOST_5));
        register(new AeternumTitle("alliance_forger",    "§bAlliance Forger",    "Form 5 alliances.",             TitleCategory.CLAN, false, TitleEffect.CHARISMA_BOOST));
        register(new AeternumTitle("kingmaker",          "§eKingmaker",          "Place 3 different players as clan leaders.", TitleCategory.CLAN, false, TitleEffect.CHARISMA_BOOST));

        // ========== KARMA TITLES ==========
        register(new AeternumTitle("champion_of_light",  "§e§l✦ Champion of Light", "Reach Holy karma level.",  TitleCategory.KARMA, false, TitleEffect.HOLY_DAMAGE_BONUS, TitleEffect.REGEN_SMALL));
        register(new AeternumTitle("avatar_of_divinity", "§e§l★ Avatar of Divinity", "Reach Divine karma level.", TitleCategory.KARMA, false, TitleEffect.HOLY_DAMAGE_BONUS, TitleEffect.REGEN_MEDIUM, TitleEffect.DEFENSE_BOOST_10));
        register(new AeternumTitle("lord_of_darkness",   "§4§l☠ Lord of Darkness",  "Reach Corrupt karma level.", TitleCategory.KARMA, false, TitleEffect.DARK_DAMAGE_BONUS, TitleEffect.ATTACK_BOOST_10));
        register(new AeternumTitle("the_abyss",          "§4§l⚠ The Abyss",         "Reach Abyssal karma level.", TitleCategory.KARMA, false, TitleEffect.DARK_DAMAGE_BONUS, TitleEffect.ATTACK_BOOST_20, TitleEffect.FEAR_AURA));
        register(new AeternumTitle("redeemed",           "§aRedeemed",           "Return from negative karma to positive.", TitleCategory.KARMA, false, TitleEffect.KARMA_BOOST_PASSIVE));

        // ========== LEVEL TITLES ==========
        register(new AeternumTitle("adventurer",         "§aAdventurer",         "Reach level 25.",               TitleCategory.LEVEL, false, TitleEffect.XP_BOOST_5));
        register(new AeternumTitle("veteran",            "§6Veteran",            "Reach level 50.",               TitleCategory.LEVEL, false, TitleEffect.XP_BOOST_10));
        register(new AeternumTitle("champion",           "§eChampion",           "Reach level 75.",               TitleCategory.LEVEL, false, TitleEffect.ALL_STATS_5));
        register(new AeternumTitle("legend",             "§6§lLegend",           "Reach max level 100.",          TitleCategory.LEVEL, false, TitleEffect.ALL_STATS_10, TitleEffect.LEGEND_AURA));
        register(new AeternumTitle("transcendent",       "§d§lTranscendent",     "Reach max level AND complete a rebirth.", TitleCategory.LEVEL, false, TitleEffect.ALL_STATS_15, TitleEffect.TRANSCEND_AURA));

        // ========== PROFESSION TITLES ==========
        register(new AeternumTitle("master_blacksmith",  "§7Master Blacksmith",  "Reach max level in Blacksmithing.", TitleCategory.PROFESSION, false, TitleEffect.CRAFT_QUALITY_BOOST));
        register(new AeternumTitle("grand_alchemist",    "§5Grand Alchemist",    "Brew 1000 potions.",            TitleCategory.PROFESSION, false, TitleEffect.POTION_POTENCY));
        register(new AeternumTitle("master_fisher",      "§9Master Fisher",      "Catch 500 rare fish.",          TitleCategory.PROFESSION, false, TitleEffect.WATER_BREATHING));
        register(new AeternumTitle("arcane_enchanter",   "§dArcane Enchanter",   "Apply 200 enchantments.",       TitleCategory.PROFESSION, false, TitleEffect.ENCHANT_POWER));

        // ========== TAMING TITLES ==========
        register(new AeternumTitle("beast_master",       "§2Beast Master",       "Tame 10 different creature types.", TitleCategory.TAMING, false, TitleEffect.TAME_DISCOUNT));
        register(new AeternumTitle("dragon_rider",       "§6Dragon Rider",       "Tame a Dragon.",                TitleCategory.TAMING, false, TitleEffect.FIRE_RESISTANCE, TitleEffect.SPEED_BOOST_10));
        register(new AeternumTitle("angel_friend",       "§eAngel Friend",       "Befriend a Seraph.",            TitleCategory.TAMING, false, TitleEffect.HOLY_DAMAGE_BONUS, TitleEffect.REGEN_SMALL));

        // ========== HIDDEN / SECRET TITLES ==========
        register(new AeternumTitle("ghost",              "§8Ghost",              "???",                           TitleCategory.SECRET, false, TitleEffect.STEALTH_SPEED, TitleEffect.SPEED_BOOST_5));
        register(new AeternumTitle("chosen_one",         "§e§l⬡ The Chosen One", "???",                         TitleCategory.SECRET, false, TitleEffect.ALL_STATS_20));
        register(new AeternumTitle("the_last",           "§4§l☩ The Last",       "???",                         TitleCategory.SECRET, false, TitleEffect.ALL_STATS_10, TitleEffect.REGEN_MEDIUM, TitleEffect.FEAR_AURA));
    }

    private static void register(AeternumTitle title) {
        ALL_TITLES.put(title.getId(), title);
    }

    // ============================
    //       TITLE GRANTING
    // ============================
    public static void grantTitle(ServerPlayer player, String titleId, MinecraftServer server) {
        AeternumTitle title = ALL_TITLES.get(titleId);
        if (title == null) return;

        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        if (data.getUnlockedTitles().contains(titleId)) return;

        data.unlockTitle(titleId);

        player.sendSystemMessage(Component.literal("§6★ NEW TITLE UNLOCKED: " + title.getDisplayName() + " §6★"));
        server.getPlayerList().broadcastSystemMessage(
            Component.literal("§e" + player.getName().getString() + " earned the title: " + title.getDisplayName()),
            false);
    }

    public static void revokeTitle(ServerPlayer player, String titleId) {
        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        data.removeTitle(titleId);
        player.sendSystemMessage(Component.literal("§cYou have lost the title: " + ALL_TITLES.getOrDefault(titleId, new AeternumTitle(titleId, titleId, "", TitleCategory.COMBAT, false)).getDisplayName()));
    }

    public static void setActiveTitle(ServerPlayer player, String titleId) {
        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        if (!data.getUnlockedTitles().contains(titleId)) {
            player.sendSystemMessage(Component.literal("§cYou haven't earned that title."));
            return;
        }
        data.setActiveTitle(titleId);
        AeternumTitle title = ALL_TITLES.get(titleId);
        if (title != null) {
            player.sendSystemMessage(Component.literal("§aActive title set to: " + title.getDisplayName()));
        }
    }

    // ============================
    //      TITLE EFFECTS TICK
    // ============================
    public static void applyActiveTitleEffects(ServerPlayer player, PlayerData data) {
        String activeTitleId = data.getActiveTitle();
        if (activeTitleId.isEmpty()) return;

        AeternumTitle title = ALL_TITLES.get(activeTitleId);
        if (title == null) return;

        for (TitleEffect effect : title.getEffects()) {
            applyEffect(player, data, effect);
        }
    }

    private static void applyEffect(ServerPlayer player, PlayerData data, TitleEffect effect) {
        switch (effect) {
            case REGEN_SMALL -> player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0, false, false));
            case REGEN_MEDIUM -> player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1, false, false));
            case REGEN_ON_LOW_HP -> {
                if (data.getHealthPercent() < 0.2) {
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2, false, false));
                }
            }
            case WATER_BREATHING -> player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 100, 0, false, false));
            case FALL_NEGATION -> player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 0, false, false));
            case SPEED_BOOST_5 -> player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 0, false, false));
            case SPEED_BOOST_10 -> player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1, false, false));
            case DEFENSE_BOOST_5, DEFENSE_BOOST_10 -> player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 0, false, false));
            case ATTACK_BOOST_5, ATTACK_BOOST_10, ATTACK_BOOST_15, ATTACK_BOOST_20 -> player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 0, false, false));
            case FIRE_RESISTANCE -> player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0, false, false));
            case XP_BOOST_5, XP_BOOST_10 -> {} // Handled in leveling system
            case LUCK_BOOST -> player.addEffect(new MobEffectInstance(MobEffects.LUCK, 100, 0, false, false));
            default -> {} // Other effects handled per-system
        }
    }

    // ============================
    //     DYNAMIC TITLE CHECK
    // ============================
    /**
     * Check and update dynamic titles on the server.
     * Should be called periodically (e.g., every 5 minutes).
     */
    public static void updateDynamicTitles(MinecraftServer server) {
        // Top PvP kills
        ServerPlayer topKiller = null;
        int topKills = 0;

        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            PlayerData data = p.getData(ModAttachments.PLAYER_DATA.get());
            if (data.getTotalPlayerKills() > topKills) {
                topKills = data.getTotalPlayerKills();
                topKiller = p;
            }
        }

        if (topKiller != null) {
            // Give "death_incarnate" to top killer, revoke from others
            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                if (p == topKiller) {
                    grantTitle(p, "death_incarnate", server);
                } else {
                    PlayerData data = p.getData(ModAttachments.PLAYER_DATA.get());
                    if (data.getUnlockedTitles().contains("death_incarnate") &&
                        ALL_TITLES.get("death_incarnate").isDynamic()) {
                        revokeTitle(p, "death_incarnate");
                    }
                }
            }
        }

        // Richest player -> Tycoon
        ServerPlayer richest = null;
        long mostGold = 0;
        for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            PlayerData data = p.getData(ModAttachments.PLAYER_DATA.get());
            long total = data.getBankBalance() + data.getWalletBalance();
            if (total > mostGold) { mostGold = total; richest = p; }
        }
        if (richest != null) {
            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                PlayerData data = p.getData(ModAttachments.PLAYER_DATA.get());
                if (p == richest) {
                    grantTitle(p, "tycoon", server);
                } else if (data.getUnlockedTitles().contains("tycoon") && ALL_TITLES.get("tycoon").isDynamic()) {
                    revokeTitle(p, "tycoon");
                }
            }
        }
    }

    // ============================
    //   CHECK TITLE CONDITIONS
    // ============================
    public static void checkAndGrantTitles(ServerPlayer player, PlayerData data, MinecraftServer server) {
        int kills = data.getTotalPlayerKills();
        int level = data.getLevel();
        long bank = data.getBankBalance() + data.getWalletBalance();
        int karma = data.getKarma();
        int bosses = data.getBossesKilled();

        if (kills >= 100) grantTitle(player, "the_slayer", server);
        if (kills >= 500) grantTitle(player, "warlord", server);
        if (level >= 25)  grantTitle(player, "adventurer", server);
        if (level >= 50)  grantTitle(player, "veteran", server);
        if (level >= 75)  grantTitle(player, "champion", server);
        if (level >= 100) grantTitle(player, "legend", server);
        if (bank >= 1_000_000L) grantTitle(player, "merchant_prince", server);
        if (karma >= 5000) grantTitle(player, "champion_of_light", server);
        if (karma >= 8000) grantTitle(player, "avatar_of_divinity", server);
        if (karma <= -5000) grantTitle(player, "lord_of_darkness", server);
        if (karma <= -8000) grantTitle(player, "the_abyss", server);
        if (bosses >= 10)  grantTitle(player, "boss_hunter", server);
    }
}

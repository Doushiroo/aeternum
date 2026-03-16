package com.aeternum.systems.nobility;

import com.aeternum.data.PlayerData;
import com.aeternum.registry.ModAttachments;
import com.aeternum.systems.titles.TitleSystem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║              NOBILITY SYSTEM — AETERNUM                     ║
 * ║  Inspirado en el sistema Noble de Lineage 2                 ║
 * ╠══════════════════════════════════════════════════════════════╣
 * ║  La nobleza es un estatus permanente que desbloquea:        ║
 * ║   - Acceso a subclases adicionales                           ║
 * ║   - 7 habilidades exclusivas de Noble                       ║
 * ║   - Participación en el Grand Olympiad                       ║
 * ║   - Acesso a zonas especiales reservadas para nobles         ║
 * ║   - Mejor acceso a tiendas de alto nivel                     ║
 * ║   - Teleport scroll especial (limitado)                      ║
 * ║   - Chat de nobleza exclusivo                                ║
 * ║                                                              ║
 * ║  Para obtener Nobleza:                                       ║
 * ║   1. Ser nivel 50+                                           ║
 * ║   2. Completar la Quest de Nobleza (matar 3 bosses épicos)  ║
 * ║   3. Pagar el Tributo de Nobleza: 100,000 AU                ║
 * ║   4. Poseer el Ramo de Olivo (drop raro de boss)            ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
public class NobilitySystem {

    public static final int NOBILITY_REQUIRED_LEVEL = 50;
    public static final long NOBILITY_TRIBUTE_COST  = 100_000L;
    public static final int  NOBLE_BOSS_REQUIREMENT = 3;

    // Tracking noble players: UUID -> NobleData
    private static final Map<UUID, NobleData> nobleRegistry = new LinkedHashMap<>();

    // ============================
    //       GRANT NOBILITY
    // ============================
    public static boolean grantNobility(ServerPlayer player, MinecraftServer server) {
        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());

        // Check requirements
        if (data.getLevel() < NOBILITY_REQUIRED_LEVEL) {
            player.sendSystemMessage(Component.literal("§cYou need to be level " + NOBILITY_REQUIRED_LEVEL + " to become Noble."));
            return false;
        }

        if (nobleRegistry.containsKey(player.getUUID())) {
            player.sendSystemMessage(Component.literal("§eYou are already of Noble status."));
            return false;
        }

        if (!data.payFromWallet(NOBILITY_TRIBUTE_COST)) {
            player.sendSystemMessage(Component.literal("§cInsufficient funds for the Nobility Tribute: " + NOBILITY_TRIBUTE_COST + " AU"));
            return false;
        }

        if (data.getBossesKilled() < NOBLE_BOSS_REQUIREMENT) {
            player.sendSystemMessage(Component.literal("§cYou must defeat at least " + NOBLE_BOSS_REQUIREMENT + " Epic Bosses first."));
            data.receiveToWallet(NOBILITY_TRIBUTE_COST); // Refund
            return false;
        }

        // Grant nobility
        NobleData noble = new NobleData(player.getUUID(), player.getName().getString(), System.currentTimeMillis());
        nobleRegistry.put(player.getUUID(), noble);

        // Grant title and benefits
        TitleSystem.grantTitle(player, "noble", server);

        // Announcement
        server.getPlayerList().broadcastSystemMessage(
            Component.literal("§e§l✦ " + player.getName().getString() + " has ascended to NOBILITY! ✦"), false);

        player.sendSystemMessage(Component.literal("§e§l═══════ YOU ARE NOW A NOBLE ═══════"));
        player.sendSystemMessage(Component.literal("§eUnlocked:"));
        player.sendSystemMessage(Component.literal("§7  - Access to Subclass system"));
        player.sendSystemMessage(Component.literal("§7  - 7 exclusive Noble Skills"));
        player.sendSystemMessage(Component.literal("§7  - Grand Olympiad participation"));
        player.sendSystemMessage(Component.literal("§7  - Noble-only zones and merchants"));
        player.sendSystemMessage(Component.literal("§7  - Noble Chat (/nc <message>)"));
        player.sendSystemMessage(Component.literal("§7  - Special teleport scrolls (5/week)"));
        player.sendSystemMessage(Component.literal("§e§l════════════════════════════════════"));

        // Apply immediate noble buff
        player.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 1200, 0));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 1));

        return true;
    }

    // ============================
    //     NOBLE SKILLS (7)
    // ============================
    public enum NobleSkill {
        AEORE_HOLY_BLESSING(
            "Aeore's Holy Blessing",
            "Channel divine energy. Resurrect a fallen ally with 70% HP. Cannot be used in combat.",
            0, 180_000, 60
        ),
        FORTUNE_OF_NOBLESSE(
            "Fortune of Noblesse",
            "Grants the blessing of fortune to all party members. +10% XP and Aurum gain for 30 minutes.",
            0, 600_000, 0
        ),
        HARMONY_OF_NOBLESSE(
            "Harmony of Noblesse",
            "Restore all of a player's energy and stamina instantly.",
            30, 300_000, 0
        ),
        NOBLESSE_BLESSING(
            "Noblesse Blessing",
            "Protect yourself from death once. If you would die, instead survive with 1 HP.",
            50, 3600_000, 0 // 1 hour cooldown
        ),
        SUMMON_CP_ITEM(
            "Noble's Blessing",
            "Summon a consumable that restores 50% of your Combat Points.",
            0, 60_000, 0
        ),
        ESCAPE(
            "Noble's Escape",
            "Teleport yourself to the nearest safe point (spawn or last respawn stone).",
            0, 300_000, 0
        ),
        WYVERN_RECHARGE(
            "Ancient Spirit's Gift",
            "Touch a tamed or friendly creature, fully restoring its HP.",
            0, 120_000, 0
        );

        private final String name;
        private final String description;
        private final double energyCost;
        private final long cooldownMs;
        private final int levelRequirement;

        NobleSkill(String name, String desc, double energy, long cd, int level) {
            this.name = name; this.description = desc;
            this.energyCost = energy; this.cooldownMs = cd; this.levelRequirement = level;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public double getEnergyCost() { return energyCost; }
        public long getCooldownMs() { return cooldownMs; }
        public int getLevelRequirement() { return levelRequirement; }
    }

    // ============================
    //       SUBCLASS SYSTEM
    // ============================
    /**
     * Nobles can add a secondary class (subclass) after reaching level 75 in their main class.
     * The subclass starts at level 40 and can reach up to level 80.
     * The player switches between main and subclass using a command.
     * Stats and skills change when switching.
     */
    public static boolean addSubclass(ServerPlayer player, String subclassName) {
        if (!isNoble(player.getUUID())) {
            player.sendSystemMessage(Component.literal("§cOnly Nobles can have a subclass."));
            return false;
        }

        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        if (data.getLevel() < 75) {
            player.sendSystemMessage(Component.literal("§cYou need to be level 75 to add a subclass."));
            return false;
        }

        NobleData noble = nobleRegistry.get(player.getUUID());
        if (noble.hasSubclass()) {
            player.sendSystemMessage(Component.literal("§cYou already have a subclass. Reach Noble Grade 2 to add another."));
            return false;
        }

        noble.setSubclass(subclassName, 40);
        player.sendSystemMessage(Component.literal("§aSubclass §e" + subclassName + " §aadded at level 40!"));
        player.sendSystemMessage(Component.literal("§7Use /subclass switch to change between your classes."));
        return true;
    }

    // ============================
    //         NOBLE RANKS
    // ============================
    public enum NobleRank {
        NOBLE("Noble",         "Standard nobility status."),
        BARON("Baron",         "Proven noble. 100+ Olympiad points."),
        VISCOUNT("Viscount",   "Distinguished noble. Captured a castle."),
        COUNT("Count",         "Elite noble. Leader of an allied force of 100+ players."),
        DUKE("Duke",           "Grand noble. Won 3+ Olympiad periods."),
        KING("King",           "Holds Aeternum Citadel. Absolute ruler.");

        private final String title;
        private final String requirement;

        NobleRank(String title, String requirement) { this.title = title; this.requirement = requirement; }

        public String getTitle() { return title; }
        public String getRequirement() { return requirement; }
    }

    // ============================
    //          CHECKS
    // ============================
    public static boolean isNoble(UUID uuid) { return nobleRegistry.containsKey(uuid); }
    public static NobleData getNobleData(UUID uuid) { return nobleRegistry.get(uuid); }
    public static Map<UUID, NobleData> getAllNobles() { return Collections.unmodifiableMap(nobleRegistry); }

    // ============================
    //         NOBLE DATA
    // ============================
    public static class NobleData {
        private final UUID playerUUID;
        private final String playerName;
        private final long grantedAt;
        private NobleRank rank = NobleRank.NOBLE;
        private int olympiadPointsTotal = 0;
        private String subclassName = null;
        private int subclassLevel = 0;
        private int teleportScrollsRemaining = 5;
        private long teleportScrollResetTime = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L;
        private final Map<String, Long> nobleCooldowns = new HashMap<>();

        public NobleData(UUID uuid, String name, long grantedAt) {
            this.playerUUID = uuid; this.playerName = name; this.grantedAt = grantedAt;
        }

        public UUID getPlayerUUID() { return playerUUID; }
        public String getPlayerName() { return playerName; }
        public NobleRank getRank() { return rank; }
        public void setRank(NobleRank r) { this.rank = r; }
        public boolean hasSubclass() { return subclassName != null; }
        public String getSubclassName() { return subclassName; }
        public void setSubclass(String name, int level) { this.subclassName = name; this.subclassLevel = level; }
        public int getSubclassLevel() { return subclassLevel; }
        public void addOlympiadPoints(int pts) { olympiadPointsTotal += pts; }
        public int getOlympiadPointsTotal() { return olympiadPointsTotal; }

        public boolean useTeleportScroll() {
            long now = System.currentTimeMillis();
            if (now > teleportScrollResetTime) {
                teleportScrollsRemaining = 5;
                teleportScrollResetTime = now + 7 * 24 * 60 * 60 * 1000L;
            }
            if (teleportScrollsRemaining <= 0) return false;
            teleportScrollsRemaining--;
            return true;
        }

        public int getTeleportScrollsRemaining() { return teleportScrollsRemaining; }
        public long getGrantedAt() { return grantedAt; }
    }
}

package com.aeternum.systems.karma;

import com.aeternum.data.PlayerData;
import com.aeternum.registry.ModAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * KARMA SYSTEM — Aeternum
 *
 * Karma ranges from -10,000 (Abyssal) to +10,000 (Divine).
 * Karma affects:
 *   - NPC interactions (villagers, pillagers, angels, demons)
 *   - Mob spawn behavior
 *   - Skill availability
 *   - Class unlock requirements
 *   - World events triggered on the player
 *   - Economy prices
 *   - Title eligibility
 *   - Passive buffs/debuffs
 *
 * Karma DECAYS slowly toward 0 over time if you do nothing.
 * Karma events are weighted by the severity of the action.
 */
public class KarmaSystem {

    // ===== KARMA GAIN EVENTS =====
    public static final int KARMA_KILL_PLAYER_WITH_BAD_KARMA = +30;   // Killing an evil player = good
    public static final int KARMA_KILL_PLAYER_NEUTRAL         = -40;   // Killing neutral player = bad
    public static final int KARMA_KILL_PLAYER_GOOD_KARMA      = -80;   // Killing good player = very bad
    public static final int KARMA_HELP_PLAYER                  = +15;   // Healing/buffing someone
    public static final int KARMA_KILL_VILLAGE_RAID            = +50;   // Stopping a raid
    public static final int KARMA_KILL_DEMON                   = +25;   // Slaying demonic entity
    public static final int KARMA_KILL_ANGEL                   = -150;  // Killing an angel (very bad)
    public static final int KARMA_DESTROY_VILLAGE_GOLEM        = -30;   // Killing a friendly entity
    public static final int KARMA_KILL_PILLAGER                = +10;   // Positive (minor)
    public static final int KARMA_DONATE_TO_POOR               = +20;   // Giving money to villagers
    public static final int KARMA_GRIEVE_PLAYER                = -50;   // Destroying another player's property
    public static final int KARMA_BETRAY_ALLY_CLAN             = -100;  // Attacking clan ally
    public static final int KARMA_COMPLETE_HOLY_QUEST          = +100;  // Holy quest completion
    public static final int KARMA_COMPLETE_DARK_QUEST          = -80;   // Dark quest
    public static final int KARMA_COMPLETE_NEUTRAL_QUEST       = +10;   // Normal quest
    public static final int KARMA_DEATH_PENALTY                = -10;   // Dying loses a bit of karma (dishonor)
    public static final int KARMA_MERCY_KILL                   = +5;    // Letting someone retreat (mechanic)
    public static final int KARMA_BOSS_KILL                    = +35;   // Defeating a boss
    public static final int KARMA_KILL_UNDEAD                  = +5;    // Minor positive
    public static final int KARMA_SUMMON_DEMON                 = -60;   // Summoning demonic entities

    // ===== APPLY KARMA =====
    public static void addKarma(ServerPlayer player, int amount, String reason) {
        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        int before = data.getKarma();
        data.addKarma(amount);
        int after = data.getKarma();

        // Notify level transitions
        notifyKarmaLevelChange(player, data, before, after);

        // Send feedback to player
        String sign = amount >= 0 ? "§a+" : "§c";
        player.sendSystemMessage(Component.literal("§7[Karma] " + sign + amount + " §8(" + reason + ")"));
    }

    public static void applyKarmaEffect(ServerPlayer player, KarmaAction action) {
        addKarma(player, action.getAmount(), action.getDescription());
    }

    // ===== KARMA DECAY =====
    // Call on server tick. Karma decays toward 0 at 1 point per minute.
    public static void tickKarmaDecay(ServerPlayer player, PlayerData data) {
        int timer = data.getKarmaDecayTimer() + 1;
        if (timer >= 1200) { // 60 seconds at 20 TPS
            timer = 0;
            int karma = data.getKarma();
            if (karma > 0) data.addKarma(-1);
            else if (karma < 0) data.addKarma(1);
        }
        data.setKarmaDecayTimer(timer);
    }

    // ===== KARMA EFFECTS =====
    // Called periodically to apply passive karma effects
    public static void applyKarmaPassiveEffects(ServerPlayer player, PlayerData data) {
        var level = data.getKarmaLevel();
        switch (level) {
            case DIVINE -> {
                // Divine aura: regeneration, resistance, slight glow
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 0, false, false));
                // Visible golden glow particle effect would be added on client side
            }
            case HOLY -> {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0, false, false));
            }
            case VIRTUOUS -> {
                // Small health regeneration bonus only
            }
            case NEUTRAL -> {
                // No special effects
            }
            case SHADY -> {
                // Villagers become wary
            }
            case WICKED -> {
                // Minor darkness effect
                player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, false));
            }
            case CORRUPT -> {
                // Dark aura: slight strength but constant minor damage
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 0, false, false));
                // Minor poison tick at this level
            }
            case ABYSSAL -> {
                // Incredibly powerful dark presence but cursed
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 100, 0, false, false));
                // Angels will attack on sight, demons are passive
            }
        }
    }

    // ===== NPC BEHAVIOR =====
    /**
     * Determine if a villager will interact with the player.
     * Returns interaction level: 2=friendly, 1=normal, 0=neutral, -1=hostile
     */
    public static int getVillagerReaction(PlayerData data) {
        int karma = data.getKarma();
        if (karma >= 5000) return 2;      // Villagers actively give discounts and gifts
        if (karma >= 1000) return 1;      // Normal friendly
        if (karma >= -500) return 0;      // Neutral - will trade but wary
        if (karma >= -2000) return -1;    // Won't trade, afraid
        return -2;                         // Flee on sight, call guards
    }

    public static int getPillagerReaction(PlayerData data) {
        int karma = data.getKarma();
        if (karma <= -5000) return 2;     // Pillagers treat them as warlord, offer special trades
        if (karma <= -2000) return 1;     // Neutral/friendly, won't attack
        if (karma <= -500) return 0;      // Cautious, might ignore
        return -1;                         // Hostile as normal
    }

    public static int getAngelReaction(PlayerData data) {
        int karma = data.getKarma();
        if (karma >= 8000) return 2;      // Divine - angels will actively protect the player
        if (karma >= 3000) return 1;      // Angels are friendly, may help in combat
        if (karma >= -3000) return 0;     // Neutral, won't help or hinder
        if (karma >= -7000) return -1;    // Angels are disapproving, may warn the player
        return -2;                         // Abyssal - angels attack on sight
    }

    public static int getDemonReaction(PlayerData data) {
        int karma = data.getKarma();
        if (karma <= -8000) return 2;     // Abyssal - demons ally with the player
        if (karma <= -4000) return 1;     // Demons are friendly/neutral
        if (karma <= 0) return 0;         // Neutral
        return -1;                         // Demons attack holy/good players
    }

    // ===== RANDOM KARMA EVENTS =====
    /**
     * Random positive events that occur for players with high karma.
     * Called occasionally from the world event handler.
     */
    public static void triggerRandomKarmaEvent(ServerPlayer player, PlayerData data) {
        var karmaLevel = data.getKarmaLevel();
        double chance = Math.random();

        switch (karmaLevel) {
            case DIVINE -> {
                if (chance < 0.01) { // 1% per check
                    // Angel descends to help the player
                    player.sendSystemMessage(Component.literal("§e✦ An angel has descended to aid you! ✦"));
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 2));
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 1200, 3));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 1));
                }
                if (chance < 0.005) {
                    // Divine drop: rare item appears
                    player.sendSystemMessage(Component.literal("§e✦ A divine gift has appeared at your feet! ✦"));
                    // Spawn blessed item
                }
            }
            case ABYSSAL -> {
                if (chance < 0.01) {
                    // Demons offer power at a cost
                    player.sendSystemMessage(Component.literal("§4[A demonic voice whispers: 'We are pleased with your darkness...']"));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 2));
                    // But at a cost: some health
                    data.damage(data.getMaxHealth() * 0.05);
                }
                if (chance < 0.003) {
                    // Demonic entity spawns to serve
                    player.sendSystemMessage(Component.literal("§4A demon emerges from the shadow, bound to serve you."));
                    // Spawn a friendly demon
                }
            }
            case HOLY, VIRTUOUS -> {
                if (chance < 0.005) {
                    player.sendSystemMessage(Component.literal("§eYou feel the warmth of divine light. +" + 10 + " HP"));
                    data.heal(10);
                }
            }
            case WICKED, CORRUPT -> {
                if (chance < 0.005) {
                    // Cursed event
                    player.sendSystemMessage(Component.literal("§8Your dark deeds weigh upon you..."));
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
                }
            }
        }
    }

    // ===== NOTIFICATION =====
    private static void notifyKarmaLevelChange(ServerPlayer player, PlayerData data, int before, int after) {
        var beforeLevel = getKarmaLevel(before);
        var afterLevel = getKarmaLevel(after);

        if (!beforeLevel.equals(afterLevel)) {
            String msg = switch (afterLevel) {
                case DIVINE -> "§e✦✦✦ You have reached DIVINE karma! The heavens rejoice! ✦✦✦";
                case HOLY -> "§eYour karma has reached HOLY level. You radiate with light.";
                case VIRTUOUS -> "§aYou are now VIRTUOUS. A path of honor and light.";
                case GOOD -> "§aYou are now GOOD karma. Villagers appreciate your presence.";
                case NEUTRAL -> "§7Your karma has returned to NEUTRAL.";
                case SHADY -> "§cYou have entered SHADY karma. Villagers grow wary of you.";
                case WICKED -> "§cYour karma is now WICKED. Darkness grows around you.";
                case CORRUPT -> "§4Your karma has become CORRUPT. Fear follows in your wake.";
                case ABYSSAL -> "§4§l⚠ You have descended to ABYSSAL karma. Pure darkness consumes you. ⚠";
            };
            player.sendSystemMessage(Component.literal(msg));
            player.sendSystemMessage(Component.literal("§7Karma: §e" + after + " §7/ 10000"));
        }
    }

    private static PlayerData.KarmaLevel getKarmaLevel(int karma) {
        if (karma >= 8000) return PlayerData.KarmaLevel.DIVINE;
        if (karma >= 5000) return PlayerData.KarmaLevel.HOLY;
        if (karma >= 2000) return PlayerData.KarmaLevel.VIRTUOUS;
        if (karma >= 500) return PlayerData.KarmaLevel.GOOD;
        if (karma > -500) return PlayerData.KarmaLevel.NEUTRAL;
        if (karma > -2000) return PlayerData.KarmaLevel.SHADY;
        if (karma > -5000) return PlayerData.KarmaLevel.WICKED;
        if (karma > -8000) return PlayerData.KarmaLevel.CORRUPT;
        return PlayerData.KarmaLevel.ABYSSAL;
    }

    // ===== KARMA ACTION ENUM =====
    public enum KarmaAction {
        KILL_INNOCENT_PLAYER("Killing an innocent player", KARMA_KILL_PLAYER_NEUTRAL),
        KILL_EVIL_PLAYER("Slaying a wicked player", KARMA_KILL_PLAYER_WITH_BAD_KARMA),
        KILL_GOOD_PLAYER("Slaying a good player", KARMA_KILL_PLAYER_GOOD_KARMA),
        HELP_PLAYER("Helping a fellow player", KARMA_HELP_PLAYER),
        STOP_RAID("Defending a village from raiders", KARMA_KILL_VILLAGE_RAID),
        KILL_DEMON("Slaying a demon", KARMA_KILL_DEMON),
        KILL_ANGEL("Slaying an angel", KARMA_KILL_ANGEL),
        KILL_PILLAGER("Defeating a Pillager", KARMA_KILL_PILLAGER),
        KILL_UNDEAD("Destroying undead", KARMA_KILL_UNDEAD),
        DONATE("Donating to the poor", KARMA_DONATE_TO_POOR),
        GRIEVE("Griefing player property", KARMA_GRIEVE_PLAYER),
        BETRAY_ALLY("Betraying a clan ally", KARMA_BETRAY_ALLY_CLAN),
        COMPLETE_HOLY_QUEST("Completing a holy quest", KARMA_COMPLETE_HOLY_QUEST),
        COMPLETE_DARK_QUEST("Completing a dark quest", KARMA_COMPLETE_DARK_QUEST),
        COMPLETE_QUEST("Completing a quest", KARMA_COMPLETE_NEUTRAL_QUEST),
        DEATH_DISHONOR("Dying in battle", KARMA_DEATH_PENALTY),
        KILL_BOSS("Defeating a boss", KARMA_BOSS_KILL),
        SUMMON_DEMON("Summoning a demon", KARMA_SUMMON_DEMON);

        private final String description;
        private final int amount;

        KarmaAction(String description, int amount) {
            this.description = description;
            this.amount = amount;
        }

        public String getDescription() { return description; }
        public int getAmount() { return amount; }
    }
}

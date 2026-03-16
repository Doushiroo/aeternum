package com.aeternum.systems;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║             WORLD EVENTS SYSTEM — AETERNUM                  ║
 * ╠══════════════════════════════════════════════════════════════╣
 * ║  Events that occur randomly or on schedule:                 ║
 * ║                                                              ║
 * ║  POSITIVE EVENTS:                                            ║
 * ║   - Divine Rain: Heals all players                          ║
 * ║   - Blessing of the Ancients: +50% XP for 1h               ║
 * ║   - Trade Festival: All prices 20% cheaper for 2h           ║
 * ║   - Hero's Call: A hero challenge appears                    ║
 * ║   - Ancient Discovery: Secret dungeons open                  ║
 * ║                                                              ║
 * ║  NEUTRAL EVENTS:                                             ║
 * ║   - Blood Moon: All mobs stronger but better drops          ║
 * ║   - Meteor Shower: Rare minerals fall from the sky          ║
 * ║   - Sea Storm: Powerful waves, sea creatures more active    ║
 * ║   - Boss Migration: Bosses move to new territories          ║
 * ║                                                              ║
 * ║  DANGER EVENTS:                                              ║
 * ║   - Demon Invasion: Demons raid towns                        ║
 * ║   - Dark Tide: All undead massively empowered               ║
 * ║   - World Hunger: All food decays faster                    ║
 * ║   - Corruption Spread: Dark zones expand                    ║
 * ║   - World Boss Awakening: A boss awakens early              ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
public class WorldEventSystem {

    private static final List<WorldEvent> EVENT_POOL = new ArrayList<>();
    private static WorldEvent activeEvent = null;
    private static long eventEndTick = 0;
    private static long nextEventCheckTick = 0;
    private static final Random RANDOM = new Random();

    // Minimum ticks between events (30 min real time = 36000 ticks)
    private static final long MIN_TICKS_BETWEEN_EVENTS = 36000L;
    // Base chance per check for an event to start (5%)
    private static final double EVENT_CHANCE = 0.05;

    static {
        // ═══════ POSITIVE EVENTS ═══════
        EVENT_POOL.add(new WorldEvent("divine_rain", "Divine Rain",
            EventType.POSITIVE, 6000L,
            "§e✦ Divine Rain blesses the world! All players are healed and gain regeneration! ✦",
            "§7The divine rain has ended.",
            server -> {
                for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                    p.heal(20);
                    p.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.REGENERATION, 1200, 1));
                }
            }, server -> {}
        ));

        EVENT_POOL.add(new WorldEvent("ancient_blessing", "Blessing of the Ancients",
            EventType.POSITIVE, 72000L, // 1 hour
            "§6✦ The Ancients bestow their blessing! All players gain +50% XP for 1 hour! ✦",
            "§7The Ancient Blessing has faded.",
            server -> {
                server.getPlayerList().broadcastSystemMessage(
                    Component.literal("§6Activate /xp_boost to benefit from this limited event!"), false);
            }, server -> {}
        ));

        EVENT_POOL.add(new WorldEvent("trade_festival", "Grand Trade Festival",
            EventType.POSITIVE, 144000L, // 2 hours
            "§a★ GRAND TRADE FESTIVAL! All NPC prices reduced by 20% for 2 hours! ★",
            "§7The Trade Festival has concluded.",
            server -> {}, server -> {}
        ));

        EVENT_POOL.add(new WorldEvent("ancient_dungeon_open", "Ancient Dungeon Revealed",
            EventType.POSITIVE, 48000L,
            "§5★ An Ancient Dungeon has been revealed! Seek the glowing portal for incredible rewards! ★",
            "§7The Ancient Dungeon has sealed itself once more.",
            server -> {
                // Spawn dungeon portal in world center area
                server.getPlayerList().broadcastSystemMessage(
                    Component.literal("§5[Oracle whispers] The Ancient Dungeon lies near the world's heart..."), false);
            }, server -> {}
        ));

        EVENT_POOL.add(new WorldEvent("shooting_star", "Shooting Star",
            EventType.POSITIVE, 12000L,
            "§e★ A Shooting Star falls! Rare stardust and star shards can be found where it lands! ★",
            "§7The fallen star has cooled.",
            server -> {
                // Drop stardust items at a random location
            }, server -> {}
        ));

        // ═══════ NEUTRAL EVENTS ═══════
        EVENT_POOL.add(new WorldEvent("blood_moon", "Blood Moon",
            EventType.NEUTRAL, 24000L, // 1 game day
            "§c☽ BLOOD MOON RISES! All mobs gain +50% strength, but drops are massively increased! ☽",
            "§7The Blood Moon fades as dawn approaches.",
            server -> {
                for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                    p.sendSystemMessage(Component.literal("§c⚠ Blood Moon active: Beware of the night! ⚠"));
                }
            }, server -> {}
        ));

        EVENT_POOL.add(new WorldEvent("meteor_shower", "Meteor Shower",
            EventType.NEUTRAL, 18000L,
            "§6☄ METEOR SHOWER! Rare ores and minerals rain from the sky! Be the first to find them! ☄",
            "§7The meteor shower ends.",
            server -> {}, server -> {}
        ));

        EVENT_POOL.add(new WorldEvent("sea_storm", "Great Sea Storm",
            EventType.NEUTRAL, 36000L,
            "§9⛵ A GREAT SEA STORM rages! Sea creatures are fierce, but sea treasures abound! ⛵",
            "§7The sea calms once more.",
            server -> {
                server.getPlayerList().broadcastSystemMessage(
                    Component.literal("§9[OCEAN] Rare sea bosses have surfaced during the storm!"), false);
            }, server -> {}
        ));

        EVENT_POOL.add(new WorldEvent("elemental_convergence", "Elemental Convergence",
            EventType.NEUTRAL, 24000L,
            "§d★ ELEMENTAL CONVERGENCE! Elemental bosses spawn across the world. Elemental drops tripled! ★",
            "§7The elemental energies stabilize.",
            server -> {}, server -> {}
        ));

        // ═══════ DANGER EVENTS ═══════
        EVENT_POOL.add(new WorldEvent("demon_invasion", "Demon Invasion",
            EventType.DANGER, 24000L,
            "§4⚠ DEMON INVASION! Demon hordes attack villages! Defend them for massive karma and rewards! ⚠",
            "§7The demon invasion has been repelled (or succeeded).",
            server -> {
                for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                    p.sendSystemMessage(Component.literal("§c[ALERT] Demons are attacking nearby villages! Defend them!"));
                }
            }, server -> {}
        ));

        EVENT_POOL.add(new WorldEvent("dark_tide", "Dark Tide",
            EventType.DANGER, 18000L,
            "§8☠ DARK TIDE! All undead creatures become massively empowered! The living must survive! ☠",
            "§7The Dark Tide recedes.",
            server -> {
                for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                    p.sendSystemMessage(Component.literal("§8Seek shelter or fight through the darkness..."));
                }
            }, server -> {}
        ));

        EVENT_POOL.add(new WorldEvent("world_boss_awakening", "World Boss Awakening",
            EventType.DANGER, -1L, // No duration, boss just appears
            "§4§l⚠ ⚠ WORLD BOSS AWAKENS! A great evil stirs! All players must unite! ⚠ ⚠",
            "§7The World Boss has been defeated... for now.",
            server -> {
                server.getPlayerList().broadcastSystemMessage(
                    Component.literal("§4[WARNING] A World Boss has spawned. Check /bosses for location!"), false);
                // Spawn world boss at predefined location
            }, server -> {}
        ));

        EVENT_POOL.add(new WorldEvent("corruption_spread", "Corruption Spread",
            EventType.DANGER, 36000L,
            "§5☠ CORRUPTION SPREADS! Dark zones expand. Only powerful players can push it back! ☠",
            "§7The corruption has been contained... for now.",
            server -> {}, server -> {}
        ));

        EVENT_POOL.add(new WorldEvent("clan_war_tournament", "Grand War Tournament",
            EventType.NEUTRAL, 144000L, // 2 hours
            "§e⚔ GRAND WAR TOURNAMENT! All clan wars during the next 2h award double war points! ⚔",
            "§7The Grand War Tournament concludes. War points tallied.",
            server -> {}, server -> {}
        ));

        EVENT_POOL.add(new WorldEvent("divine_trial", "Divine Trial",
            EventType.POSITIVE, 48000L,
            "§e✦ DIVINE TRIAL! Prove your worth. Complete the trial for permanent stat bonuses! ✦",
            "§7The Divine Trial portal closes.",
            server -> {
                server.getPlayerList().broadcastSystemMessage(
                    Component.literal("§e[Heaven] The Trial of Light opens at the Temple of Dawn!"), false);
            }, server -> {}
        ));

        EVENT_POOL.add(new WorldEvent("heroes_return", "Heroes' Return",
            EventType.POSITIVE, 24000L,
            "§6★ HEROES' RETURN! All current Heroes receive a temporary power boost! ★",
            "§7The Heroes' power boost fades.",
            server -> {
                for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                    // Boost current olympiad heroes
                    p.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.DAMAGE_BOOST, 24000, 1));
                }
            }, server -> {}
        ));
    }

    // ═══════════════════════════════════════
    //         TICK & EVENT MANAGEMENT
    // ═══════════════════════════════════════
    public static void tick(MinecraftServer server, long currentTick) {
        // End active event if expired
        if (activeEvent != null && activeEvent.getDurationTicks() > 0 && currentTick >= eventEndTick) {
            endCurrentEvent(server);
        }

        // Check for new event
        if (activeEvent == null && currentTick >= nextEventCheckTick) {
            nextEventCheckTick = currentTick + 1200; // Check every minute

            if (RANDOM.nextDouble() < EVENT_CHANCE) {
                triggerRandomEvent(server, currentTick);
            }
        }
    }

    public static void triggerRandomEvent(MinecraftServer server, long currentTick) {
        if (EVENT_POOL.isEmpty()) return;

        WorldEvent event = EVENT_POOL.get(RANDOM.nextInt(EVENT_POOL.size()));
        activeEvent = event;
        eventEndTick = event.getDurationTicks() > 0 ? currentTick + event.getDurationTicks() : Long.MAX_VALUE;

        // Broadcast start
        server.getPlayerList().broadcastSystemMessage(Component.literal(""), false);
        server.getPlayerList().broadcastSystemMessage(
            Component.literal("§6§l══════ WORLD EVENT ══════"), false);
        server.getPlayerList().broadcastSystemMessage(
            Component.literal(event.getStartMessage()), false);
        if (event.getDurationTicks() > 0) {
            long minutes = (event.getDurationTicks() / 20) / 60;
            server.getPlayerList().broadcastSystemMessage(
                Component.literal("§7Duration: " + minutes + " minutes"), false);
        }
        server.getPlayerList().broadcastSystemMessage(
            Component.literal("§6§l═══════════════════════"), false);

        // Execute start action
        event.onStart(server);

        // Set minimum time until next event
        nextEventCheckTick = currentTick + event.getDurationTicks() + MIN_TICKS_BETWEEN_EVENTS;
    }

    public static void triggerSpecificEvent(String eventId, MinecraftServer server, long currentTick) {
        EVENT_POOL.stream()
            .filter(e -> e.getId().equals(eventId))
            .findFirst()
            .ifPresent(e -> {
                activeEvent = e;
                eventEndTick = e.getDurationTicks() > 0 ? currentTick + e.getDurationTicks() : Long.MAX_VALUE;
                server.getPlayerList().broadcastSystemMessage(
                    Component.literal(e.getStartMessage()), false);
                e.onStart(server);
            });
    }

    private static void endCurrentEvent(MinecraftServer server) {
        if (activeEvent == null) return;
        server.getPlayerList().broadcastSystemMessage(
            Component.literal(activeEvent.getEndMessage()), false);
        activeEvent.onEnd(server);
        activeEvent = null;
    }

    public static WorldEvent getActiveEvent() { return activeEvent; }
    public static boolean hasActiveEvent() { return activeEvent != null; }
    public static boolean isEventActive(String eventId) {
        return activeEvent != null && activeEvent.getId().equals(eventId);
    }

    // ═══════════════════════════════════════
    //           EVENT CLASS
    // ═══════════════════════════════════════
    public static class WorldEvent {
        private final String id;
        private final String name;
        private final EventType type;
        private final long durationTicks;
        private final String startMessage;
        private final String endMessage;
        private final java.util.function.Consumer<MinecraftServer> onStart;
        private final java.util.function.Consumer<MinecraftServer> onEnd;

        public WorldEvent(String id, String name, EventType type, long duration,
                          String startMsg, String endMsg,
                          java.util.function.Consumer<MinecraftServer> onStart,
                          java.util.function.Consumer<MinecraftServer> onEnd) {
            this.id = id; this.name = name; this.type = type; this.durationTicks = duration;
            this.startMessage = startMsg; this.endMessage = endMsg;
            this.onStart = onStart; this.onEnd = onEnd;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public EventType getType() { return type; }
        public long getDurationTicks() { return durationTicks; }
        public String getStartMessage() { return startMessage; }
        public String getEndMessage() { return endMessage; }
        public void onStart(MinecraftServer server) { onStart.accept(server); }
        public void onEnd(MinecraftServer server) { onEnd.accept(server); }
    }

    public enum EventType { POSITIVE, NEUTRAL, DANGER }
}

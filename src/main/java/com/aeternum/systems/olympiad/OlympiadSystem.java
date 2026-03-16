package com.aeternum.systems.olympiad;

import com.aeternum.data.PlayerData;
import com.aeternum.registry.ModAttachments;
import com.aeternum.systems.titles.TitleSystem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║          GRAND OLYMPIAD SYSTEM — AETERNUM                   ║
 * ║  Inspirado en Lineage 2 Grand Olympiad                       ║
 * ╠══════════════════════════════════════════════════════════════╣
 * ║  - Se realiza cada 7 días (configurable)                     ║
 * ║  - Los jugadores se inscriben pagando una tarifa             ║
 * ║  - Combates 1v1 clasificatorios por clase                    ║
 * ║  - Top jugador de cada clase → HÉROE DE CLASE                ║
 * ║  - El mejor en general → GRAN CAMPEÓN (título dinámico)      ║
 * ║  - Los Héroes ganan: arma especial, habilidades exclusivas,  ║
 * ║    aura visible, chat de héroes, inmunidad parcial en guerras ║
 * ║  - Puntos de Olimpiada (OP) se acumulan entre ediciones      ║
 * ║  - Con OP se compran habilidades y equipo exclusivo          ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
public class OlympiadSystem {

    // ============================
    //        CONFIGURACIÓN
    // ============================
    public static final long OLYMPIAD_DURATION_TICKS     = 7 * 24000L * 20L; // 7 días en ticks
    public static final long REGISTRATION_FEE_AU         = 1_000L;            // Costo de inscripción
    public static final int  MIN_LEVEL_TO_PARTICIPATE    = 30;                // Nivel mínimo
    public static final int  WIN_POINTS                  = 10;                // OP por victoria
    public static final int  LOSS_POINTS                 = -3;                // OP pérdida
    public static final int  MINIMUM_FIGHTS_FOR_HERO     = 5;                 // Mínimo de peleas para ser héroe
    public static final int  HERO_VALID_DAYS             = 7;                 // Días que dura el título de Héroe

    // ============================
    //       ESTADO GLOBAL
    // ============================
    private static OlympiadPeriod currentPeriod = null;
    private static final Map<UUID, OlympiadParticipant> participants = new LinkedHashMap<>();
    private static final Map<UUID, OlympiadParticipant> allTimeRecords = new LinkedHashMap<>();
    private static final List<OlympiadMatch> matchHistory = new ArrayList<>();
    private static final Queue<OlympiadMatch> matchQueue = new LinkedList<>();
    private static final Map<UUID, OlympiadHero> activeHeroes = new HashMap<>();
    private static long periodStartTick = 0;
    private static boolean isActive = false;
    private static boolean isRegistrationOpen = false;

    // Hall of Fame: Lista de todos los campeones históricos
    private static final List<HallOfFameEntry> hallOfFame = new ArrayList<>();

    // ============================
    //    INICIO DE OLIMPIADA
    // ============================
    public static void startOlympiadPeriod(MinecraftServer server) {
        if (isActive) return;

        currentPeriod = new OlympiadPeriod(UUID.randomUUID(), System.currentTimeMillis());
        participants.clear();
        matchQueue.clear();
        isActive = true;
        isRegistrationOpen = true;

        // Anuncio global épico
        server.getPlayerList().broadcastSystemMessage(
            Component.literal(""), false);
        server.getPlayerList().broadcastSystemMessage(
            Component.literal("§6§l  ╔══════════════════════════════════════╗"), false);
        server.getPlayerList().broadcastSystemMessage(
            Component.literal("§6§l  ║   ⚔  GRAND OLYMPIAD BEGINS!  ⚔     ║"), false);
        server.getPlayerList().broadcastSystemMessage(
            Component.literal("§e§l  ║  Prove your worth. Become a HERO.    ║"), false);
        server.getPlayerList().broadcastSystemMessage(
            Component.literal("§7§l  ║  /olympiad register — Fee: 1,000 AU  ║"), false);
        server.getPlayerList().broadcastSystemMessage(
            Component.literal("§6§l  ╚══════════════════════════════════════╝"), false);
        server.getPlayerList().broadcastSystemMessage(
            Component.literal(""), false);
    }

    // ============================
    //        REGISTRO
    // ============================
    public static boolean register(ServerPlayer player) {
        if (!isRegistrationOpen) {
            player.sendSystemMessage(Component.literal("§cOlympiad registration is not open."));
            return false;
        }

        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());

        if (data.getLevel() < MIN_LEVEL_TO_PARTICIPATE) {
            player.sendSystemMessage(Component.literal("§cYou need to be level " + MIN_LEVEL_TO_PARTICIPATE + " to participate."));
            return false;
        }

        if (participants.containsKey(player.getUUID())) {
            player.sendSystemMessage(Component.literal("§eYou are already registered."));
            return false;
        }

        if (!data.payFromWallet(REGISTRATION_FEE_AU)) {
            player.sendSystemMessage(Component.literal("§cInsufficient funds. Fee: " + REGISTRATION_FEE_AU + " AU"));
            return false;
        }

        OlympiadParticipant p = new OlympiadParticipant(
            player.getUUID(), player.getName().getString(),
            data.getPlayerClass().name(), data.getLevel()
        );
        participants.put(player.getUUID(), p);

        player.sendSystemMessage(Component.literal(
            "§a§l★ You are now registered in the Grand Olympiad! ★\n" +
            "§7Class: §e" + data.getPlayerClass().getDisplayName() + "\n" +
            "§7Minimum fights for Hero: §e" + MINIMUM_FIGHTS_FOR_HERO));
        return true;
    }

    // ============================
    //      COMBATES / MATCHES
    // ============================
    /**
     * Empareja a dos jugadores para un combate de Olimpiada.
     * Se prefiere emparejar jugadores de la misma clase primero.
     */
    public static boolean queueForMatch(ServerPlayer player, ServerPlayer server2) {
        // En implementación completa: sistema de matchmaking automático
        // Aquí: manual challenge o cola automática
        OlympiadParticipant p1 = participants.get(player.getUUID());
        OlympiadParticipant p2 = participants.get(server2.getUUID());

        if (p1 == null || p2 == null) return false;
        if (p1.isInMatch() || p2.isInMatch()) return false;

        OlympiadMatch match = new OlympiadMatch(
            UUID.randomUUID(), player.getUUID(), server2.getUUID(),
            p1.getClassName().equals(p2.getClassName()) // Same class match?
        );

        matchQueue.add(match);
        p1.setInMatch(true);
        p2.setInMatch(true);
        return true;
    }

    /**
     * Registrar resultado de un combate.
     */
    public static void recordMatchResult(UUID winnerUUID, UUID loserUUID, int winnerHPRemaining, MinecraftServer server) {
        OlympiadParticipant winner = participants.get(winnerUUID);
        OlympiadParticipant loser = participants.get(loserUUID);

        if (winner == null || loser == null) return;

        // Actualizar estadísticas
        winner.addWin(WIN_POINTS);
        loser.addLoss(LOSS_POINTS);

        winner.setInMatch(false);
        loser.setInMatch(false);

        // Registrar en historial
        OlympiadMatch completedMatch = new OlympiadMatch(UUID.randomUUID(), winnerUUID, loserUUID, false);
        completedMatch.setResult(winnerUUID, winnerHPRemaining);
        matchHistory.add(completedMatch);
        currentPeriod.incrementMatches();

        // Notificar
        ServerPlayer winnerPlayer = server.getPlayerList().getPlayer(winnerUUID);
        ServerPlayer loserPlayer = server.getPlayerList().getPlayer(loserUUID);

        if (winnerPlayer != null)
            winnerPlayer.sendSystemMessage(Component.literal("§6§l⚔ VICTORY! §e+" + WIN_POINTS + " OP §7| Fights: " + winner.getTotalFights()));
        if (loserPlayer != null)
            loserPlayer.sendSystemMessage(Component.literal("§c⚔ DEFEAT. §7" + LOSS_POINTS + " OP | Keep fighting!"));

        // Broadcast si fue una pelea emocionante (ganador con poco HP)
        if (winnerHPRemaining < 15) {
            server.getPlayerList().broadcastSystemMessage(
                Component.literal("§6[Olympiad] §e" + winner.getPlayerName() + " §7defeated §e" +
                    loser.getPlayerName() + " §6in an EPIC close fight!"), false);
        }
    }

    // ============================
    //    FIN DE OLIMPIADA / HÉROES
    // ============================
    public static void endOlympiadPeriod(MinecraftServer server) {
        if (!isActive) return;

        isActive = false;
        isRegistrationOpen = false;

        server.getPlayerList().broadcastSystemMessage(
            Component.literal("§6§l══════════ GRAND OLYMPIAD RESULTS ══════════"), false);

        // Agrupar por clase y determinar héroe por clase
        Map<String, OlympiadParticipant> heroByClass = new LinkedHashMap<>();
        OlympiadParticipant grandChampion = null;
        int maxPoints = Integer.MIN_VALUE;

        for (OlympiadParticipant p : participants.values()) {
            if (p.getTotalFights() < MINIMUM_FIGHTS_FOR_HERO) continue;

            // Héroe por clase
            String cls = p.getClassName();
            OlympiadParticipant currentBest = heroByClass.get(cls);
            if (currentBest == null || p.getOlympiadPoints() > currentBest.getOlympiadPoints()) {
                heroByClass.put(cls, p);
            }

            // Gran Campeón general
            if (p.getOlympiadPoints() > maxPoints) {
                maxPoints = p.getOlympiadPoints();
                grandChampion = p;
            }
        }

        // Coronar héroes
        revokeAllHeroes(server);

        for (Map.Entry<String, OlympiadParticipant> entry : heroByClass.entrySet()) {
            crownHero(entry.getValue(), entry.getKey(), false, server);
        }

        // Coronar Gran Campeón
        if (grandChampion != null) {
            crownGrandChampion(grandChampion, server);
        }

        // Guardar en Hall of Fame
        if (grandChampion != null) {
            hallOfFame.add(new HallOfFameEntry(
                grandChampion.getPlayerUUID(),
                grandChampion.getPlayerName(),
                grandChampion.getClassName(),
                grandChampion.getOlympiadPoints(),
                grandChampion.getWins(),
                grandChampion.getLosses(),
                System.currentTimeMillis()
            ));
        }

        // Acumular OP históricos
        for (OlympiadParticipant p : participants.values()) {
            OlympiadParticipant historical = allTimeRecords.computeIfAbsent(
                p.getPlayerUUID(),
                k -> new OlympiadParticipant(k, p.getPlayerName(), p.getClassName(), p.getLevel())
            );
            historical.addHistoricalOP(p.getOlympiadPoints());
        }

        server.getPlayerList().broadcastSystemMessage(
            Component.literal("§6§l══════════════════════════════════════════"), false);
    }

    private static void crownHero(OlympiadParticipant participant, String className, boolean isGrand, MinecraftServer server) {
        ServerPlayer player = server.getPlayerList().getPlayer(participant.getPlayerUUID());

        OlympiadHero hero = new OlympiadHero(
            participant.getPlayerUUID(),
            participant.getPlayerName(),
            className,
            isGrand,
            System.currentTimeMillis() + (HERO_VALID_DAYS * 24 * 60 * 60 * 1000L)
        );
        activeHeroes.put(participant.getPlayerUUID(), hero);

        String heroTitle = isGrand ?
            "§6§l⚔ GRAND CHAMPION ⚔" :
            "§e§l★ CLASS HERO [" + className + "] ★";

        server.getPlayerList().broadcastSystemMessage(
            Component.literal(heroTitle + " §7→ §f" + participant.getPlayerName()), false);

        if (player != null) {
            // Dar título especial
            TitleSystem.grantTitle(player, isGrand ? "grand_champion" : "class_hero_" + className.toLowerCase(), server);

            player.sendSystemMessage(Component.literal("§6§l⚔ YOU ARE NOW A HERO OF AETERNUM! ⚔"));
            player.sendSystemMessage(Component.literal("§eBenefits: Hero aura, exclusive skills, Hero chat, reduced war vulnerability."));

            // Aplicar efectos inmediatos
            hero.applyHeroEffects(player);
        }
    }

    private static void crownGrandChampion(OlympiadParticipant participant, MinecraftServer server) {
        crownHero(participant, participant.getClassName(), true, server);
        server.getPlayerList().broadcastSystemMessage(
            Component.literal("§6§l★★★ " + participant.getPlayerName() +
                " is the GRAND CHAMPION of the Olympiad with " + participant.getOlympiadPoints() + " OP! ★★★"), false);
    }

    private static void revokeAllHeroes(MinecraftServer server) {
        for (OlympiadHero hero : activeHeroes.values()) {
            ServerPlayer player = server.getPlayerList().getPlayer(hero.getPlayerUUID());
            if (player != null) {
                hero.removeHeroEffects(player);
                player.sendSystemMessage(Component.literal("§7Your Hero status has ended. Enter the next Olympiad to reclaim glory!"));
            }
        }
        activeHeroes.clear();
    }

    // ============================
    //   COMPRAS CON OP HISTÓRICOS
    // ============================
    public static boolean purchaseWithOP(ServerPlayer player, OPShopItem item) {
        OlympiadParticipant historical = allTimeRecords.get(player.getUUID());
        if (historical == null || historical.getHistoricalOP() < item.getCost()) {
            player.sendSystemMessage(Component.literal("§cInsufficient Olympiad Points. Need: " + item.getCost() + " OP"));
            return false;
        }
        historical.spendHistoricalOP(item.getCost());
        item.giveToPlayer(player);
        return true;
    }

    // ============================
    //           GETTERS
    // ============================
    public static boolean isActive() { return isActive; }
    public static boolean isRegistrationOpen() { return isRegistrationOpen; }
    public static Map<UUID, OlympiadParticipant> getParticipants() { return Collections.unmodifiableMap(participants); }
    public static Map<UUID, OlympiadHero> getActiveHeroes() { return Collections.unmodifiableMap(activeHeroes); }
    public static List<HallOfFameEntry> getHallOfFame() { return Collections.unmodifiableList(hallOfFame); }
    public static boolean isHero(UUID uuid) { return activeHeroes.containsKey(uuid); }

    public static OlympiadParticipant getParticipant(UUID uuid) { return participants.get(uuid); }
    public static OlympiadHero getHero(UUID uuid) { return activeHeroes.get(uuid); }

    // ============================
    //       CLASES INTERNAS
    // ============================

    /** Participante en la olimpiada actual */
    public static class OlympiadParticipant {
        private final UUID playerUUID;
        private final String playerName;
        private final String className;
        private final int level;
        private int olympiadPoints = 0;
        private int wins = 0;
        private int losses = 0;
        private boolean inMatch = false;
        private int historicalOP = 0;

        public OlympiadParticipant(UUID uuid, String name, String cls, int level) {
            this.playerUUID = uuid; this.playerName = name; this.className = cls; this.level = level;
        }

        public void addWin(int points) { wins++; olympiadPoints += points; }
        public void addLoss(int points) { losses++; olympiadPoints = Math.max(0, olympiadPoints + points); }
        public int getTotalFights() { return wins + losses; }
        public void setInMatch(boolean b) { inMatch = b; }
        public boolean isInMatch() { return inMatch; }
        public void addHistoricalOP(int op) { historicalOP += op; }
        public void spendHistoricalOP(int op) { historicalOP -= op; }

        public UUID getPlayerUUID() { return playerUUID; }
        public String getPlayerName() { return playerName; }
        public String getClassName() { return className; }
        public int getLevel() { return level; }
        public int getOlympiadPoints() { return olympiadPoints; }
        public int getWins() { return wins; }
        public int getLosses() { return losses; }
        public int getHistoricalOP() { return historicalOP; }
    }

    /** Héroe activo en el mundo */
    public static class OlympiadHero {
        private final UUID playerUUID;
        private final String playerName;
        private final String className;
        private final boolean isGrandChampion;
        private final long expiresAt;

        public OlympiadHero(UUID uuid, String name, String cls, boolean grand, long expires) {
            this.playerUUID = uuid; this.playerName = name; this.className = cls;
            this.isGrandChampion = grand; this.expiresAt = expires;
        }

        public void applyHeroEffects(ServerPlayer player) {
            // Héroes tienen efectos especiales permanentes durante su mandato
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.HERO_OF_THE_VILLAGE, Integer.MAX_VALUE, 0, false, false));
            // En implementación real: agregar acceso a skills exclusivas, glow especial, etc.
        }

        public void removeHeroEffects(ServerPlayer player) {
            player.removeEffect(net.minecraft.world.effect.MobEffects.HERO_OF_THE_VILLAGE);
        }

        public boolean isExpired() { return System.currentTimeMillis() > expiresAt; }
        public UUID getPlayerUUID() { return playerUUID; }
        public String getPlayerName() { return playerName; }
        public String getClassName() { return className; }
        public boolean isGrandChampion() { return isGrandChampion; }
        public long getExpiresAt() { return expiresAt; }
    }

    /** Combate de Olimpiada */
    public static class OlympiadMatch {
        private final UUID id;
        private final UUID player1UUID;
        private final UUID player2UUID;
        private final boolean sameClass;
        private UUID winnerUUID;
        private int winnerHPRemaining;
        private long startTime;
        private long endTime;

        public OlympiadMatch(UUID id, UUID p1, UUID p2, boolean sameClass) {
            this.id = id; this.player1UUID = p1; this.player2UUID = p2;
            this.sameClass = sameClass; this.startTime = System.currentTimeMillis();
        }

        public void setResult(UUID winner, int hpRemaining) {
            this.winnerUUID = winner; this.winnerHPRemaining = hpRemaining;
            this.endTime = System.currentTimeMillis();
        }

        public UUID getId() { return id; }
        public UUID getWinnerUUID() { return winnerUUID; }
    }

    /** Período de Olimpiada */
    public static class OlympiadPeriod {
        private final UUID id;
        private final long startTime;
        private int totalMatches = 0;

        public OlympiadPeriod(UUID id, long start) { this.id = id; this.startTime = start; }
        public void incrementMatches() { totalMatches++; }
        public int getTotalMatches() { return totalMatches; }
    }

    /** Entrada en el Hall of Fame */
    public static class HallOfFameEntry {
        private final UUID playerUUID;
        private final String playerName;
        private final String className;
        private final int finalOP;
        private final int wins;
        private final int losses;
        private final long achievedAt;

        public HallOfFameEntry(UUID uuid, String name, String cls, int op, int w, int l, long at) {
            this.playerUUID = uuid; this.playerName = name; this.className = cls;
            this.finalOP = op; this.wins = w; this.losses = l; this.achievedAt = at;
        }

        public String getPlayerName() { return playerName; }
        public int getFinalOP() { return finalOP; }
        public String getClassName() { return className; }
    }

    /** Ítems comprables con OP históricos */
    public enum OPShopItem {
        HERO_WEAPON_SWORD("Hero's Blade",           500,  "A weapon of champions. +30% damage"),
        HERO_WEAPON_BOW("Hero's Longbow",           500,  "+30% bow damage, unbreakable"),
        HERO_WEAPON_STAFF("Hero's Arcane Staff",    500,  "+30% magic damage, unique particle"),
        OLYMPIAD_ARMOR_SET("Olympiad Armor Set",    800,  "+20% all stats while worn"),
        HERO_SKILL_SHOUT("Hero Shout",              200,  "Broadcast a message to the entire server"),
        HERO_SKILL_INVINCIBILITY("Brief Invincibility", 400, "5 seconds of complete invulnerability, 10min CD"),
        ATTRIBUTE_STONE_STR("STR Stone",             100, "+2 STR permanently"),
        ATTRIBUTE_STONE_INT("INT Stone",             100, "+2 INT permanently"),
        ATTRIBUTE_STONE_VIT("VIT Stone",             100, "+2 VIT permanently"),
        LEGACY_TITLE_SCROLL("Legacy Title Scroll",  300,  "Permanently unlock a special title"),
        REBIRTH_CRYSTAL("Rebirth Crystal",          1000, "Perform a Rebirth (prestige reset with bonuses)");

        private final String name;
        private final int cost;
        private final String description;

        OPShopItem(String name, int cost, String description) {
            this.name = name; this.cost = cost; this.description = description;
        }

        public int getCost() { return cost; }
        public String getName() { return name; }
        public String getDescription() { return description; }

        public void giveToPlayer(ServerPlayer player) {
            player.sendSystemMessage(Component.literal("§6[Olympiad Shop] §aYou received: §e" + name + " §7- " + description));
            // In full implementation: actually give the item/effect
        }
    }
}

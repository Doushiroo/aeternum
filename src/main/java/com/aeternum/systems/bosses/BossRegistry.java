package com.aeternum.systems.bosses;

import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║                  EPIC BOSS REGISTRY — AETERNUM                      ║
 * ╠══════════════════════════════════════════════════════════════════════╣
 * ║  TIER 1 - SOLO BOSSES: Derrotables por 1 jugador fuerte             ║
 * ║  TIER 2 - GROUP BOSSES: Requieren 3-10 jugadores                    ║
 * ║  TIER 3 - RAID BOSSES: 10-40 jugadores                              ║
 * ║  TIER 4 - WORLD BOSSES: Toda la comunidad (40+ jugadores)           ║
 * ║  TIER 5 - LEGENDARY: Los más poderosos. Casi invencibles.           ║
 * ╠══════════════════════════════════════════════════════════════════════╣
 * ║  Todos los bosses:                                                    ║
 * ║   - Tienen respawn timer (configurable)                              ║
 * ║   - Drops únicos y Aurum                                             ║
 * ║   - Anuncio global al aparecer                                       ║
 * ║   - Inteligencia artificial avanzada                                 ║
 * ║   - Múltiples fases                                                  ║
 * ║   - Algunos son dueños de territorios                                ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 */
public class BossRegistry {

    public static final Map<String, BossDefinition> ALL_BOSSES = new LinkedHashMap<>();

    static {
        // ════════════════════════════════════════════
        //   TIER 1 — SOLO BOSSES (1 jugador fuerte)
        // ════════════════════════════════════════════
        register(new BossDefinition("shadow_warden",    "Shadow Warden",
            BossTier.SOLO, BossType.UNDEAD,
            5_000,   20,  1, 150,  false, false,
            "An undead soldier risen from a forgotten war. Guards the forest ruins.",
            3600L, // 1 hora de respawn
            new String[]{"shadow_sword", "fallen_knight_helm"},
            500L, 100));

        register(new BossDefinition("corrupted_druid",  "Corrupted Druid",
            BossTier.SOLO, BossType.NATURE,
            7_000,   15,  2, 200,  false, false,
            "A nature mage twisted by dark magic. Controls vines and poison.",
            5400L,
            new String[]{"nature_staff", "poison_essence"},
            750L, 150));

        register(new BossDefinition("sea_witch",        "Sea Witch Kira",
            BossTier.SOLO, BossType.AQUATIC,
            8_000,   18,  2, 180,  false, false,
            "Manipulates tides and calls sea creatures to battle.",
            7200L,
            new String[]{"tidal_wand", "pearl_crown"},
            900L, 180));

        register(new BossDefinition("cave_titan",       "Cave Titan",
            BossTier.SOLO, BossType.GIANT,
            15_000,  25,  3, 300,  false, false,
            "Massive stone golem deep in the caverns. Collapses the cave on intruders.",
            10800L,
            new String[]{"titan_gauntlet", "stone_heart_gem"},
            1200L, 200));

        // ════════════════════════════════════════════
        //   TIER 2 — GROUP BOSSES (3-10 jugadores)
        // ════════════════════════════════════════════
        register(new BossDefinition("venom_queen",      "Venom Queen Sythara",
            BossTier.GROUP, BossType.BEAST,
            40_000,  35,  5, 500,  false, true,
            "Ancient serpent queen who births endless snake minions. Her venom is nearly incurable.",
            21600L, // 6 horas
            new String[]{"queen_fang", "venom_scale_armor", "antidote_recipe"},
            3000L, 500));

        register(new BossDefinition("frost_dragon",     "Frost Dragon Kalthazar",
            BossTier.GROUP, BossType.DRAGON,
            60_000,  40,  6, 700,  false, true,
            "Ancient dragon of the frozen peaks. Breathes absolute zero. Claims the mountain as its territory.",
            28800L, // 8 horas
            new String[]{"dragon_scale_frost", "kalthazar_eye", "frozen_breath_scroll"},
            5000L, 800,
            true, "Mountain Territory")); // Territory owner

        register(new BossDefinition("nightmare_knight", "Nightmare Knight Vorax",
            BossTier.GROUP, BossType.UNDEAD,
            55_000,  45,  5, 650,  false, true,
            "A legendary fallen paladin reborn as death itself. Has healer minion that must be killed first.",
            25200L,
            new String[]{"nightmare_lance", "void_shield", "dark_knight_armor"},
            4500L, 700));

        register(new BossDefinition("djinn_lord",       "Djinn Lord Al'Rashid",
            BossTier.GROUP, BossType.ELEMENTAL,
            70_000,  50,  7, 800,  false, true,
            "Master of wind and fire. Teleports constantly, summons elemental warriors. Controls the desert.",
            36000L,
            new String[]{"djinn_ring", "sands_of_time", "al_rashid_saber"},
            6000L, 900,
            true, "Desert Territory"));

        register(new BossDefinition("abyssal_leviathan","Abyssal Leviathan",
            BossTier.GROUP, BossType.AQUATIC,
            80_000,  55,  8, 900,  false, true,
            "Colossal sea serpent lurking in the ocean abyss. Rules all sea creatures. Almost impossible alone.",
            43200L, // 12 horas
            new String[]{"leviathan_scale", "abyssal_trident", "ocean_lord_crown"},
            8000L, 1200,
            true, "Ocean Territory"));

        // ════════════════════════════════════════════
        //   TIER 3 — RAID BOSSES (10-40 jugadores)
        // ════════════════════════════════════════════
        register(new BossDefinition("demon_warlord",    "Demon Warlord Balrog",
            BossTier.RAID, BossType.DEMON,
            200_000, 70,  15, 2000, true,  true,
            "A demon general of unmatched power. Summons hordes of demons. High karma negative players might get mercy from him.",
            72000L, // 1 día
            new String[]{"balrog_axe", "demon_general_armor", "hellfire_core", "balrog_eye"},
            20000L, 3000));

        register(new BossDefinition("arch_lich",        "Arch Lich Mortivian",
            BossTier.RAID, BossType.UNDEAD,
            180_000, 75,  12, 1800, true,  true,
            "The most powerful undead mage. Casts continent-wide curses. His phylactery must be destroyed before he can be killed.",
            86400L, // 24 horas
            new String[]{"lich_phylactery_fragment", "mort_staff", "soul_crown", "ancient_dark_tome"},
            18000L, 2800));

        register(new BossDefinition("storm_titan",      "Storm Titan Thorvald",
            BossTier.RAID, BossType.GIANT,
            220_000, 80,  20, 2500, true,  true,
            "A titan born from a lightning storm. Commands weather. Each of his stomps creates earthquakes.",
            86400L,
            new String[]{"storm_hammer_shard", "titan_heart", "sky_gauntlets", "weather_orb"},
            25000L, 3500,
            true, "Storm Peaks Territory"));

        register(new BossDefinition("fallen_archangel", "Fallen Archangel Serafael",
            BossTier.RAID, BossType.FALLEN_ANGEL,
            250_000, 85,  20, 3000, true,  true,
            "Once a guardian of light, now twisted. Commands both holy and dark powers. His wings can blot out the sun.",
            108000L, // 30 horas
            new String[]{"fallen_wing_fragment", "serafael_halo", "corrupted_holy_sword", "angelic_core"},
            30000L, 4000));

        register(new BossDefinition("sea_emperor",      "Sea Emperor Poseidron",
            BossTier.RAID, BossType.AQUATIC,
            300_000, 90,  25, 3500, true,  true,
            "The god-emperor of the seas. Floods entire regions. Only attackable by boat or with water breathing.",
            129600L, // 36 horas
            new String[]{"poseidron_trident", "ocean_crown", "sea_emperor_scale", "tidal_orb"},
            35000L, 5000,
            true, "Deep Ocean Territory"));

        // ════════════════════════════════════════════
        //   TIER 4 — WORLD BOSSES (40+ jugadores)
        // ════════════════════════════════════════════
        register(new BossDefinition("world_serpent",    "Jormungandr, World Serpent",
            BossTier.WORLD, BossType.ELDER_BEAST,
            1_000_000, 100, 40, 8000, true, true,
            "The world serpent from ancient prophecy. Its body spans entire continents. Immune to all physical damage unless its scales are broken.",
            604800L, // 1 semana
            new String[]{"world_serpent_fang", "jormungandr_scale_set", "ancient_venom", "serpent_crown", "world_lore_tome"},
            100000L, 15000,
            true, "World Serpent's Domain"));

        register(new BossDefinition("void_dragon",      "Void Dragon Nytheron",
            BossTier.WORLD, BossType.DRAGON,
            800_000, 100, 35, 7000, true, true,
            "The void made flesh. Tears rifts in reality. Immune to all magic. Can erase players from existence temporarily.",
            518400L, // 6 días
            new String[]{"void_dragon_shard", "nytheron_eye_set", "void_scale_armor", "dimensional_blade"},
            80000L, 12000,
            true, "Void Rift Zone"));

        register(new BossDefinition("archangel_michael","Archangel Michael",
            BossTier.WORLD, BossType.ARCHANGEL,
            900_000, 100, 40, 10000, true, true,
            "A true Archangel. Appears only when darkness overwhelms the world. Attacks players with negative karma aggressively. Can be pacified by high-karma players.",
            864000L, // 10 días
            new String[]{"seraphim_feather", "divine_blade_shard", "holy_aegis_plate", "archangel_blessing_scroll"},
            0L, 20000)); // No Aurum drop (divine entity)

        register(new BossDefinition("demon_king",       "Demon King Malphas",
            BossTier.WORLD, BossType.DEMON,
            950_000, 100, 40, 12000, true, true,
            "A demon king who invades the overworld. Corrupts the land. Only clans with positive war record can damage him properly.",
            864000L,
            new String[]{"malphas_horn_shard", "demon_king_armor", "hellfire_throne", "corruption_orb"},
            150000L, 25000));

        // ════════════════════════════════════════════
        //   TIER 5 — LEGENDARY (Almost invincible)
        // ════════════════════════════════════════════
        register(new BossDefinition("the_ancient",      "The Ancient — First Being",
            BossTier.LEGENDARY, BossType.PRIMORDIAL,
            5_000_000, 100, 50, 50000, true, true,
            "The first entity to exist in this world. Has no true weakness. Changes tactics mid-fight. Can only be harmed by combining all element types in one attack.",
            2592000L, // 30 días
            new String[]{"ancient_fragment_set", "primordial_armor_set", "first_blade", "creation_orb", "ancient_tome_complete"},
            500000L, 100000));

        register(new BossDefinition("end_of_days",      "End of Days — The Void Itself",
            BossTier.LEGENDARY, BossType.VOID,
            10_000_000, 100, 50, 100000, true, true,
            "The embodiment of the end. Appears only once per server epoch. Defeating it rewards the entire server with special permanent world bonuses.",
            -1L, // Never respawns (once per server lifetime)
            new String[]{"void_essence_complete", "aeternum_weapon_set_complete", "legend_of_aeternum_title"},
            1000000L, 200000));
    }

    private static void register(BossDefinition def) {
        ALL_BOSSES.put(def.getId(), def);
    }

    public static BossDefinition getBoss(String id) { return ALL_BOSSES.get(id); }
    public static Collection<BossDefinition> getAllBosses() { return ALL_BOSSES.values(); }

    public static List<BossDefinition> getBossesByTier(BossTier tier) {
        return ALL_BOSSES.values().stream()
            .filter(b -> b.getTier() == tier)
            .toList();
    }
}

package com.aeternum.registry;

import com.aeternum.AeternumMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║             ENTITY REGISTRY — AETERNUM                      ║
 * ╠══════════════════════════════════════════════════════════════╣
 * ║  CELESTIALS (Angelic beings - react to karma)               ║
 * ║  INFERNALS  (Demonic beings - react to karma inversely)     ║
 * ║  OCEAN      (Animated sea life - makes the ocean alive)     ║
 * ║  SKY        (Sky entities - makes the sky meaningful)       ║
 * ║  WILD       (Enhanced natural creatures, tameable)          ║
 * ║  UNDEAD     (Complex undead with unique behaviors)          ║
 * ║  CONSTRUCTS (Mechanical / magical creations)                ║
 * ║  BOSSES     (Boss-tier entities - each one unique)          ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * Total: 60+ unique entity types
 */
public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, AeternumMod.MODID);

    // ═══════════════════════════
    //  CELESTIALS — Angel Beings
    // ═══════════════════════════
    /** Lowest rank angel. Scouts. Passive to good karma players. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> CHERUB =
        ENTITY_TYPES.register("cherub", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.CREATURE)
                .sized(0.6f, 0.6f).build("cherub"));

    /** Standard angel warrior. Attacks dark karma players. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> GUARDIAN_ANGEL =
        ENTITY_TYPES.register("guardian_angel", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.CREATURE)
                .sized(0.8f, 2.0f).build("guardian_angel"));

    /** Powerful angel with healing aura. Rare encounter. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> SERAPH =
        ENTITY_TYPES.register("seraph", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.CREATURE)
                .sized(1.0f, 2.5f).build("seraph"));

    /** Multi-winged angel. Can be tamed (extremely hard) by divine karma players. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> DOMINION_ANGEL =
        ENTITY_TYPES.register("dominion_angel", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.CREATURE)
                .sized(1.2f, 3.0f).build("dominion_angel"));

    /** Boss-tier. The fallen archangel. One of the RAID bosses. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> FALLEN_ARCHANGEL =
        ENTITY_TYPES.register("fallen_archangel", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(1.5f, 4.0f).fireImmune().build("fallen_archangel"));

    /** World Boss. Archangel Michael. Only one exists. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> ARCHANGEL_MICHAEL =
        ENTITY_TYPES.register("archangel_michael", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(2.0f, 5.0f).fireImmune().build("archangel_michael"));

    // ═══════════════════════════
    //  INFERNALS — Demon Beings
    // ═══════════════════════════
    /** Minor demon. Attacks positive karma players. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> IMP =
        ENTITY_TYPES.register("imp", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(0.5f, 0.8f).fireImmune().build("imp"));

    /** Standard demon warrior. Has weapon. Intelligent. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> DEMON_WARRIOR =
        ENTITY_TYPES.register("demon_warrior", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(0.9f, 2.2f).fireImmune().build("demon_warrior"));

    /** Demon mage. Casts hellfire. Can corrupt blocks around it. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> INFERNAL_MAGE =
        ENTITY_TYPES.register("infernal_mage", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(0.9f, 2.0f).fireImmune().build("infernal_mage"));

    /** Succubus. Seduces players to drop their guard (confusion debuff). */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> SUCCUBUS =
        ENTITY_TYPES.register("succubus", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(0.8f, 1.9f).fireImmune().build("succubus"));

    /** Hellhound. Fast, fire-breathing dog. Can be tamed by dark karma players. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> HELLHOUND =
        ENTITY_TYPES.register("hellhound", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(1.2f, 1.2f).fireImmune().build("hellhound"));

    /** Demon captain. Leads groups. Has tactics. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> DEMON_CAPTAIN =
        ENTITY_TYPES.register("demon_captain", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(1.0f, 2.5f).fireImmune().build("demon_captain"));

    /** Boss demon. Raid tier. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> DEMON_WARLORD =
        ENTITY_TYPES.register("demon_warlord", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(1.5f, 3.5f).fireImmune().build("demon_warlord"));

    // ═══════════════════════════
    //  OCEAN — Makes the sea alive
    // ═══════════════════════════
    /** Giant crab. Territorial. Drops sea armor pieces. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> GIANT_CRAB =
        ENTITY_TYPES.register("giant_crab", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.WATER_CREATURE)
                .sized(1.5f, 0.8f).build("giant_crab"));

    /** Sea serpent. Ambushes ships. Coils around prey. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> SEA_SERPENT =
        ENTITY_TYPES.register("sea_serpent", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.WATER_CREATURE)
                .sized(1.5f, 3.0f).build("sea_serpent"));

    /** Mermaid/Merman. Neutral. Trades in sea materials. Can help players find shipwrecks. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> MERFOLK =
        ENTITY_TYPES.register("merfolk", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.WATER_CREATURE)
                .sized(0.8f, 1.8f).build("merfolk"));

    /** Kelpie. Drags players underwater. Nightmare in rivers. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> KELPIE =
        ENTITY_TYPES.register("kelpie", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.WATER_CREATURE)
                .sized(1.3f, 2.0f).build("kelpie"));

    /** Kraken. Group boss in ocean. Can attack ships. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> KRAKEN =
        ENTITY_TYPES.register("kraken", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(3.0f, 2.0f).build("kraken"));

    /** Abyssal Leviathan. The raid boss of the ocean. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> ABYSSAL_LEVIATHAN =
        ENTITY_TYPES.register("abyssal_leviathan", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(5.0f, 5.0f).build("abyssal_leviathan"));

    /** Deep sea angler fish. Lures and stuns. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> DEEP_ANGLER =
        ENTITY_TYPES.register("deep_angler", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.WATER_CREATURE)
                .sized(1.0f, 1.0f).build("deep_angler"));

    /** Coral elemental. Guards coral reefs. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> CORAL_ELEMENTAL =
        ENTITY_TYPES.register("coral_elemental", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.WATER_CREATURE)
                .sized(1.2f, 1.5f).build("coral_elemental"));

    /** Poseidron. World boss of the sea. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> SEA_EMPEROR =
        ENTITY_TYPES.register("sea_emperor", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(4.0f, 6.0f).build("sea_emperor"));

    // ═══════════════════════════
    //  SKY — Makes the air alive
    // ═══════════════════════════
    /** Wind sprite. Tiny. Guides players toward treasures if karma is good. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> WIND_SPRITE =
        ENTITY_TYPES.register("wind_sprite", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.AMBIENT)
                .sized(0.3f, 0.3f).build("wind_sprite"));

    /** Storm hawk. Aggressive bird. Drops rare feathers. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> STORM_HAWK =
        ENTITY_TYPES.register("storm_hawk", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.CREATURE)
                .sized(0.9f, 0.5f).build("storm_hawk"));

    /** Sky whale. Peaceful giant floating creature. Rare. Drops unique sky materials. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> SKY_WHALE =
        ENTITY_TYPES.register("sky_whale", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.CREATURE)
                .sized(4.0f, 2.0f).build("sky_whale"));

    /** Thunderbird. Storm-caller. Attacks during thunderstorms. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> THUNDERBIRD =
        ENTITY_TYPES.register("thunderbird", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(2.0f, 1.5f).build("thunderbird"));

    /** Sky dragon (juvenile). Can be tamed by level 50+ Dragon Knight class. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> SKY_DRAGON =
        ENTITY_TYPES.register("sky_dragon", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(2.5f, 2.0f).fireImmune().build("sky_dragon"));

    /** Frost wyrm. Ice-breathing dragon variant. Cold biomes. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> FROST_WYRM =
        ENTITY_TYPES.register("frost_wyrm", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(2.5f, 2.0f).build("frost_wyrm"));

    /** Cloud giant. Lives in high-altitude clouds. Has floating island base. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> CLOUD_GIANT =
        ENTITY_TYPES.register("cloud_giant", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(1.5f, 4.0f).build("cloud_giant"));

    // ═══════════════════════════
    //  WILD — Tameable creatures
    // ═══════════════════════════
    /** Direwolf. Strong wolf. Medium difficulty to tame. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> DIREWOLF =
        ENTITY_TYPES.register("direwolf", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.CREATURE)
                .sized(1.0f, 1.0f).build("direwolf"));

    /** Sabertooth tiger. Ambush predator. Hard to tame. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> SABERTOOTH =
        ENTITY_TYPES.register("sabertooth", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(1.2f, 1.0f).build("sabertooth"));

    /** Gryphon. Flying mount when tamed. Very hard to tame. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> GRYPHON =
        ENTITY_TYPES.register("gryphon", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(1.5f, 1.5f).build("gryphon"));

    /** Basilisk. Petrifies with gaze. Nearly impossible to tame. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> BASILISK =
        ENTITY_TYPES.register("basilisk", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(1.3f, 0.8f).build("basilisk"));

    /** Phoenix. Fire creature. Reborn on death. Extremely hard to tame. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> PHOENIX =
        ENTITY_TYPES.register("phoenix", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.CREATURE)
                .sized(1.0f, 1.5f).fireImmune().build("phoenix"));

    /** Unicorn. Divine horse. Only tameable by Holy+ karma players. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> UNICORN =
        ENTITY_TYPES.register("unicorn", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.CREATURE)
                .sized(1.3f, 1.6f).build("unicorn"));

    // ═══════════════════════════
    //  UNDEAD — Complex undead
    // ═══════════════════════════
    /** Wight. Intelligent undead warrior. Can be commanded by Necromancers. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> WIGHT =
        ENTITY_TYPES.register("wight", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(0.8f, 2.0f).build("wight"));

    /** Wraith. Incorporeal undead. Phases through walls. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> WRAITH =
        ENTITY_TYPES.register("wraith", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(0.6f, 2.0f).build("wraith"));

    /** Bone Dragon. Necromancer-summoned massive undead dragon. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> BONE_DRAGON =
        ENTITY_TYPES.register("bone_dragon", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(2.5f, 2.5f).build("bone_dragon"));

    /** Lich (player-summoned). The undead mage form. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> LICH =
        ENTITY_TYPES.register("lich", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.MONSTER)
                .sized(0.8f, 2.2f).build("lich"));

    // ═══════════════════════════
    //  SPECIAL NPC ENTITIES
    // ═══════════════════════════
    /** Mobile merchant who travels between towns. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> TRAVELING_MERCHANT =
        ENTITY_TYPES.register("traveling_merchant", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.CREATURE)
                .sized(0.6f, 1.95f).build("traveling_merchant"));

    /** Banker NPC for deposit/withdraw. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> AETERNUM_BANKER =
        ENTITY_TYPES.register("aeternum_banker", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.CREATURE)
                .sized(0.6f, 1.95f).build("aeternum_banker"));

    /** Class trainer who allows skill unlocks. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> CLASS_TRAINER =
        ENTITY_TYPES.register("class_trainer", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.CREATURE)
                .sized(0.6f, 1.95f).build("class_trainer"));

    /** Dark merchant. Only trades with negative karma players. */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> DARK_MERCHANT =
        ENTITY_TYPES.register("dark_merchant", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.CREATURE)
                .sized(0.6f, 1.95f).build("dark_merchant"));

    /** Oracle NPC who gives prophecies (hints about world events). */
    public static final DeferredHolder<EntityType<?>, EntityType<?>> ORACLE =
        ENTITY_TYPES.register("oracle", () ->
            EntityType.Builder.of((type, level) -> null, MobCategory.CREATURE)
                .sized(0.6f, 1.95f).build("oracle"));
}

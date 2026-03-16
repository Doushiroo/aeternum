package com.aeternum.systems.skills;

import com.aeternum.data.PlayerData;
import com.aeternum.systems.classes.PlayerClass;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.*;

/**
 * Skill Registry and System — defines ALL skills available in Aeternum.
 * Skills have: cooldown, energy cost, level requirement, class requirements.
 * Players unlock skills via skill points; can mix skills from different trees.
 */
public class SkillSystem {

    // =============================================
    //             SKILL DEFINITIONS
    // =============================================
    public static final Map<String, AeternumSkill> ALL_SKILLS = new LinkedHashMap<>();

    static {
        // ========== WARRIOR SKILLS ==========
        register(new AeternumSkill("warrior_shield_bash",       "Shield Bash",          "Bash the enemy with your shield, stunning them for 2 seconds.",                    PlayerClass.WARRIOR,  1,  20, 8000,  SkillType.ACTIVE,  SkillTag.MELEE,   SkillTag.STUN));
        register(new AeternumSkill("warrior_battle_cry",        "Battle Cry",           "Empower all nearby allies, increasing their attack by 25% for 15s.",               PlayerClass.WARRIOR,  5,  35, 30000, SkillType.ACTIVE,  SkillTag.BUFF,    SkillTag.AOE));
        register(new AeternumSkill("warrior_whirlwind",         "Whirlwind",            "Spin with your weapon, dealing 180% damage to all enemies around you.",             PlayerClass.WARRIOR,  10, 40, 12000, SkillType.ACTIVE,  SkillTag.MELEE,   SkillTag.AOE));
        register(new AeternumSkill("warrior_last_stand",        "Last Stand",           "When below 20% HP, gain 50% damage reduction and 100% attack for 10s.",            PlayerClass.WARRIOR,  15, 0,  60000, SkillType.ACTIVE,  SkillTag.BUFF,    SkillTag.PASSIVE));
        register(new AeternumSkill("warrior_titan_rage",        "Titan Rage",           "[ULTIMATE] Triple your physical attack for 20s. Immune to CC. Visible aura.",      PlayerClass.WARRIOR,  25, 80, 120000,SkillType.ULTIMATE,SkillTag.BUFF,    SkillTag.MELEE));
        register(new AeternumSkill("warrior_iron_fortress",     "Iron Fortress",        "[PASSIVE] Gain +10% physical defense per nearby ally (max +50%).",                 PlayerClass.WARRIOR,  8,  0,  0,     SkillType.PASSIVE, SkillTag.BUFF));
        register(new AeternumSkill("warrior_charge",            "Charge",               "Dash forward, knocking back the first enemy hit and dealing 150% damage.",         PlayerClass.WARRIOR,  3,  25, 10000, SkillType.ACTIVE,  SkillTag.MELEE,   SkillTag.MOVEMENT));
        register(new AeternumSkill("warrior_war_banner",        "War Banner",           "Plant a banner that buffs all allies in range with +30 attack and +20 defense.",   PlayerClass.WARRIOR,  20, 50, 45000, SkillType.ACTIVE,  SkillTag.BUFF,    SkillTag.AOE));

        // ========== PALADIN SKILLS ==========
        register(new AeternumSkill("paladin_holy_strike",       "Holy Strike",          "Imbue your weapon with holy light. Deals bonus damage to dark entities.",          PlayerClass.PALADIN,  1,  20, 6000,  SkillType.ACTIVE,  SkillTag.MELEE,   SkillTag.HOLY));
        register(new AeternumSkill("paladin_lay_on_hands",      "Lay on Hands",         "Channel divine energy to heal target for 40% of their max HP.",                   PlayerClass.PALADIN,  3,  50, 45000, SkillType.ACTIVE,  SkillTag.HEAL));
        register(new AeternumSkill("paladin_divine_shield",     "Divine Shield",        "Become immune to all damage for 5 seconds. Cannot attack during this time.",       PlayerClass.PALADIN,  12, 60, 90000, SkillType.ACTIVE,  SkillTag.BUFF));
        register(new AeternumSkill("paladin_consecrate",        "Consecrate",           "Sanctify the ground. Dark entities take continuous holy damage while in the area.",PlayerClass.PALADIN,  8,  40, 20000, SkillType.ACTIVE,  SkillTag.AOE,     SkillTag.HOLY));
        register(new AeternumSkill("paladin_aura_of_light",     "Aura of Light",        "[PASSIVE] Emit a holy aura that reduces all incoming damage by 10% for allies.",   PlayerClass.PALADIN,  10, 0,  0,     SkillType.PASSIVE, SkillTag.HOLY,    SkillTag.BUFF));
        register(new AeternumSkill("paladin_resurrection",      "Resurrection",         "[ULTIMATE] Revive a fallen ally with 50% HP. Cannot be used in combat longer than 5m.", PlayerClass.PALADIN, 30, 100, 300000, SkillType.ULTIMATE, SkillTag.HEAL));
        register(new AeternumSkill("paladin_judgment",          "Judgment",             "Call down divine judgment on an enemy, dealing massive holy damage based on your faith.",PlayerClass.PALADIN, 18, 70, 25000, SkillType.ACTIVE, SkillTag.HOLY));

        // ========== BERSERKER SKILLS ==========
        register(new AeternumSkill("berserker_frenzy",          "Frenzy",               "Enter a frenzy: +60% attack speed, +40% damage, but -30% defense for 12s.",       PlayerClass.BERSERKER,1,  30, 20000, SkillType.ACTIVE,  SkillTag.BUFF,    SkillTag.MELEE));
        register(new AeternumSkill("berserker_warcry",          "Warcry",               "Terrify enemies, causing them to flee for 3 seconds.",                             PlayerClass.BERSERKER,5,  25, 15000, SkillType.ACTIVE,  SkillTag.DEBUFF,  SkillTag.AOE));
        register(new AeternumSkill("berserker_blood_rage",      "Blood Rage",           "[PASSIVE] For every 10% HP lost, gain 8% physical damage.",                       PlayerClass.BERSERKER,8,  0,  0,     SkillType.PASSIVE, SkillTag.BUFF));
        register(new AeternumSkill("berserker_cleave",          "Cleave",               "A powerful swing that hits all enemies in a wide arc for 200% damage.",            PlayerClass.BERSERKER,3,  35, 8000,  SkillType.ACTIVE,  SkillTag.MELEE,   SkillTag.AOE));
        register(new AeternumSkill("berserker_devastation",     "Devastation",          "[ULTIMATE] Unleash uncontrolled fury for 30s. Massively increased damage and speed. Might attack allies.", PlayerClass.BERSERKER, 25, 0, 180000, SkillType.ULTIMATE, SkillTag.BUFF));

        // ========== RANGER SKILLS ==========
        register(new AeternumSkill("ranger_precise_shot",       "Precise Shot",         "A carefully aimed shot that deals 250% damage and ignores 50% of armor.",          PlayerClass.RANGER,   1,  20, 8000,  SkillType.ACTIVE,  SkillTag.RANGED));
        register(new AeternumSkill("ranger_multishot",          "Multishot",            "Fire 5 arrows simultaneously in a spread.",                                        PlayerClass.RANGER,   5,  35, 12000, SkillType.ACTIVE,  SkillTag.RANGED,  SkillTag.AOE));
        register(new AeternumSkill("ranger_trap",               "Snare Trap",           "Place an invisible trap that roots the first enemy to step on it for 4 seconds.",  PlayerClass.RANGER,   3,  25, 15000, SkillType.ACTIVE,  SkillTag.CONTROL));
        register(new AeternumSkill("ranger_camouflage",         "Camouflage",           "Blend into the environment, becoming nearly invisible for 10 seconds.",            PlayerClass.RANGER,   8,  30, 30000, SkillType.ACTIVE,  SkillTag.STEALTH));
        register(new AeternumSkill("ranger_eagle_eye",          "[PASSIVE] Eagle Eye",  "Increase bow range by 50% and headshot damage by 40%.",                           PlayerClass.RANGER,   10, 0,  0,     SkillType.PASSIVE, SkillTag.RANGED));
        register(new AeternumSkill("ranger_storm_arrows",       "Storm of Arrows",      "[ULTIMATE] Rain 50 arrows on a target area over 5 seconds.",                      PlayerClass.RANGER,   20, 90, 120000,SkillType.ULTIMATE,SkillTag.RANGED,  SkillTag.AOE));

        // ========== ASSASSIN SKILLS ==========
        register(new AeternumSkill("assassin_shadow_step",      "Shadow Step",          "Teleport behind target enemy and deal 300% crit damage.",                         PlayerClass.ASSASSIN, 1,  30, 12000, SkillType.ACTIVE,  SkillTag.MELEE,   SkillTag.MOVEMENT));
        register(new AeternumSkill("assassin_vanish",           "Vanish",               "Become completely invisible for 8 seconds. Breaking stealth causes +100% damage.",PlayerClass.ASSASSIN, 3,  40, 20000, SkillType.ACTIVE,  SkillTag.STEALTH));
        register(new AeternumSkill("assassin_poison_blade",     "Poison Blade",         "Apply a deadly poison that deals damage over 15 seconds.",                        PlayerClass.ASSASSIN, 5,  20, 8000,  SkillType.ACTIVE,  SkillTag.DEBUFF,  SkillTag.MELEE));
        register(new AeternumSkill("assassin_death_mark",       "Death Mark",           "Mark a target. Killing the marked target in 30s resets all cooldowns.",           PlayerClass.ASSASSIN, 12, 50, 60000, SkillType.ACTIVE,  SkillTag.DEBUFF));
        register(new AeternumSkill("assassin_execution",        "Execution",            "[ULTIMATE] Instantly kill a target below 20% HP. Leaves no trace.",               PlayerClass.ASSASSIN, 20, 70, 90000, SkillType.ULTIMATE,SkillTag.MELEE));

        // ========== MAGE SKILLS ==========
        register(new AeternumSkill("mage_fireball",             "Fireball",             "Hurl a fireball that explodes on impact, burning everything nearby.",              PlayerClass.MAGE,     1,  20, 3000,  SkillType.ACTIVE,  SkillTag.MAGIC,   SkillTag.FIRE, SkillTag.AOE));
        register(new AeternumSkill("mage_ice_lance",            "Ice Lance",            "Launch a spear of ice that slows the target and deals heavy magic damage.",        PlayerClass.MAGE,     3,  25, 5000,  SkillType.ACTIVE,  SkillTag.MAGIC,   SkillTag.ICE,  SkillTag.DEBUFF));
        register(new AeternumSkill("mage_lightning_bolt",       "Lightning Bolt",       "Strike a target with lightning. Chains to up to 3 nearby enemies.",               PlayerClass.MAGE,     5,  30, 6000,  SkillType.ACTIVE,  SkillTag.MAGIC,   SkillTag.LIGHTNING, SkillTag.AOE));
        register(new AeternumSkill("mage_arcane_shield",        "Arcane Shield",        "Generate a magic barrier absorbing 500 damage before breaking.",                  PlayerClass.MAGE,     8,  50, 25000, SkillType.ACTIVE,  SkillTag.BUFF));
        register(new AeternumSkill("mage_time_stop",            "Time Stop",            "[ULTIMATE] Freeze all enemies in a large radius for 5 seconds.",                  PlayerClass.MAGE,     25, 100,180000,SkillType.ULTIMATE,SkillTag.CONTROL, SkillTag.AOE));
        register(new AeternumSkill("mage_meteor",               "Meteor",               "[ULTIMATE] Call a massive meteor from the sky, devastating a large area.",         PlayerClass.MAGE,     30, 100,300000,SkillType.ULTIMATE,SkillTag.MAGIC,   SkillTag.FIRE, SkillTag.AOE));
        register(new AeternumSkill("mage_blink",                "Blink",                "Teleport up to 20 blocks in the direction you are looking.",                       PlayerClass.MAGE,     3,  20, 8000,  SkillType.ACTIVE,  SkillTag.MOVEMENT));
        register(new AeternumSkill("mage_mana_surge",           "[PASSIVE] Mana Surge", "15% chance on spell cast to double the spell's damage at no extra energy cost.",  PlayerClass.MAGE,     10, 0,  0,     SkillType.PASSIVE, SkillTag.MAGIC));

        // ========== WARLOCK SKILLS ==========
        register(new AeternumSkill("warlock_soul_drain",        "Soul Drain",           "Drain life from target, dealing damage and healing yourself for half.",            PlayerClass.WARLOCK,  1,  25, 6000,  SkillType.ACTIVE,  SkillTag.MAGIC,   SkillTag.DARK));
        register(new AeternumSkill("warlock_dark_pact",         "Dark Pact",            "Sacrifice 20% of your HP to double your magic damage for 15 seconds.",            PlayerClass.WARLOCK,  5,  0,  30000, SkillType.ACTIVE,  SkillTag.BUFF,    SkillTag.DARK));
        register(new AeternumSkill("warlock_curse_of_weakness", "Curse of Weakness",    "Curse a target, reducing their damage by 40% and defense by 30% for 20s.",        PlayerClass.WARLOCK,  3,  30, 15000, SkillType.ACTIVE,  SkillTag.DEBUFF,  SkillTag.DARK));
        register(new AeternumSkill("warlock_hellfire",          "Hellfire",             "Summon hellfire that deals heavy fire damage in an area for 8 seconds.",           PlayerClass.WARLOCK,  10, 60, 25000, SkillType.ACTIVE,  SkillTag.MAGIC,   SkillTag.FIRE, SkillTag.AOE));
        register(new AeternumSkill("warlock_demonic_form",      "Demonic Form",         "[ULTIMATE] Transform into a demon for 30s. Massive stat boost, wings, and dark abilities.", PlayerClass.WARLOCK, 25, 80, 180000, SkillType.ULTIMATE, SkillTag.DARK, SkillTag.BUFF));
        register(new AeternumSkill("warlock_siphon_soul",       "Siphon Soul",          "[PASSIVE] On enemy kill, restore 15% of your max energy.",                        PlayerClass.WARLOCK,  8,  0,  0,     SkillType.PASSIVE, SkillTag.DARK));

        // ========== NECROMANCER SKILLS ==========
        register(new AeternumSkill("necro_raise_dead",          "Raise Dead",           "Animate a nearby corpse as a skeleton soldier under your command.",               PlayerClass.NECROMANCER,1,30, 5000,  SkillType.ACTIVE,  SkillTag.SUMMON,  SkillTag.DARK));
        register(new AeternumSkill("necro_army_of_darkness",    "Army of Darkness",     "Raise all corpses in a 20-block radius as undead warriors.",                      PlayerClass.NECROMANCER,10,80,30000, SkillType.ACTIVE,  SkillTag.SUMMON,  SkillTag.DARK, SkillTag.AOE));
        register(new AeternumSkill("necro_death_coil",          "Death Coil",           "Hurl a coil of death energy. Damages enemies, heals undead allies.",              PlayerClass.NECROMANCER,3,25, 5000,  SkillType.ACTIVE,  SkillTag.MAGIC,   SkillTag.DARK));
        register(new AeternumSkill("necro_bone_armor",          "Bone Armor",           "Surround yourself with bone fragments that absorb damage and deal damage when hit.",PlayerClass.NECROMANCER,5,35,18000, SkillType.ACTIVE,  SkillTag.BUFF));
        register(new AeternumSkill("necro_plague",              "Plague",               "Infect a target with a deadly disease that spreads to nearby enemies.",            PlayerClass.NECROMANCER,8,40, 20000, SkillType.ACTIVE,  SkillTag.DEBUFF,  SkillTag.DARK, SkillTag.AOE));
        register(new AeternumSkill("necro_lich_form",           "Lich Form",            "[ULTIMATE] Transform into a Lich. Massively empower undead, gain dark magic mastery.",PlayerClass.NECROMANCER,30,100,300000,SkillType.ULTIMATE,SkillTag.DARK,SkillTag.BUFF));
        register(new AeternumSkill("necro_death_and_decay",     "Death and Decay",      "Corrupt the ground. Enemies in the area take continuous shadow damage.",          PlayerClass.NECROMANCER,12,50,20000, SkillType.ACTIVE,  SkillTag.AOE,     SkillTag.DARK));

        // ========== SUMMONER SKILLS ==========
        register(new AeternumSkill("summoner_fire_elemental",   "Summon Fire Elemental","Summon a fire elemental that fights for you and burns enemies.",                   PlayerClass.SUMMONER, 1,  40, 60000, SkillType.ACTIVE,  SkillTag.SUMMON,  SkillTag.FIRE));
        register(new AeternumSkill("summoner_earth_golem",      "Summon Earth Golem",   "Call forth a stone golem that absorbs hits and deals heavy blows.",               PlayerClass.SUMMONER, 5,  60, 90000, SkillType.ACTIVE,  SkillTag.SUMMON));
        register(new AeternumSkill("summoner_spirit_pack",      "Spirit Pack",          "Summon 3 spirit wolves that track and attack your target.",                        PlayerClass.SUMMONER, 8,  50, 30000, SkillType.ACTIVE,  SkillTag.SUMMON));
        register(new AeternumSkill("summoner_bind_soul",        "Bind Soul",            "[PASSIVE] When a summon is destroyed, absorb its soul to gain a temporary stat boost.",PlayerClass.SUMMONER,10,0, 0,    SkillType.PASSIVE, SkillTag.BUFF));
        register(new AeternumSkill("summoner_ancient_dragon",   "Ancient Dragon",       "[ULTIMATE] Summon an ancient dragon for 60 seconds. The most powerful summon.",   PlayerClass.SUMMONER, 35, 100,600000,SkillType.ULTIMATE,SkillTag.SUMMON));

        // ========== DRUID SKILLS ==========
        register(new AeternumSkill("druid_bear_form",           "Bear Form",            "Transform into a bear: +80% HP, +40% defense, powerful melee attacks.",           PlayerClass.DRUID,    1,  30, 15000, SkillType.ACTIVE,  SkillTag.BUFF,    SkillTag.NATURE));
        register(new AeternumSkill("druid_cat_form",            "Cat Form",             "Transform into a cat: +60% speed, stealth, and claw attacks.",                    PlayerClass.DRUID,    3,  25, 12000, SkillType.ACTIVE,  SkillTag.BUFF,    SkillTag.STEALTH, SkillTag.NATURE));
        register(new AeternumSkill("druid_entangle",            "Entangle",             "Roots all enemies in an area with magical vines for 4 seconds.",                  PlayerClass.DRUID,    5,  35, 15000, SkillType.ACTIVE,  SkillTag.CONTROL, SkillTag.NATURE, SkillTag.AOE));
        register(new AeternumSkill("druid_regrowth",            "Regrowth",             "Apply a healing-over-time effect that restores HP for 20 seconds.",               PlayerClass.DRUID,    3,  25, 10000, SkillType.ACTIVE,  SkillTag.HEAL,    SkillTag.NATURE));
        register(new AeternumSkill("druid_hurricane",           "Hurricane",            "[ULTIMATE] Summon a hurricane, knocking back and dealing wind damage to all enemies in a massive area.", PlayerClass.DRUID, 25, 100, 180000, SkillType.ULTIMATE, SkillTag.NATURE, SkillTag.AOE));
        register(new AeternumSkill("druid_moonfire",            "Moonfire",             "Call down lunar energy on a target dealing massive magic damage and applying a slow.", PlayerClass.DRUID, 10, 45, 10000, SkillType.ACTIVE, SkillTag.MAGIC, SkillTag.NATURE));

        // ========== CLERIC SKILLS ==========
        register(new AeternumSkill("cleric_heal",               "Heal",                 "Restore 30% max HP to target ally.",                                              PlayerClass.CLERIC,   1,  25, 5000,  SkillType.ACTIVE,  SkillTag.HEAL,    SkillTag.HOLY));
        register(new AeternumSkill("cleric_holy_nova",          "Holy Nova",            "Release a burst of holy energy healing all allies and damaging undead nearby.",   PlayerClass.CLERIC,   5,  40, 10000, SkillType.ACTIVE,  SkillTag.HEAL,    SkillTag.HOLY, SkillTag.AOE));
        register(new AeternumSkill("cleric_divine_protection",  "Divine Protection",    "Grant an ally a divine shield absorbing the next 3 hits.",                        PlayerClass.CLERIC,   8,  40, 20000, SkillType.ACTIVE,  SkillTag.BUFF,    SkillTag.HOLY));
        register(new AeternumSkill("cleric_smite",              "Smite",                "Channel divine wrath to deal heavy holy damage to a single enemy.",               PlayerClass.CLERIC,   3,  25, 6000,  SkillType.ACTIVE,  SkillTag.MAGIC,   SkillTag.HOLY));
        register(new AeternumSkill("cleric_mass_resurrection",  "Mass Resurrection",    "[ULTIMATE] Resurrect all fallen allies within 30 blocks with 50% HP.",            PlayerClass.CLERIC,   35, 100,600000,SkillType.ULTIMATE,SkillTag.HEAL,    SkillTag.HOLY, SkillTag.AOE));

        // ========== MONK SKILLS ==========
        register(new AeternumSkill("monk_tiger_palm",           "Tiger Palm",           "Swift palm strike dealing 120% damage. Reduces next skill cooldown by 50%.",      PlayerClass.MONK,     1,  15, 4000,  SkillType.ACTIVE,  SkillTag.MELEE));
        register(new AeternumSkill("monk_spinning_crane_kick",  "Spinning Crane Kick",  "Kick in a circle hitting all enemies around you for 150% damage.",                PlayerClass.MONK,     3,  25, 8000,  SkillType.ACTIVE,  SkillTag.MELEE,   SkillTag.AOE));
        register(new AeternumSkill("monk_iron_skin",            "[PASSIVE] Iron Skin",  "Reduce all physical damage by 15% when not wearing heavy armor.",                 PlayerClass.MONK,     5,  0,  0,     SkillType.PASSIVE, SkillTag.BUFF));
        register(new AeternumSkill("monk_thousand_fists",       "Thousand Fists",       "[ULTIMATE] Deliver 20 rapid strikes over 3 seconds, each dealing 80% damage.",   PlayerClass.MONK,     20, 60, 120000,SkillType.ULTIMATE,SkillTag.MELEE));
        register(new AeternumSkill("monk_chi_burst",            "Chi Burst",            "Launch a wave of chi energy that heals allies and damages enemies it passes through.", PlayerClass.MONK, 8, 35, 12000, SkillType.ACTIVE, SkillTag.MELEE, SkillTag.HEAL));
        register(new AeternumSkill("monk_transcendence",        "Transcendence",        "Enter a meditative state: rapidly regenerate HP and Energy for 10 seconds.",      PlayerClass.MONK,     12, 0,  45000, SkillType.ACTIVE,  SkillTag.BUFF,    SkillTag.HEAL));

        // ========== BARD SKILLS ==========
        register(new AeternumSkill("bard_song_of_war",          "Song of War",          "Play a battle anthem increasing all allies' damage by 30% for 30s.",             PlayerClass.BARD,     1,  30, 40000, SkillType.ACTIVE,  SkillTag.BUFF,    SkillTag.AOE));
        register(new AeternumSkill("bard_dissonance",           "Dissonance",           "Play a cacophonous melody that confuses enemies, causing them to attack randomly.",PlayerClass.BARD,     5,  35, 20000, SkillType.ACTIVE,  SkillTag.DEBUFF,  SkillTag.AOE));
        register(new AeternumSkill("bard_healing_hymn",         "Healing Hymn",         "Sing a restorative song that heals all allies for 5s.",                          PlayerClass.BARD,     3,  40, 25000, SkillType.ACTIVE,  SkillTag.HEAL,    SkillTag.AOE));
        register(new AeternumSkill("bard_ballad_of_heroes",     "Ballad of Heroes",     "[ULTIMATE] Inspire your entire team. All allies gain all stats +50% for 45s.",   PlayerClass.BARD,     25, 100,300000,SkillType.ULTIMATE,SkillTag.BUFF,    SkillTag.AOE));

        // ========== SHARED / UNIVERSAL SKILLS (any class can unlock) ==========
        register(new AeternumSkill("shared_second_wind",        "Second Wind",          "Recover 25% of max HP instantly. Usable once every 3 minutes.",                  null,                 1,  0,  180000,SkillType.ACTIVE,  SkillTag.HEAL));
        register(new AeternumSkill("shared_sprint",             "Sprint",               "[PASSIVE] Increase movement speed by 15% when at full stamina.",                  null,                 1,  0,  0,     SkillType.PASSIVE, SkillTag.MOVEMENT));
        register(new AeternumSkill("shared_iron_will",          "Iron Will",            "[PASSIVE] Immune to stun effects. Reduces all CC duration by 50%.",              null,                 20, 0,  0,     SkillType.PASSIVE, SkillTag.BUFF));
        register(new AeternumSkill("shared_treasure_sense",     "Treasure Sense",       "[PASSIVE] Detect nearby chests, ores, and buried treasures.",                    null,                 10, 0,  0,     SkillType.PASSIVE, SkillTag.UTILITY));
        register(new AeternumSkill("shared_berserk_strike",     "Berserk Strike",       "A raw, powerful melee hit dealing 200% damage. No class restriction.",           null,                 5,  30, 15000, SkillType.ACTIVE,  SkillTag.MELEE));
        register(new AeternumSkill("shared_divine_blessing",    "Divine Blessing",      "Requires high karma. Call upon divine favor, instantly healing and removing debuffs.", null, 15, 50, 60000, SkillType.ACTIVE, SkillTag.HEAL, SkillTag.HOLY));
        register(new AeternumSkill("shared_shadow_veil",        "Shadow Veil",          "Requires negative karma. Wrap yourself in shadows, reducing detection for 20s.", null, 10, 30, 30000, SkillType.ACTIVE, SkillTag.STEALTH, SkillTag.DARK));

        // ========== CELESTIAL KNIGHT SKILLS ==========
        register(new AeternumSkill("celestial_starfall",        "Starfall",             "Call down a rain of celestial stars on all enemies in a massive area.",           PlayerClass.CELESTIAL_KNIGHT, 20, 80, 120000, SkillType.ACTIVE, SkillTag.HOLY, SkillTag.AOE));
        register(new AeternumSkill("celestial_wings",           "Celestial Wings",      "Grow divine wings. Gain flight for 60 seconds.",                                  PlayerClass.CELESTIAL_KNIGHT, 5,  50, 90000, SkillType.ACTIVE, SkillTag.MOVEMENT, SkillTag.HOLY));
        register(new AeternumSkill("celestial_divine_wrath",    "Divine Wrath",         "[ULTIMATE] Channel the wrath of the heavens. Devastating holy explosion visible from far away.", PlayerClass.CELESTIAL_KNIGHT, 30, 100, 600000, SkillType.ULTIMATE, SkillTag.HOLY, SkillTag.AOE));

        // ========== VOID WALKER SKILLS ==========
        register(new AeternumSkill("void_phase",                "Phase",                "Phase out of reality for 3 seconds. Immune to all damage. Cannot act.",           PlayerClass.VOID_WALKER, 5, 40, 20000, SkillType.ACTIVE, SkillTag.MOVEMENT));
        register(new AeternumSkill("void_singularity",          "Singularity",          "Create a gravitational singularity that pulls all enemies toward a point.",       PlayerClass.VOID_WALKER, 15, 70, 45000, SkillType.ACTIVE, SkillTag.CONTROL, SkillTag.AOE));
        register(new AeternumSkill("void_annihilation",         "Annihilation",         "[ULTIMATE] Erase a target from existence for 10 seconds. They cannot act or be affected.", PlayerClass.VOID_WALKER, 30, 100, 300000, SkillType.ULTIMATE, SkillTag.CONTROL));

        // ========== DRAGON KNIGHT SKILLS ==========
        register(new AeternumSkill("dknight_fire_breath",       "Dragon Breath",        "Breathe a cone of fire dealing massive damage over 3 seconds.",                   PlayerClass.DRAGON_KNIGHT, 1, 40, 15000, SkillType.ACTIVE, SkillTag.FIRE, SkillTag.AOE));
        register(new AeternumSkill("dknight_dragon_scales",     "Dragon Scales",        "[PASSIVE] Your skin hardens. +25% physical defense and fire immunity.",           PlayerClass.DRAGON_KNIGHT, 5, 0, 0, SkillType.PASSIVE, SkillTag.BUFF));
        register(new AeternumSkill("dknight_ancient_roar",      "Ancient Roar",         "[ULTIMATE] Let out a dragon's roar. Terrify all enemies in the world, giving massive debuffs.", PlayerClass.DRAGON_KNIGHT, 30, 100, 600000, SkillType.ULTIMATE, SkillTag.AOE, SkillTag.DEBUFF));
    }

    private static void register(AeternumSkill skill) {
        ALL_SKILLS.put(skill.getId(), skill);
    }

    public static AeternumSkill getSkill(String id) {
        return ALL_SKILLS.get(id);
    }

    public static List<AeternumSkill> getSkillsForClass(PlayerClass cls) {
        List<AeternumSkill> result = new ArrayList<>();
        for (AeternumSkill skill : ALL_SKILLS.values()) {
            if (skill.getRequiredClass() == null || skill.getRequiredClass() == cls) {
                result.add(skill);
            }
        }
        return result;
    }

    /**
     * Try to use a skill. Returns true if successful.
     */
    public static boolean useSkill(ServerPlayer player, PlayerData data, String skillId) {
        AeternumSkill skill = getSkill(skillId);
        if (skill == null) return false;

        if (!data.hasSkill(skillId)) {
            player.sendSystemMessage(Component.literal("§cYou haven't learned that skill."));
            return false;
        }

        long remaining = data.getSkillCooldownRemaining(skillId);
        if (remaining > 0) {
            player.sendSystemMessage(Component.literal("§eSkill on cooldown: " + (remaining / 1000) + "s remaining."));
            return false;
        }

        if (!data.consumeEnergy(skill.getEnergyCost())) {
            player.sendSystemMessage(Component.literal("§bNot enough energy!"));
            return false;
        }

        // Apply cooldown
        data.setSkillCooldown(skillId, skill.getCooldownMs());

        // Execute skill effect
        executeSkill(player, data, skill);
        return true;
    }

    private static void executeSkill(ServerPlayer player, PlayerData data, AeternumSkill skill) {
        switch (skill.getId()) {
            case "warrior_shield_bash" -> applyStunToTarget(player, 2000);
            case "warrior_battle_cry" -> applyCryBuff(player);
            case "warrior_whirlwind" -> whirlwindAttack(player, data);
            case "warrior_titan_rage" -> applyTitanRage(player, data);
            case "paladin_lay_on_hands" -> healTarget(player, data, 0.40);
            case "paladin_divine_shield" -> applyDivineShield(player, 5000);
            case "mage_fireball" -> launchFireball(player, data);
            case "mage_blink" -> teleportForward(player, 20);
            case "mage_time_stop" -> freezeArea(player, 30, 5000);
            case "necro_raise_dead" -> raiseDeadNearby(player, data);
            case "druid_bear_form" -> transformBearForm(player, data);
            case "shared_second_wind" -> { data.heal(data.getMaxHealth() * 0.25); }
            case "shared_divine_blessing" -> {
                if (data.getKarma() >= 2000) {
                    data.heal(data.getMaxHealth() * 0.5);
                    player.removeAllEffects();
                } else {
                    player.sendSystemMessage(Component.literal("§eYour karma is too low for divine blessing."));
                    data.setCurrentEnergy(data.getCurrentEnergy() + 50); // refund
                }
            }
            default -> player.sendSystemMessage(Component.literal("§a[" + skill.getDisplayName() + "] activated!"));
        }
    }

    // ========== SKILL EFFECT HELPERS ==========
    private static void applyStunToTarget(ServerPlayer player, long durationMs) {
        LivingEntity target = getTarget(player);
        if (target != null) {
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, (int)(durationMs / 50), 255));
            player.sendSystemMessage(Component.literal("§aShield Bash! Target stunned!"));
        }
    }

    private static void applyCryBuff(ServerPlayer player) {
        // Buff all nearby players
        player.level().getEntitiesOfClass(ServerPlayer.class,
            player.getBoundingBox().inflate(15)).forEach(p ->
            p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 300, 1)));
        player.sendSystemMessage(Component.literal("§6Battle Cry! Allies empowered!"));
    }

    private static void whirlwindAttack(ServerPlayer player, PlayerData data) {
        player.level().getEntitiesOfClass(LivingEntity.class,
            player.getBoundingBox().inflate(3)).stream()
            .filter(e -> e != player)
            .forEach(e -> e.hurt(player.damageSources().playerAttack(player), (float)(data.getPhysicalAttack() * 1.8)));
        player.sendSystemMessage(Component.literal("§cWhirlwind!"));
    }

    private static void applyTitanRage(ServerPlayer player, PlayerData data) {
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 400, 3));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 2));
        player.sendSystemMessage(Component.literal("§4TITAN RAGE!"));
    }

    private static void healTarget(ServerPlayer player, PlayerData data, double percent) {
        data.heal(data.getMaxHealth() * percent);
        player.sendSystemMessage(Component.literal("§aHealed for " + (int)(data.getMaxHealth() * percent) + " HP!"));
    }

    private static void applyDivineShield(ServerPlayer player, long durationMs) {
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, (int)(durationMs / 50), 255));
        player.sendSystemMessage(Component.literal("§eDivine Shield activated!"));
    }

    private static void launchFireball(ServerPlayer player, PlayerData data) {
        // Spawn a fireball entity
        var fb = new net.minecraft.world.entity.projectile.SmallFireball(
            player.level(), player,
            player.getViewVector(1.0f).x,
            player.getViewVector(1.0f).y,
            player.getViewVector(1.0f).z);
        player.level().addFreshEntity(fb);
    }

    private static void teleportForward(ServerPlayer player, int blocks) {
        var look = player.getViewVector(1.0f).normalize().scale(blocks);
        player.teleportTo(player.getX() + look.x, player.getY() + look.y, player.getZ() + look.z);
    }

    private static void freezeArea(ServerPlayer player, double radius, long durationMs) {
        player.level().getEntitiesOfClass(LivingEntity.class,
            player.getBoundingBox().inflate(radius))
            .stream().filter(e -> e != player)
            .forEach(e -> e.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, (int)(durationMs / 50), 255)));
        player.sendSystemMessage(Component.literal("§bTime Stop!"));
    }

    private static void raiseDeadNearby(ServerPlayer player, PlayerData data) {
        // Find a skeleton/zombie spot and summon zombie
        var zombie = new net.minecraft.world.entity.monster.Zombie(player.level());
        zombie.moveTo(player.getX(), player.getY(), player.getZ(), 0, 0);
        player.level().addFreshEntity(zombie);
        data.addTamedEntity(zombie.getUUID());
        player.sendSystemMessage(Component.literal("§2Raised a zombie!"));
    }

    private static void transformBearForm(ServerPlayer player, PlayerData data) {
        data.setMaxHealth(data.getMaxHealth() * 1.8);
        data.heal(data.getMaxHealth() * 0.3);
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 1));
        player.sendSystemMessage(Component.literal("§6Bear Form!"));
    }

    private static LivingEntity getTarget(ServerPlayer player) {
        var result = player.level().getEntitiesOfClass(LivingEntity.class,
            player.getBoundingBox().inflate(5).move(player.getViewVector(1.0f).scale(3)));
        return result.stream().filter(e -> e != player).findFirst().orElse(null);
    }
}

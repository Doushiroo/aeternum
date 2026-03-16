package com.aeternum.systems.classes;

/**
 * All available player classes in Aeternum.
 * Each class has a unique playstyle and skill tree.
 * Players choose their class and customize it through skill allocation.
 */
public enum PlayerClass {

    // === DEFAULT ===
    WANDERER("Wanderer", "The beginner class. Balanced stats with no specialization.", ClassType.BALANCED, false),

    // === MELEE CLASSES ===
    WARRIOR("Warrior", "Master of melee combat. High defense and health.", ClassType.MELEE, false),
    BERSERKER("Berserker", "Sacrifices defense for devastating attack power. Rage fuels their power.", ClassType.MELEE, false),
    PALADIN("Paladin", "Holy warrior who balances offense with healing and light magic. Favored by the Divine.", ClassType.HYBRID, false),
    SHADOW_KNIGHT("Shadow Knight", "Dark warrior who draws power from corruption. Commands dark energy and drains life.", ClassType.HYBRID, true),
    MONK("Monk", "Disciplines the body to its limit. Lightning-fast combos and iron defense without armor.", ClassType.MELEE, false),
    KNIGHT_OF_LIGHT("Knight of Light", "Divine champion. Supreme defense, holy auras, and the favor of celestial beings.", ClassType.MELEE, false),

    // === RANGED CLASSES ===
    RANGER("Ranger", "Expert archer and tracker. High mobility, traps, and precision shots.", ClassType.RANGED, false),
    ASSASSIN("Assassin", "Master of stealth and critical strikes. Kills from the shadows before the enemy reacts.", ClassType.RANGED, false),
    HUNTER("Hunter", "Tames and fights alongside powerful beasts. Bond with creatures enhances all abilities.", ClassType.RANGED, false),

    // === MAGIC CLASSES ===
    MAGE("Mage", "Unleashes raw elemental magic. Glass cannon with immense destructive potential.", ClassType.MAGIC, false),
    WARLOCK("Warlock", "Pacts with dark forces grant incredible power. Corruption has a price.", ClassType.MAGIC, true),
    DRUID("Druid", "Shapeshifter attuned to nature. Controls terrain, summons animals, and heals with earth magic.", ClassType.MAGIC, false),
    CLERIC("Cleric", "Holy healer and support. Divine intervention, buffs, and the power to repel darkness.", ClassType.MAGIC, false),
    BARD("Bard", "Master of inspiration and debilitation. Music-based magic that buffs allies and destroys enemies.", ClassType.MAGIC, false),

    // === SUMMONER CLASSES ===
    NECROMANCER("Necromancer", "Raises the dead to fight. Commands armies of undead, drains life, and corrupts death itself.", ClassType.SUMMONER, true),
    SUMMONER("Summoner", "Binds elemental and magical creatures to do their bidding. The more enemies fall, the stronger they become.", ClassType.SUMMONER, false),
    BLOOD_MAGE("Blood Mage", "Uses own health as a resource for catastrophic magical power. Risk everything for power.", ClassType.MAGIC, true),

    // === SUPPORT/UTILITY CLASSES ===
    ALCHEMIST("Alchemist", "Master of potions and transmutations. Buffs, debuffs, and battlefield control through chemistry.", ClassType.UTILITY, false),
    ENGINEER("Engineer", "Crafts mechanical contraptions for combat. Turrets, bombs, and complex machines.", ClassType.UTILITY, false),

    // === SPECIAL CLASSES (unlocked via quests/achievements) ===
    CELESTIAL_KNIGHT("Celestial Knight", "★ Rare. Forged through divine trial. Commands both light and stellar magic. Radiates divine aura.", ClassType.SPECIAL, false),
    VOID_WALKER("Void Walker", "★ Rare. Torn between dimensions. Phases through reality, warps space, and wields void energy.", ClassType.SPECIAL, false),
    CHAOS_LORD("Chaos Lord", "★ Rare. Pure entropy incarnate. Every battle is unpredictable — impossibly powerful chaos.", ClassType.SPECIAL, true),
    DRAGON_KNIGHT("Dragon Knight", "★ Rare. Bonded with a dragon spirit. Breathes fire, grows scales, gains draconic senses.", ClassType.SPECIAL, false);

    private final String displayName;
    private final String description;
    private final ClassType type;
    private final boolean darkAligned;

    PlayerClass(String displayName, String description, ClassType type, boolean darkAligned) {
        this.displayName = displayName;
        this.description = description;
        this.type = type;
        this.darkAligned = darkAligned;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public ClassType getType() { return type; }
    public boolean isDarkAligned() { return darkAligned; }
    public boolean isSpecial() { return type == ClassType.SPECIAL; }

    /**
     * Minimum karma required to take this class.
     * Dark-aligned classes require negative karma to unlock.
     */
    public int getKarmaRequirement() {
        return switch (this) {
            case SHADOW_KNIGHT, WARLOCK, BLOOD_MAGE -> -1000;
            case NECROMANCER -> -500;
            case CHAOS_LORD -> -3000;
            case PALADIN, CLERIC, KNIGHT_OF_LIGHT -> 500;
            case CELESTIAL_KNIGHT -> 5000;
            default -> 0; // No karma requirement
        };
    }

    /**
     * Classes that require special quest completion.
     */
    public boolean requiresQuest() {
        return switch (this) {
            case CELESTIAL_KNIGHT, VOID_WALKER, CHAOS_LORD, DRAGON_KNIGHT -> true;
            default -> false;
        };
    }

    public enum ClassType {
        MELEE, RANGED, MAGIC, SUMMONER, HYBRID, UTILITY, BALANCED, SPECIAL
    }
}

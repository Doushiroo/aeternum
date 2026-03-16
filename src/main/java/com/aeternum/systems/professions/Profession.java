package com.aeternum.systems.professions;

import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║              PROFESSION SYSTEM — AETERNUM                   ║
 * ╠══════════════════════════════════════════════════════════════╣
 * ║  12 profesiones completamente desarrolladas.                ║
 * ║  Cada profesión tiene 10 niveles de maestría.               ║
 * ║  Los jugadores tienen una profesión primaria y pueden        ║
 * ║  tener una secundaria (al ser Noble).                       ║
 * ║  Cada nivel desbloquea recetas, bonos y habilidades únicas. ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
public enum Profession {

    NONE("None", "No profession selected.", ProfessionCategory.NONE,
        List.of()),

    // ════════ CRAFTING PROFESSIONS ════════
    BLACKSMITH("Blacksmith", "Master of metals. Forges weapons and armor of legendary quality.",
        ProfessionCategory.CRAFTING,
        List.of(
            new ProfessionLevel(1,  "Apprentice Smith",    "Can craft basic iron tools and weapons."),
            new ProfessionLevel(2,  "Iron Smith",          "Unlocks steel alloys. Tools last 30% longer."),
            new ProfessionLevel(3,  "Steel Artisan",       "Can repair any equipment. Quality bonus on crafted items."),
            new ProfessionLevel(4,  "Master Forger",       "Unlocks rare metal smelting (Mythril, Adamant)."),
            new ProfessionLevel(5,  "Weaponsmith",         "Weapons crafted deal +10% damage. Unlock enchant slots on craft."),
            new ProfessionLevel(6,  "Armorsmith",          "Armor crafted has +15% durability and defense bonus."),
            new ProfessionLevel(7,  "Runesmith",           "Can inscribe runes on equipment for special effects."),
            new ProfessionLevel(8,  "Grand Smith",         "Craft sets with unique set bonuses. Materials cost -25%."),
            new ProfessionLevel(9,  "Legendary Artisan",   "All crafted weapons can have a unique name and glow."),
            new ProfessionLevel(10, "Divine Forgemaster",  "Can craft Divine-tier equipment. Items gain a soul upon craft.")
        )),

    ALCHEMIST("Alchemist", "Transmutes materials and brews powerful concoctions.",
        ProfessionCategory.CRAFTING,
        List.of(
            new ProfessionLevel(1,  "Apprentice Alchemist","Basic potions: health, energy, speed."),
            new ProfessionLevel(2,  "Herbalist",           "Identify and brew from wild herbs. Potions last 50% longer."),
            new ProfessionLevel(3,  "Transmuter",          "Convert one material to another (with loss). Basic transmutation."),
            new ProfessionLevel(4,  "Potion Master",       "Brew advanced potions: strength, defense, invisibility."),
            new ProfessionLevel(5,  "Explosive Expert",    "Craft alchemical bombs and throwables. Area denial."),
            new ProfessionLevel(6,  "Catalyst Refiner",    "Extract rare catalysts from boss drops. Enhance potions."),
            new ProfessionLevel(7,  "Homunculus Maker",    "Create living constructs that assist in crafting."),
            new ProfessionLevel(8,  "Grand Alchemist",     "Philosopher's Stone: multiply materials. Legendary potions."),
            new ProfessionLevel(9,  "Elixir Sage",         "Permanent stat elixirs. Effects that persist beyond respawn."),
            new ProfessionLevel(10, "Grand Sage of Matter","Transmute anything. Create items that don't exist in the world.")
        )),

    ENCHANTER("Enchanter", "Imbues equipment with magical power beyond normal enchanting.",
        ProfessionCategory.CRAFTING,
        List.of(
            new ProfessionLevel(1,  "Rune Carver",         "Apply basic enchantments without an anvil."),
            new ProfessionLevel(2,  "Scroll Writer",       "Create enchantment scrolls for others to use."),
            new ProfessionLevel(3,  "Essence Extractor",   "Extract enchantments from gear without destroying it."),
            new ProfessionLevel(4,  "Gem Setter",          "Socket gems into equipment for special bonuses."),
            new ProfessionLevel(5,  "Dual Enchanter",      "Apply two conflicting enchantments to one item."),
            new ProfessionLevel(6,  "Elemental Binder",    "Bind elements (fire, ice, lightning) to weapons permanently."),
            new ProfessionLevel(7,  "Cursed Inscriber",    "Apply curse enchantments with double the power."),
            new ProfessionLevel(8,  "Astral Enchanter",    "Enchantments have a 10% chance to level up after kills."),
            new ProfessionLevel(9,  "Divine Inscriber",    "Add divine enchantments only usable by good karma players."),
            new ProfessionLevel(10, "Master Runekeeper",   "Create Legendary-tier enchantments. Items gain sentience.")
        )),

    CARPENTER("Carpenter", "Builder and craftsman of structures, furniture, and traps.",
        ProfessionCategory.CRAFTING,
        List.of(
            new ProfessionLevel(1,  "Wood Cutter",         "Enhanced axes. Extra wood drops."),
            new ProfessionLevel(2,  "Basic Builder",       "Craft scaffolding and basic furniture."),
            new ProfessionLevel(3,  "Trap Setter",         "Craft mechanical traps for defense."),
            new ProfessionLevel(4,  "Fortifier",           "Build reinforced walls. Structures take 30% less damage."),
            new ProfessionLevel(5,  "Catapult Engineer",   "Build siege weapons for castle sieges."),
            new ProfessionLevel(6,  "Dungeon Architect",   "Design dungeon-like structures with automated defenses."),
            new ProfessionLevel(7,  "Naval Builder",       "Construct advanced ships. Faster and more durable."),
            new ProfessionLevel(8,  "Master Constructor",  "Build clan halls and territory structures."),
            new ProfessionLevel(9,  "Grand Architect",     "Unique aesthetics. Constructions can buff nearby players."),
            new ProfessionLevel(10, "World Builder",       "Your structures become part of the world lore.")
        )),

    // ════════ GATHERING PROFESSIONS ════════
    MINER("Miner", "Master of underground resources. Finds what others cannot.",
        ProfessionCategory.GATHERING,
        List.of(
            new ProfessionLevel(1,  "Pit Worker",          "Basic mining boost. 10% chance of double ore."),
            new ProfessionLevel(2,  "Ore Finder",          "Detect ore veins within 10 blocks."),
            new ProfessionLevel(3,  "Cave Delver",         "Immune to fall damage underground. Night vision in caves."),
            new ProfessionLevel(4,  "Gem Hunter",          "Significantly increased gem drop rate."),
            new ProfessionLevel(5,  "Deep Miner",          "Mine in the Deepslate layer 50% faster."),
            new ProfessionLevel(6,  "Crystal Extractor",   "Extract crystals and magical stones others cannot see."),
            new ProfessionLevel(7,  "Void Miner",          "Access void-layer resources with special void pickaxe."),
            new ProfessionLevel(8,  "Grand Prospector",    "Map underground veins. Share maps with clan."),
            new ProfessionLevel(9,  "Mythril Master",      "Only profession that can mine Mythril and Adamantite."),
            new ProfessionLevel(10, "World Geomancer",     "Sense any underground structure. Summon ore veins.")
        )),

    FARMER("Farmer", "Master of agriculture and animal husbandry.",
        ProfessionCategory.GATHERING,
        List.of(
            new ProfessionLevel(1,  "Peasant",             "Crops grow 20% faster. Basic animal breeding."),
            new ProfessionLevel(2,  "Cultivator",          "Unlock special crops: magic herbs, spice plants."),
            new ProfessionLevel(3,  "Animal Keeper",       "Animals produce 50% more. Breed rare animals."),
            new ProfessionLevel(4,  "Herbalist",           "Grow special herbs used in high-tier alchemy."),
            new ProfessionLevel(5,  "Ranch Master",        "Manage large herds. Produce special foods with stat buffs."),
            new ProfessionLevel(6,  "Mystical Cultivator", "Grow plants that don't normally exist. Magic crops."),
            new ProfessionLevel(7,  "Creature Breeder",    "Breed monsters. Farm drops from friendly monster farms."),
            new ProfessionLevel(8,  "Grand Rancher",       "Products sell for 3x market value."),
            new ProfessionLevel(9,  "Nature's Chosen",     "Crops grow in any biome, any condition. No seasons stop you."),
            new ProfessionLevel(10, "Primordial Farmer",   "Grow Yggdrasil seeds. Legendary materials from nature.")
        )),

    FISHER("Fisher", "Knows the secrets of the ocean. Master of aquatic resources.",
        ProfessionCategory.GATHERING,
        List.of(
            new ProfessionLevel(1,  "Fisherman",           "30% faster fishing. Rare fish appear more."),
            new ProfessionLevel(2,  "Pearl Diver",         "Dive deeper and longer. Find sea treasures."),
            new ProfessionLevel(3,  "Sea Trapper",         "Set underwater traps. Catch sea creatures."),
            new ProfessionLevel(4,  "Treasure Hunter",     "Find sunken ships and underwater ruins."),
            new ProfessionLevel(5,  "Leviathan Wrangler",  "Can fish in boss waters safely. Catch boss-tier fish."),
            new ProfessionLevel(6,  "Ocean Sage",          "Understand sea creature language (taming bonus)."),
            new ProfessionLevel(7,  "Abyssal Fisher",      "Fish in the abyss for unique deep-sea materials."),
            new ProfessionLevel(8,  "Sea Merchant",        "Trade with sea creatures. Bartering for rare items."),
            new ProfessionLevel(9,  "Titan Fisherman",     "Attempt to catch sea titans. Legendary fishing events."),
            new ProfessionLevel(10, "God of the Sea",      "Can summon and dismiss sea creatures. Ocean obeys you.")
        )),

    // ════════ COMBAT PROFESSIONS ════════
    BOUNTY_HUNTER("Bounty Hunter", "Tracks and eliminates targets for reward. Gains bonus from killing.",
        ProfessionCategory.COMBAT,
        List.of(
            new ProfessionLevel(1,  "Tracker",             "See footprints and trails of nearby players/mobs."),
            new ProfessionLevel(2,  "Scout",               "Mark a target. Always know their general direction."),
            new ProfessionLevel(3,  "Manhunter",           "Contracts from the bounty board. Earn reward for targets."),
            new ProfessionLevel(4,  "Assassin's Path",     "Poisons and traps deal +25% damage."),
            new ProfessionLevel(5,  "Headhunter",          "Collect trophies from boss kills for extra rewards."),
            new ProfessionLevel(6,  "Blood Money",         "+50% Aurum from all player kills. Notoriety increases."),
            new ProfessionLevel(7,  "Elite Hunter",        "Can track players across dimensions."),
            new ProfessionLevel(8,  "Grand Slayer",        "Immunity to fear effects. +30% damage to marked targets."),
            new ProfessionLevel(9,  "Legendary Bounty",    "Take contracts to kill world bosses alone. Huge rewards."),
            new ProfessionLevel(10, "Death's Agent",       "Target marked for death cannot teleport or hide. Death finds all.")
        )),

    MERCENARY("Mercenary", "Sells combat skills to the highest bidder. Works for clans as hired muscle.",
        ProfessionCategory.COMBAT,
        List.of(
            new ProfessionLevel(1,  "Hired Sword",         "Can register as mercenary for clan wars."),
            new ProfessionLevel(2,  "Versatile Warrior",   "Use any weapon type without penalty."),
            new ProfessionLevel(3,  "Siege Specialist",    "Deal +40% damage to castle gates and structures."),
            new ProfessionLevel(4,  "Veteran Combatant",   "+20% damage when outnumbered."),
            new ProfessionLevel(5,  "Battle-Hardened",     "After 5 minutes in combat, gain combat resilience stacks."),
            new ProfessionLevel(6,  "War Strategist",      "Buff allied players around you with war presence."),
            new ProfessionLevel(7,  "Commander",           "Lead NPC mercenary troops in clan wars."),
            new ProfessionLevel(8,  "Elite Mercenary",     "Command price triples. Buff entire clan in war."),
            new ProfessionLevel(9,  "Warlord for Hire",    "Negotiate war terms as mercenary leader."),
            new ProfessionLevel(10, "Grand War Master",    "Your presence on a battlefield shifts the tide. All stats maxed in war.")
        )),

    // ════════ SOCIAL PROFESSIONS ════════
    MERCHANT("Merchant", "Master of trade and economics. Gets the best deals and earns the most.",
        ProfessionCategory.SOCIAL,
        List.of(
            new ProfessionLevel(1,  "Street Vendor",       "Set up a personal shop anywhere."),
            new ProfessionLevel(2,  "Haggler",             "NPC prices 10% lower. Sell prices 10% higher."),
            new ProfessionLevel(3,  "Market Analyst",      "See market trends. Know when to buy and sell."),
            new ProfessionLevel(4,  "Trader",              "Bulk discounts on player-to-player trades."),
            new ProfessionLevel(5,  "Caravan Master",      "Transport goods between territories for bonuses."),
            new ProfessionLevel(6,  "Tax Manipulator",     "Reduce your tax burden by 15% in owned territories."),
            new ProfessionLevel(7,  "Market Monopolist",   "Temporarily control prices of one commodity."),
            new ProfessionLevel(8,  "Trade Empire",        "Clan members get your trade bonuses."),
            new ProfessionLevel(9,  "Economic Overlord",   "Set up a personal bank branch in your territory."),
            new ProfessionLevel(10, "God of Commerce",     "Economic control. Can crash or inflate server markets temporarily.")
        )),

    DIPLOMAT("Diplomat", "Masters of negotiation and politics. Reduces friction and creates alliances.",
        ProfessionCategory.SOCIAL,
        List.of(
            new ProfessionLevel(1,  "Spokesperson",        "Speak to hostile mobs briefly without them attacking."),
            new ProfessionLevel(2,  "Negotiator",          "Reduce tax rates in territory by negotiation."),
            new ProfessionLevel(3,  "Emissary",            "Deliver messages between clans with guaranteed safety."),
            new ProfessionLevel(4,  "Peace Broker",        "War declaration notice reduced. Peace terms more favorable."),
            new ProfessionLevel(5,  "Allied Commander",    "Coordinate between allied clans more effectively."),
            new ProfessionLevel(6,  "Spy Network",         "Detect infiltrators in your clan. Counter-espionage."),
            new ProfessionLevel(7,  "Kingmaker",           "Influence elections of clan leaders in non-hostile clans."),
            new ProfessionLevel(8,  "Grand Diplomat",      "Immune to war declarations for 24h per week."),
            new ProfessionLevel(9,  "World Mediator",      "Propose world peace events. Reduce server-wide conflict."),
            new ProfessionLevel(10, "Peacekeeper of Ages", "Your word holds law. Declare binding truces any clan must honor.")
        )),

    SCHOLAR("Scholar", "Master of knowledge. Unlocks hidden lore, buffs party through wisdom.",
        ProfessionCategory.SOCIAL,
        List.of(
            new ProfessionLevel(1,  "Student",             "+10% XP gain from all sources."),
            new ProfessionLevel(2,  "Historian",           "Access to lore books and world history."),
            new ProfessionLevel(3,  "Linguist",            "Read ancient texts. Translate mob languages."),
            new ProfessionLevel(4,  "Arcanist",            "Identify magic item properties without testing."),
            new ProfessionLevel(5,  "Lorekeeper",          "+25% XP gain. Share XP bonus with nearby party."),
            new ProfessionLevel(6,  "Monster Expert",      "Know weaknesses of every creature. Reveal boss patterns."),
            new ProfessionLevel(7,  "Ancient Knowledge",   "Unlock recipes lost to time. Access to secret content."),
            new ProfessionLevel(8,  "Grand Scholar",       "Research passive buffs for clan. Study grants stat boosts."),
            new ProfessionLevel(9,  "Oracle",              "See future world events. Predict boss spawns."),
            new ProfessionLevel(10, "Keeper of All Truth", "All knowledge is yours. No cooldowns on knowledge skills.")
        ));

    private final String displayName;
    private final String description;
    private final ProfessionCategory category;
    private final List<ProfessionLevel> levels;

    Profession(String displayName, String description, ProfessionCategory category, List<ProfessionLevel> levels) {
        this.displayName = displayName;
        this.description = description;
        this.category = category;
        this.levels = levels;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public ProfessionCategory getCategory() { return category; }
    public List<ProfessionLevel> getLevels() { return levels; }

    public ProfessionLevel getLevelData(int level) {
        int index = Math.max(0, Math.min(level - 1, levels.size() - 1));
        return levels.isEmpty() ? null : levels.get(index);
    }

    public long getXpForNextLevel(int currentLevel) {
        return (long)(500 * Math.pow(currentLevel, 1.5) + 200 * currentLevel);
    }

    public enum ProfessionCategory { CRAFTING, GATHERING, COMBAT, SOCIAL, NONE }

    public record ProfessionLevel(int level, String title, String bonusDescription) {}
}

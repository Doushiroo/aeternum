package com.aeternum.data;

import com.aeternum.systems.classes.PlayerClass;
import com.aeternum.systems.professions.Profession;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.*;

/**
 * Central data container for all Aeternum player stats.
 * Serializes to NBT and is stored via Data Attachment on the player entity.
 */
public class PlayerData implements INBTSerializable<CompoundTag> {

    // === LEVELING ===
    private int level = 1;
    private long experience = 0;
    private int skillPoints = 0;
    private int professionPoints = 0;

    // === VITAL STATS ===
    private double maxHealth = 100.0;
    private double currentHealth = 100.0;
    private double maxEnergy = 100.0;
    private double currentEnergy = 100.0;
    private double maxStamina = 100.0;
    private double currentStamina = 100.0;

    // === COMBAT ATTRIBUTES ===
    private double physicalAttack = 10.0;
    private double magicAttack = 10.0;
    private double physicalDefense = 5.0;
    private double magicDefense = 5.0;
    private double critChance = 0.05;      // 5% base
    private double critMultiplier = 1.5;   // 150% damage on crit
    private double dodgeChance = 0.03;
    private double blockChance = 0.05;
    private double attackSpeed = 1.0;
    private double movementSpeed = 1.0;

    // === CLASS SYSTEM ===
    private String playerClass = PlayerClass.WANDERER.name();
    private int classLevel = 1;
    private long classExperience = 0;
    private List<String> unlockedSkills = new ArrayList<>();
    private Map<String, Long> skillCooldowns = new HashMap<>();  // skill id -> last use millis
    private int[] attributeAllocation = new int[6]; // STR, AGI, INT, VIT, WIS, LUK

    // === PROFESSION SYSTEM ===
    private String primaryProfession = Profession.NONE.name();
    private String secondaryProfession = Profession.NONE.name();
    private Map<String, Integer> professionLevels = new HashMap<>();
    private Map<String, Long> professionXp = new HashMap<>();

    // === KARMA SYSTEM ===
    private int karma = 0;          // -10000 to +10000
    private int karmaDecayTimer = 0;
    private int totalKillsPlayers = 0;
    private int totalKillsMobs = 0;
    private int totalDeaths = 0;
    private int totalHelpActions = 0;

    // === ECONOMY ===
    private long bankBalance = 0;
    private long walletBalance = 500; // Starting currency (Aurum)
    private long totalTaxesPaid = 0;
    private long totalEarned = 0;

    // === CLAN ===
    private UUID clanId = null;
    private String clanRank = "RECRUIT";
    private boolean diplomaticImmunity = false;
    private long diplomaticImmunityExpiry = 0L;

    // === TERRITORY ===
    private UUID ownedTerritoryId = null;

    // === TITLES ===
    private String activeTitle = "";
    private List<String> unlockedTitles = new ArrayList<>();
    private Map<String, Map<String, Object>> titleEffects = new HashMap<>();

    // === TEMPERATURE ===
    private float bodyTemperature = 37.0f; // Celsius
    private float temperatureResistance = 0.0f;
    private int heatExhaustionTimer = 0;
    private int hypothermiaTimer = 0;
    private boolean hasFrostbite = false;

    // === EXPLORATION ===
    private long distanceTraveled = 0;
    private Set<String> discoveredBiomes = new HashSet<>();
    private int dimensionsVisited = 0;

    // === COMBAT STATS (for titles) ===
    private int totalPlayerKills = 0;
    private int highestDamageDealt = 0;
    private int bossesKilled = 0;

    // === TAMING ===
    private List<UUID> tamedEntities = new ArrayList<>();
    private int maxTamedEntities = 3;

    // === MISC FLAGS ===
    private boolean firstLogin = true;
    private long lastOnline = 0L;
    private int rebirthCount = 0; // Prestige system

    // ========== CONSTRUCTOR ==========
    public PlayerData() {}

    // ========== LEVELING METHODS ==========
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = Math.max(1, Math.min(100, level)); }
    public long getExperience() { return experience; }
    public void addExperience(long xp) { this.experience += xp; }

    public long getXpRequiredForNextLevel() {
        // Formula: base * level^2 + linear * level
        return (long)(150 * Math.pow(level, 1.8) + 200 * level);
    }

    public boolean canLevelUp() {
        return experience >= getXpRequiredForNextLevel() && level < 100;
    }

    public void levelUp() {
        if (!canLevelUp()) return;
        experience -= getXpRequiredForNextLevel();
        level++;
        skillPoints += 3;
        professionPoints += 1;
        // Scale stats per level
        maxHealth += 15.0;
        maxEnergy += 8.0;
        maxStamina += 5.0;
        physicalAttack += 1.5;
        magicAttack += 1.5;
        physicalDefense += 0.8;
        magicDefense += 0.8;
        currentHealth = maxHealth;
        currentEnergy = maxEnergy;
    }

    // ========== VITAL STAT METHODS ==========
    public double getMaxHealth() { return maxHealth; }
    public double getCurrentHealth() { return currentHealth; }
    public void setCurrentHealth(double h) { this.currentHealth = Math.max(0, Math.min(maxHealth, h)); }
    public void heal(double amount) { setCurrentHealth(currentHealth + amount); }
    public void damage(double amount) { setCurrentHealth(currentHealth - amount); }
    public boolean isDead() { return currentHealth <= 0; }
    public double getHealthPercent() { return currentHealth / maxHealth; }

    public double getMaxEnergy() { return maxEnergy; }
    public double getCurrentEnergy() { return currentEnergy; }
    public void setCurrentEnergy(double e) { this.currentEnergy = Math.max(0, Math.min(maxEnergy, e)); }
    public boolean consumeEnergy(double amount) {
        if (currentEnergy < amount) return false;
        currentEnergy -= amount;
        return true;
    }

    public double getMaxStamina() { return maxStamina; }
    public double getCurrentStamina() { return currentStamina; }
    public void setCurrentStamina(double s) { this.currentStamina = Math.max(0, Math.min(maxStamina, s)); }

    // ========== KARMA METHODS ==========
    public int getKarma() { return karma; }
    public void addKarma(int amount) { this.karma = Math.max(-10000, Math.min(10000, karma + amount)); }
    public KarmaLevel getKarmaLevel() {
        if (karma >= 8000) return KarmaLevel.DIVINE;
        if (karma >= 5000) return KarmaLevel.HOLY;
        if (karma >= 2000) return KarmaLevel.VIRTUOUS;
        if (karma >= 500) return KarmaLevel.GOOD;
        if (karma > -500) return KarmaLevel.NEUTRAL;
        if (karma > -2000) return KarmaLevel.SHADY;
        if (karma > -5000) return KarmaLevel.WICKED;
        if (karma > -8000) return KarmaLevel.CORRUPT;
        return KarmaLevel.ABYSSAL;
    }

    public enum KarmaLevel {
        DIVINE, HOLY, VIRTUOUS, GOOD, NEUTRAL, SHADY, WICKED, CORRUPT, ABYSSAL
    }

    // ========== CLASS METHODS ==========
    public PlayerClass getPlayerClass() {
        try { return PlayerClass.valueOf(playerClass); }
        catch (Exception e) { return PlayerClass.WANDERER; }
    }
    public void setPlayerClass(PlayerClass cls) {
        this.playerClass = cls.name();
        this.classLevel = 1;
        this.classExperience = 0;
        applyClassBaseStats(cls);
    }

    private void applyClassBaseStats(PlayerClass cls) {
        // Reset then apply class modifiers
        switch (cls) {
            case WARRIOR -> { physicalAttack += 10; physicalDefense += 8; maxHealth += 50; }
            case PALADIN -> { physicalAttack += 5; physicalDefense += 10; maxHealth += 40; magicAttack += 5; }
            case BERSERKER -> { physicalAttack += 20; critChance += 0.05; maxHealth += 30; physicalDefense -= 2; }
            case RANGER -> { physicalAttack += 8; dodgeChance += 0.08; movementSpeed += 0.1; }
            case ASSASSIN -> { physicalAttack += 12; critChance += 0.10; dodgeChance += 0.10; maxHealth -= 10; }
            case MAGE -> { magicAttack += 25; maxEnergy += 50; maxHealth -= 15; physicalDefense -= 3; }
            case WARLOCK -> { magicAttack += 20; maxEnergy += 30; critChance += 0.05; }
            case NECROMANCER -> { magicAttack += 15; maxEnergy += 40; maxTamedEntities += 5; }
            case SUMMONER -> { magicAttack += 10; maxEnergy += 60; maxTamedEntities += 8; }
            case DRUID -> { magicAttack += 10; physicalAttack += 5; maxHealth += 20; }
            case CLERIC -> { magicAttack += 8; magicDefense += 15; maxEnergy += 25; }
            case MONK -> { physicalAttack += 10; dodgeChance += 0.12; attackSpeed += 0.2; maxStamina += 30; }
            case ALCHEMIST -> { maxEnergy += 20; professionPoints += 5; }
            case BARD -> { movementSpeed += 0.05; magicAttack += 5; physicalDefense += 3; }
            case SHADOW_KNIGHT -> { physicalAttack += 15; magicAttack += 10; physicalDefense += 5; karma -= 200; }
            case KNIGHT_OF_LIGHT -> { physicalDefense += 15; magicDefense += 10; maxHealth += 60; karma += 200; }
            default -> {}
        }
    }

    public boolean hasSkill(String skillId) { return unlockedSkills.contains(skillId); }
    public void unlockSkill(String skillId) { if (!unlockedSkills.contains(skillId)) unlockedSkills.add(skillId); }
    public List<String> getUnlockedSkills() { return Collections.unmodifiableList(unlockedSkills); }

    public boolean isSkillOnCooldown(String skillId) {
        Long lastUse = skillCooldowns.get(skillId);
        if (lastUse == null) return false;
        // Cooldown is stored separately per skill in SkillRegistry
        return false; // Will be handled by SkillSystem
    }
    public void setSkillCooldown(String skillId, long cooldownMs) {
        skillCooldowns.put(skillId, System.currentTimeMillis() + cooldownMs);
    }
    public long getSkillCooldownRemaining(String skillId) {
        Long expiry = skillCooldowns.get(skillId);
        if (expiry == null) return 0;
        return Math.max(0, expiry - System.currentTimeMillis());
    }

    // ========== ATTRIBUTE ALLOCATION ==========
    // Indices: 0=STR, 1=AGI, 2=INT, 3=VIT, 4=WIS, 5=LUK
    public int[] getAttributeAllocation() { return attributeAllocation.clone(); }
    public int getSTR() { return attributeAllocation[0]; }
    public int getAGI() { return attributeAllocation[1]; }
    public int getINT() { return attributeAllocation[2]; }
    public int getVIT() { return attributeAllocation[3]; }
    public int getWIS() { return attributeAllocation[4]; }
    public int getLUK() { return attributeAllocation[5]; }

    public boolean allocateAttribute(int index, int points) {
        if (index < 0 || index > 5) return false;
        if (skillPoints < points) return false;
        attributeAllocation[index] += points;
        skillPoints -= points;
        recalculateFromAttributes();
        return true;
    }

    private void recalculateFromAttributes() {
        // STR increases physical attack
        physicalAttack = 10.0 + (getSTR() * 2.0);
        // AGI increases attack speed, dodge
        dodgeChance = 0.03 + (getAGI() * 0.002);
        attackSpeed = 1.0 + (getAGI() * 0.01);
        // INT increases magic attack
        magicAttack = 10.0 + (getINT() * 2.5);
        // VIT increases max HP
        maxHealth = 100.0 + (getVIT() * 10.0) + (level * 15.0);
        // WIS increases max energy, magic defense
        maxEnergy = 100.0 + (getWIS() * 5.0);
        magicDefense = 5.0 + (getWIS() * 1.0);
        // LUK increases crit chance
        critChance = 0.05 + (getLUK() * 0.003);
    }

    // ========== ECONOMY METHODS ==========
    public long getBankBalance() { return bankBalance; }
    public long getWalletBalance() { return walletBalance; }
    public void setBankBalance(long b) { this.bankBalance = Math.max(0, b); }
    public void setWalletBalance(long w) { this.walletBalance = Math.max(0, w); }
    public boolean payFromWallet(long amount) {
        if (walletBalance < amount) return false;
        walletBalance -= amount;
        totalTaxesPaid += 0; // updated separately
        return true;
    }
    public void receiveToWallet(long amount) { walletBalance += amount; totalEarned += amount; }

    // ========== CLAN METHODS ==========
    public UUID getClanId() { return clanId; }
    public void setClanId(UUID id) { this.clanId = id; }
    public boolean isInClan() { return clanId != null; }
    public String getClanRank() { return clanRank; }
    public void setClanRank(String rank) { this.clanRank = rank; }
    public boolean hasDiplomaticImmunity() {
        return diplomaticImmunity && System.currentTimeMillis() < diplomaticImmunityExpiry;
    }
    public void grantDiplomaticImmunity(long durationMs) {
        this.diplomaticImmunity = true;
        this.diplomaticImmunityExpiry = System.currentTimeMillis() + durationMs;
    }

    // ========== TEMPERATURE METHODS ==========
    public float getBodyTemperature() { return bodyTemperature; }
    public void setBodyTemperature(float t) { this.bodyTemperature = t; }
    public void adjustBodyTemperature(float delta) { this.bodyTemperature += delta; }
    public float getTemperatureResistance() { return temperatureResistance; }
    public void setTemperatureResistance(float r) { this.temperatureResistance = r; }

    public TemperatureStatus getTemperatureStatus() {
        if (bodyTemperature >= 42.0f) return TemperatureStatus.HEAT_STROKE;
        if (bodyTemperature >= 40.0f) return TemperatureStatus.HEAT_EXHAUSTION;
        if (bodyTemperature >= 38.5f) return TemperatureStatus.HOT;
        if (bodyTemperature >= 36.0f) return TemperatureStatus.NORMAL;
        if (bodyTemperature >= 34.0f) return TemperatureStatus.COLD;
        if (bodyTemperature >= 30.0f) return TemperatureStatus.HYPOTHERMIA;
        return TemperatureStatus.FROSTBITE;
    }

    public enum TemperatureStatus {
        HEAT_STROKE, HEAT_EXHAUSTION, HOT, NORMAL, COLD, HYPOTHERMIA, FROSTBITE
    }

    // ========== TITLES ==========
    public String getActiveTitle() { return activeTitle; }
    public void setActiveTitle(String title) { this.activeTitle = title; }
    public List<String> getUnlockedTitles() { return Collections.unmodifiableList(unlockedTitles); }
    public void unlockTitle(String title) { if (!unlockedTitles.contains(title)) unlockedTitles.add(title); }
    public void removeTitle(String title) { unlockedTitles.remove(title); if (activeTitle.equals(title)) activeTitle = ""; }

    // ========== KILL / STAT TRACKING ==========
    public int getTotalPlayerKills() { return totalPlayerKills; }
    public void incrementPlayerKills() { totalPlayerKills++; totalKillsPlayers++; }
    public int getBossesKilled() { return bossesKilled; }
    public void incrementBossesKilled() { bossesKilled++; }
    public int getHighestDamageDealt() { return highestDamageDealt; }
    public void updateHighestDamage(int dmg) { if (dmg > highestDamageDealt) highestDamageDealt = dmg; }

    // ========== TAMING ==========
    public List<UUID> getTamedEntities() { return Collections.unmodifiableList(tamedEntities); }
    public boolean canTameMore() { return tamedEntities.size() < maxTamedEntities; }
    public void addTamedEntity(UUID uuid) { if (canTameMore()) tamedEntities.add(uuid); }
    public void removeTamedEntity(UUID uuid) { tamedEntities.remove(uuid); }

    // ========== PROFESSION METHODS ==========
    public Profession getPrimaryProfession() {
        try { return Profession.valueOf(primaryProfession); }
        catch (Exception e) { return Profession.NONE; }
    }
    public void setPrimaryProfession(Profession p) { this.primaryProfession = p.name(); }
    public int getProfessionLevel(Profession p) { return professionLevels.getOrDefault(p.name(), 1); }
    public long getProfessionXp(Profession p) { return professionXp.getOrDefault(p.name(), 0L); }
    public void addProfessionXp(Profession p, long xp) {
        professionXp.merge(p.name(), xp, Long::sum);
    }
    public void setProfessionLevel(Profession p, int lvl) { professionLevels.put(p.name(), lvl); }

    // ========== NBT SERIALIZATION ==========
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        // Leveling
        tag.putInt("level", level);
        tag.putLong("experience", experience);
        tag.putInt("skillPoints", skillPoints);
        tag.putInt("professionPoints", professionPoints);

        // Vitals
        tag.putDouble("maxHealth", maxHealth);
        tag.putDouble("currentHealth", currentHealth);
        tag.putDouble("maxEnergy", maxEnergy);
        tag.putDouble("currentEnergy", currentEnergy);
        tag.putDouble("maxStamina", maxStamina);
        tag.putDouble("currentStamina", currentStamina);

        // Combat
        tag.putDouble("physicalAttack", physicalAttack);
        tag.putDouble("magicAttack", magicAttack);
        tag.putDouble("physicalDefense", physicalDefense);
        tag.putDouble("magicDefense", magicDefense);
        tag.putDouble("critChance", critChance);
        tag.putDouble("critMultiplier", critMultiplier);
        tag.putDouble("dodgeChance", dodgeChance);
        tag.putDouble("blockChance", blockChance);
        tag.putDouble("attackSpeed", attackSpeed);
        tag.putDouble("movementSpeed", movementSpeed);

        // Class
        tag.putString("playerClass", playerClass);
        tag.putInt("classLevel", classLevel);
        tag.putLong("classExperience", classExperience);

        ListTag skillsList = new ListTag();
        for (String s : unlockedSkills) skillsList.add(StringTag.valueOf(s));
        tag.put("unlockedSkills", skillsList);

        CompoundTag cooldownsTag = new CompoundTag();
        skillCooldowns.forEach((k, v) -> cooldownsTag.putLong(k, v));
        tag.put("skillCooldowns", cooldownsTag);

        tag.putIntArray("attributeAllocation", attributeAllocation);

        // Professions
        tag.putString("primaryProfession", primaryProfession);
        tag.putString("secondaryProfession", secondaryProfession);
        CompoundTag profLevels = new CompoundTag();
        professionLevels.forEach((k, v) -> profLevels.putInt(k, v));
        tag.put("professionLevels", profLevels);
        CompoundTag profXp = new CompoundTag();
        professionXp.forEach((k, v) -> profXp.putLong(k, v));
        tag.put("professionXp", profXp);

        // Karma
        tag.putInt("karma", karma);
        tag.putInt("karmaDecayTimer", karmaDecayTimer);
        tag.putInt("totalKillsPlayers", totalKillsPlayers);
        tag.putInt("totalKillsMobs", totalKillsMobs);
        tag.putInt("totalDeaths", totalDeaths);
        tag.putInt("totalHelpActions", totalHelpActions);

        // Economy
        tag.putLong("bankBalance", bankBalance);
        tag.putLong("walletBalance", walletBalance);
        tag.putLong("totalTaxesPaid", totalTaxesPaid);
        tag.putLong("totalEarned", totalEarned);

        // Clan
        if (clanId != null) tag.putUUID("clanId", clanId);
        tag.putString("clanRank", clanRank);
        tag.putBoolean("diplomaticImmunity", diplomaticImmunity);
        tag.putLong("diplomaticImmunityExpiry", diplomaticImmunityExpiry);

        // Temperature
        tag.putFloat("bodyTemperature", bodyTemperature);
        tag.putFloat("temperatureResistance", temperatureResistance);
        tag.putInt("heatExhaustionTimer", heatExhaustionTimer);
        tag.putInt("hypothermiaTimer", hypothermiaTimer);
        tag.putBoolean("hasFrostbite", hasFrostbite);

        // Titles
        tag.putString("activeTitle", activeTitle);
        ListTag titleList = new ListTag();
        for (String t : unlockedTitles) titleList.add(StringTag.valueOf(t));
        tag.put("unlockedTitles", titleList);

        // Exploration
        tag.putLong("distanceTraveled", distanceTraveled);
        tag.putInt("dimensionsVisited", dimensionsVisited);
        ListTag biomeList = new ListTag();
        for (String b : discoveredBiomes) biomeList.add(StringTag.valueOf(b));
        tag.put("discoveredBiomes", biomeList);

        // Combat stats
        tag.putInt("totalPlayerKills", totalPlayerKills);
        tag.putInt("highestDamageDealt", highestDamageDealt);
        tag.putInt("bossesKilled", bossesKilled);

        // Taming
        ListTag tamedList = new ListTag();
        for (UUID u : tamedEntities) {
            CompoundTag ut = new CompoundTag();
            ut.putUUID("uuid", u);
            tamedList.add(ut);
        }
        tag.put("tamedEntities", tamedList);
        tag.putInt("maxTamedEntities", maxTamedEntities);

        // Misc
        tag.putBoolean("firstLogin", firstLogin);
        tag.putLong("lastOnline", lastOnline);
        tag.putInt("rebirthCount", rebirthCount);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        level = tag.getInt("level");
        experience = tag.getLong("experience");
        skillPoints = tag.getInt("skillPoints");
        professionPoints = tag.getInt("professionPoints");

        maxHealth = tag.getDouble("maxHealth");
        currentHealth = tag.getDouble("currentHealth");
        maxEnergy = tag.getDouble("maxEnergy");
        currentEnergy = tag.getDouble("currentEnergy");
        maxStamina = tag.getDouble("maxStamina");
        currentStamina = tag.getDouble("currentStamina");

        physicalAttack = tag.getDouble("physicalAttack");
        magicAttack = tag.getDouble("magicAttack");
        physicalDefense = tag.getDouble("physicalDefense");
        magicDefense = tag.getDouble("magicDefense");
        critChance = tag.getDouble("critChance");
        critMultiplier = tag.getDouble("critMultiplier");
        dodgeChance = tag.getDouble("dodgeChance");
        blockChance = tag.getDouble("blockChance");
        attackSpeed = tag.getDouble("attackSpeed");
        movementSpeed = tag.getDouble("movementSpeed");

        playerClass = tag.getString("playerClass");
        classLevel = tag.getInt("classLevel");
        classExperience = tag.getLong("classExperience");

        unlockedSkills = new ArrayList<>();
        ListTag skillsList = tag.getList("unlockedSkills", Tag.TAG_STRING);
        for (int i = 0; i < skillsList.size(); i++) unlockedSkills.add(skillsList.getString(i));

        skillCooldowns = new HashMap<>();
        CompoundTag cooldownsTag = tag.getCompound("skillCooldowns");
        for (String k : cooldownsTag.getAllKeys()) skillCooldowns.put(k, cooldownsTag.getLong(k));

        int[] rawAttr = tag.getIntArray("attributeAllocation");
        if (rawAttr.length == 6) attributeAllocation = rawAttr;

        primaryProfession = tag.getString("primaryProfession");
        secondaryProfession = tag.getString("secondaryProfession");
        CompoundTag profLevels = tag.getCompound("professionLevels");
        professionLevels = new HashMap<>();
        for (String k : profLevels.getAllKeys()) professionLevels.put(k, profLevels.getInt(k));
        CompoundTag profXp = tag.getCompound("professionXp");
        professionXp = new HashMap<>();
        for (String k : profXp.getAllKeys()) professionXp.put(k, profXp.getLong(k));

        karma = tag.getInt("karma");
        karmaDecayTimer = tag.getInt("karmaDecayTimer");
        totalKillsPlayers = tag.getInt("totalKillsPlayers");
        totalKillsMobs = tag.getInt("totalKillsMobs");
        totalDeaths = tag.getInt("totalDeaths");
        totalHelpActions = tag.getInt("totalHelpActions");

        bankBalance = tag.getLong("bankBalance");
        walletBalance = tag.getLong("walletBalance");
        totalTaxesPaid = tag.getLong("totalTaxesPaid");
        totalEarned = tag.getLong("totalEarned");

        if (tag.hasUUID("clanId")) clanId = tag.getUUID("clanId");
        clanRank = tag.getString("clanRank");
        diplomaticImmunity = tag.getBoolean("diplomaticImmunity");
        diplomaticImmunityExpiry = tag.getLong("diplomaticImmunityExpiry");

        bodyTemperature = tag.getFloat("bodyTemperature");
        temperatureResistance = tag.getFloat("temperatureResistance");
        heatExhaustionTimer = tag.getInt("heatExhaustionTimer");
        hypothermiaTimer = tag.getInt("hypothermiaTimer");
        hasFrostbite = tag.getBoolean("hasFrostbite");

        activeTitle = tag.getString("activeTitle");
        unlockedTitles = new ArrayList<>();
        ListTag titleList = tag.getList("unlockedTitles", Tag.TAG_STRING);
        for (int i = 0; i < titleList.size(); i++) unlockedTitles.add(titleList.getString(i));

        distanceTraveled = tag.getLong("distanceTraveled");
        dimensionsVisited = tag.getInt("dimensionsVisited");
        discoveredBiomes = new HashSet<>();
        ListTag biomeList = tag.getList("discoveredBiomes", Tag.TAG_STRING);
        for (int i = 0; i < biomeList.size(); i++) discoveredBiomes.add(biomeList.getString(i));

        totalPlayerKills = tag.getInt("totalPlayerKills");
        highestDamageDealt = tag.getInt("highestDamageDealt");
        bossesKilled = tag.getInt("bossesKilled");

        tamedEntities = new ArrayList<>();
        ListTag tamedList = tag.getList("tamedEntities", Tag.TAG_COMPOUND);
        for (int i = 0; i < tamedList.size(); i++) {
            CompoundTag ut = tamedList.getCompound(i);
            tamedEntities.add(ut.getUUID("uuid"));
        }
        maxTamedEntities = tag.getInt("maxTamedEntities");

        firstLogin = tag.getBoolean("firstLogin");
        lastOnline = tag.getLong("lastOnline");
        rebirthCount = tag.getInt("rebirthCount");
    }

    // Expose some raw setters for system use
    public void setKarmaDecayTimer(int t) { this.karmaDecayTimer = t; }
    public int getKarmaDecayTimer() { return karmaDecayTimer; }
    public void setFirstLogin(boolean b) { this.firstLogin = b; }
    public boolean isFirstLogin() { return firstLogin; }
    public void setLastOnline(long t) { this.lastOnline = t; }
    public void incrementDeaths() { totalDeaths++; }
    public void incrementHelpActions() { totalHelpActions++; }
    public int getTotalDeaths() { return totalDeaths; }
    public Set<String> getDiscoveredBiomes() { return discoveredBiomes; }
    public void addDiscoveredBiome(String biome) { discoveredBiomes.add(biome); }
    public long getDistanceTraveled() { return distanceTraveled; }
    public void addDistanceTraveled(long d) { distanceTraveled += d; }
    public int getRebirthCount() { return rebirthCount; }
    public void incrementRebirth() { rebirthCount++; }
    public int getSkillPoints() { return skillPoints; }
    public int getProfessionPoints() { return professionPoints; }
    public void consumeSkillPoint() { if (skillPoints > 0) skillPoints--; }
    public void addSkillPoints(int p) { skillPoints += p; }
    public UUID getOwnedTerritoryId() { return ownedTerritoryId; }
    public void setOwnedTerritoryId(UUID id) { this.ownedTerritoryId = id; }
    public int getHeatExhaustionTimer() { return heatExhaustionTimer; }
    public void setHeatExhaustionTimer(int t) { this.heatExhaustionTimer = t; }
    public int getHypothermiaTimer() { return hypothermiaTimer; }
    public void setHypothermiaTimer(int t) { this.hypothermiaTimer = t; }
    public boolean hasFrostbite() { return hasFrostbite; }
    public void setFrostbite(boolean f) { this.hasFrostbite = f; }
    public void setMaxHealth(double h) { this.maxHealth = h; }
    public void setMaxEnergy(double e) { this.maxEnergy = e; }
    public void setPhysicalAttack(double a) { this.physicalAttack = a; }
    public void setMagicAttack(double a) { this.magicAttack = a; }
    public void setPhysicalDefense(double d) { this.physicalDefense = d; }
    public void setMagicDefense(double d) { this.magicDefense = d; }
    public double getPhysicalAttack() { return physicalAttack; }
    public double getMagicAttack() { return magicAttack; }
    public double getPhysicalDefense() { return physicalDefense; }
    public double getMagicDefense() { return magicDefense; }
    public double getCritChance() { return critChance; }
    public double getCritMultiplier() { return critMultiplier; }
    public double getDodgeChance() { return dodgeChance; }
    public double getBlockChance() { return blockChance; }
    public double getAttackSpeed() { return attackSpeed; }
    public double getMovementSpeed() { return movementSpeed; }
    public long getTotalEarned() { return totalEarned; }
    public long getTotalTaxesPaid() { return totalTaxesPaid; }
    public void addTaxPaid(long amount) { totalTaxesPaid += amount; walletBalance = Math.max(0, walletBalance - amount); }
    public int getClassLevel() { return classLevel; }
    public void setClassLevel(int l) { this.classLevel = l; }
    public long getClassExperience() { return classExperience; }
    public void addClassExperience(long xp) { this.classExperience += xp; }
}

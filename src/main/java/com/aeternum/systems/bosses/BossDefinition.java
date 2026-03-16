package com.aeternum.systems.bosses;

public class BossDefinition {
    private final String id;
    private final String displayName;
    private final BossTier tier;
    private final BossType type;
    private final int maxHP;
    private final int level;
    private final int minPlayersRecommended;
    private final int physicalDamage;
    private final boolean hasPhases;
    private final boolean hasMinions;
    private final String lore;
    private final long respawnTimeTicks;
    private final String[] drops;
    private final long aurumDrop;
    private final int expReward;
    private final boolean isTerritoryOwner;
    private final String territoryName;

    // Respawn tracking
    private long lastKilledAt = 0;
    private boolean isAlive = true;

    public BossDefinition(String id, String displayName, BossTier tier, BossType type,
                           int maxHP, int level, int minPlayers, int physDamage,
                           boolean phases, boolean minions, String lore,
                           long respawnTicks, String[] drops, long aurum, int exp) {
        this(id, displayName, tier, type, maxHP, level, minPlayers, physDamage,
             phases, minions, lore, respawnTicks, drops, aurum, exp, false, "");
    }

    public BossDefinition(String id, String displayName, BossTier tier, BossType type,
                           int maxHP, int level, int minPlayers, int physDamage,
                           boolean phases, boolean minions, String lore,
                           long respawnTicks, String[] drops, long aurum, int exp,
                           boolean isTerritoryOwner, String territoryName) {
        this.id = id; this.displayName = displayName; this.tier = tier; this.type = type;
        this.maxHP = maxHP; this.level = level; this.minPlayersRecommended = minPlayers;
        this.physicalDamage = physDamage; this.hasPhases = phases; this.hasMinions = minions;
        this.lore = lore; this.respawnTimeTicks = respawnTicks; this.drops = drops;
        this.aurumDrop = aurum; this.expReward = exp;
        this.isTerritoryOwner = isTerritoryOwner; this.territoryName = territoryName;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public BossTier getTier() { return tier; }
    public BossType getType() { return type; }
    public int getMaxHP() { return maxHP; }
    public int getLevel() { return level; }
    public int getMinPlayersRecommended() { return minPlayersRecommended; }
    public int getPhysicalDamage() { return physicalDamage; }
    public boolean hasPhases() { return hasPhases; }
    public boolean hasMinions() { return hasMinions; }
    public String getLore() { return lore; }
    public long getRespawnTimeTicks() { return respawnTimeTicks; }
    public String[] getDrops() { return drops; }
    public long getAurumDrop() { return aurumDrop; }
    public int getExpReward() { return expReward; }
    public boolean isTerritoryOwner() { return isTerritoryOwner; }
    public String getTerritoryName() { return territoryName; }
    public boolean isAlive() { return isAlive; }
    public void setAlive(boolean alive) { this.isAlive = alive; }
    public long getLastKilledAt() { return lastKilledAt; }
    public void onKilled() { this.isAlive = false; this.lastKilledAt = System.currentTimeMillis(); }
    public boolean canRespawn() {
        if (respawnTimeTicks < 0) return false; // Never respawns
        return !isAlive && System.currentTimeMillis() >= lastKilledAt + (respawnTimeTicks * 50);
    }
}

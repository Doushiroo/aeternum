package com.aeternum.systems.skills;

import com.aeternum.systems.classes.PlayerClass;

import java.util.Arrays;
import java.util.List;

public class AeternumSkill {
    private final String id;
    private final String displayName;
    private final String description;
    private final PlayerClass requiredClass;   // null = any class
    private final int levelRequirement;
    private final double energyCost;
    private final long cooldownMs;
    private final SkillType type;
    private final List<SkillTag> tags;

    public AeternumSkill(String id, String displayName, String description,
                          PlayerClass requiredClass, int levelRequirement,
                          double energyCost, long cooldownMs,
                          SkillType type, SkillTag... tags) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.requiredClass = requiredClass;
        this.levelRequirement = levelRequirement;
        this.energyCost = energyCost;
        this.cooldownMs = cooldownMs;
        this.type = type;
        this.tags = Arrays.asList(tags);
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public PlayerClass getRequiredClass() { return requiredClass; }
    public int getLevelRequirement() { return levelRequirement; }
    public double getEnergyCost() { return energyCost; }
    public long getCooldownMs() { return cooldownMs; }
    public SkillType getType() { return type; }
    public List<SkillTag> getTags() { return tags; }
    public boolean hasTag(SkillTag tag) { return tags.contains(tag); }

    /** Skill point cost to unlock this skill */
    public int getUnlockCost() {
        return switch (type) {
            case PASSIVE -> 1;
            case ACTIVE -> 2;
            case ULTIMATE -> 5;
        };
    }
}

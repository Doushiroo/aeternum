package com.aeternum.systems.titles;

import java.util.Arrays;
import java.util.List;

public class AeternumTitle {
    private final String id;
    private final String displayName;
    private final String requirement;
    private final TitleCategory category;
    private final boolean dynamic;
    private final List<TitleEffect> effects;

    public AeternumTitle(String id, String displayName, String requirement, TitleCategory category, boolean dynamic, TitleEffect... effects) {
        this.id = id; this.displayName = displayName; this.requirement = requirement;
        this.category = category; this.dynamic = dynamic; this.effects = Arrays.asList(effects);
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getRequirement() { return requirement; }
    public TitleCategory getCategory() { return category; }
    public boolean isDynamic() { return dynamic; }
    public List<TitleEffect> getEffects() { return effects; }
}

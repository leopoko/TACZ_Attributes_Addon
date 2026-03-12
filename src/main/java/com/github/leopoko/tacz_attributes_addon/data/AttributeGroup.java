package com.github.leopoko.tacz_attributes_addon.data;

import java.util.Collections;
import java.util.Set;

/**
 * Defines a group of mutually exclusive attributes with a limit on how many
 * from the group can appear on a single gun. Used for balancing attribute combinations.
 *
 * Configured in attribute_pool.json (global) and gun_attribute_overrides.json (per-gun).
 */
public class AttributeGroup {
    private final String name;
    private final int maxFromGroup;
    private final Set<String> attributes;

    public AttributeGroup(String name, int maxFromGroup, Set<String> attributes) {
        this.name = name;
        this.maxFromGroup = maxFromGroup;
        this.attributes = attributes != null ? attributes : Collections.emptySet();
    }

    public String getName() { return name; }
    public int getMaxFromGroup() { return maxFromGroup; }
    public Set<String> getAttributes() { return attributes; }
    public boolean hasAttributes() { return !attributes.isEmpty(); }
}

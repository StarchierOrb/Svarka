package org.bukkit.craftbukkit.util;

import net.minecraft.util.DamageSource;

// Util class to create custom DamageSources.
public final class CraftDamageSource extends DamageSource {
    public static DamageSource copyOf(final DamageSource original) {
        CraftDamageSource newSource = new CraftDamageSource(original.damageType);

        // Check ignoresArmor
        if (original.isUnblockable()) {
            newSource.setIgnoreArmor();
        }

        // Check magic
        if (original.isMagicDamage()) {
            newSource.setMagic();
        }

        // Check fire
        if (original.isExplosion()) {
            newSource.setExplosion();
        }

        return newSource;
    }

    private CraftDamageSource(String identifier) {
        super(identifier);
    }
}
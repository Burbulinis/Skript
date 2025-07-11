package org.skriptlang.skript.bukkit.spawners.util;

import com.google.common.base.Preconditions;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public record SkriptEquipmentDropChance(@NotNull EquipmentSlot slot, float chance) {

	public SkriptEquipmentDropChance {
		Preconditions.checkNotNull(slot, "slot cannot be null");
		Preconditions.checkArgument(chance > 0, "chance must be greater than 0");
	}

}

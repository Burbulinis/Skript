package org.skriptlang.skript.bukkit.spawners.util;

import com.google.common.base.Preconditions;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;

public record TrialSpawnerRewardEntry(@NotNull LootTable lootTable, int weight) {

	public TrialSpawnerRewardEntry {
		Preconditions.checkNotNull(lootTable, "lootTable cannot be null");
		Preconditions.checkArgument(weight > 0, "weight must be greater than 0");
	}

}

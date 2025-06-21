package org.skriptlang.skript.bukkit.spawners.util;

import com.google.common.base.Preconditions;
import org.bukkit.block.TrialSpawner;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Skript trial spawner, which is a wrapper around a Bukkit {@link TrialSpawner}.
 * @param trialSpawner the Bukkit trial spawner
 */
public record SkriptTrialSpawner(@NotNull TrialSpawner trialSpawner) {

	public SkriptTrialSpawner {
		Preconditions.checkNotNull(trialSpawner, "trialSpawner cannot be null");
	}

}

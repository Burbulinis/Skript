package org.skriptlang.skript.bukkit.spawners.util;

import com.google.common.base.Preconditions;
import org.bukkit.block.TrialSpawner;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Skript trial spawner, which is a wrapper around a Bukkit {@link TrialSpawner}.
 */
public class SkriptTrialSpawner implements SkriptSpawner {

	private final @NotNull TrialSpawner trialSpawner;

	public SkriptTrialSpawner(@NotNull TrialSpawner trialSpawner) {
		Preconditions.checkNotNull(trialSpawner, "trialSpawner cannot be null");

		this.trialSpawner = trialSpawner;
	}

	public @NotNull TrialSpawner getBukkitTrialSpawner() {
		return trialSpawner;
	}

	@Override
	public @NotNull SpawnerType getType() {
		return SpawnerType.TRIAL;
	}

}

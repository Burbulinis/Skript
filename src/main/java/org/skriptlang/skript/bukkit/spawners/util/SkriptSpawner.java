package org.skriptlang.skript.bukkit.spawners.util;

import com.google.common.base.Preconditions;
import org.bukkit.spawner.Spawner;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Skript spawner, which is a wrapper around a Bukkit {@link Spawner} and its type.
 * @param spawner the Bukkit spawner
 * @param type the type of the spawner, which can be either a minecart or a creature spawner
 */
public record SkriptSpawner(@NotNull Spawner spawner, @NotNull SpawnerType type) {

	public SkriptSpawner {
		Preconditions.checkNotNull(spawner, "spawner cannot be null");
		Preconditions.checkNotNull(type, "type cannot be null");
	}

}
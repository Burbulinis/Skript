package org.skriptlang.skript.bukkit.spawners.util;

import com.google.common.base.Preconditions;
import org.bukkit.spawner.Spawner;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Skript mob spawner, which is a wrapper around {@link Spawner} and it can be either a minecart or a creature spawner.
 */
public class SkriptMobSpawner implements SkriptSpawner {

	private final @NotNull Spawner spawner;
	private final @NotNull SpawnerType type;

	/**
	 * Constructs a SkriptMobSpawner from a Bukkit spawner and a type.
	 * @param spawner the Bukkit spawner
	 * @param type the type of the spawner, which can be either a minecart or a creature spawner
	 */
	public SkriptMobSpawner(@NotNull Spawner spawner, @NotNull SpawnerType type) {
		Preconditions.checkNotNull(spawner, "spawner cannot be null");
		Preconditions.checkNotNull(type, "type cannot be null");
		Preconditions.checkArgument(type != SpawnerType.TRIAL, "type cannot be TRIAL");

		this.spawner = spawner;
		this.type = type;
	}

	public @NotNull Spawner getBukkitSpawner() {
		return spawner;
	}

	@Override
	public @NotNull SpawnerType getType() {
		return type;
	}

}

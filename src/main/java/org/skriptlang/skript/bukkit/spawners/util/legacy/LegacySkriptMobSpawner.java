package org.skriptlang.skript.bukkit.spawners.util.legacy;

import com.google.common.base.Preconditions;
import org.bukkit.block.CreatureSpawner;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.bukkit.spawners.util.SkriptSpawner;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerType;

public class LegacySkriptMobSpawner implements SkriptSpawner {

	private final @NotNull CreatureSpawner spawner;

	public LegacySkriptMobSpawner(@NotNull CreatureSpawner spawner) {
		Preconditions.checkNotNull(spawner, "spawner cannot be null");
		this.spawner = spawner;
	}

	public @NotNull CreatureSpawner getBukkitSpawner() {
		return spawner;
	}

	@Override
	public @NotNull SpawnerType getType() {
		return SpawnerType.CREATURE;
	}

}

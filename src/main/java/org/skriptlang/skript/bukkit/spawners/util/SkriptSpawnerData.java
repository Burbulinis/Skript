package org.skriptlang.skript.bukkit.spawners.util;

import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import ch.njol.yggdrasil.YggdrasilSerializable;
import com.google.common.base.Preconditions;
import org.bukkit.spawner.Spawner;
import org.jetbrains.annotations.NotNull;

public class SkriptSpawnerData extends AbstractSpawnerData implements YggdrasilSerializable {

	private int maxNearbyEntities = SpawnerUtils.DEFAULT_MAX_NEARBY_ENTITIES;
	private int spawnCount = SpawnerUtils.DEFAULT_SPAWN_COUNT;
	private @NotNull Timespan maxSpawnDelay = SpawnerUtils.DEFAULT_MAX_SPAWN_DELAY;
	private @NotNull Timespan minSpawnDelay = SpawnerUtils.DEFAULT_MIN_SPAWN_DELAY;

	public static SkriptSpawnerData fromBukkitSpawner(@NotNull Spawner spawner) {
		SkriptSpawnerData data = new SkriptSpawnerData();

		SpawnerUtils.applySpawnerDataToAbstractData(spawner, data);
		data.setMaxNearbyEntities(spawner.getMaxNearbyEntities());
		data.setSpawnCount(spawner.getSpawnCount());
		data.setMaxSpawnDelay(new Timespan(TimePeriod.TICK, spawner.getMaxSpawnDelay()));
		data.setMinSpawnDelay(new Timespan(TimePeriod.TICK, spawner.getMinSpawnDelay()));

		return data;
	}

	public void applyDataToBukkitSpawner(@NotNull Spawner spawner) {
		SpawnerUtils.applyAbstractDataToSpawner(this, spawner);

		spawner.setMaxNearbyEntities(getMaxNearbyEntities());
		spawner.setSpawnCount(getSpawnCount());
		spawner.setMaxSpawnDelay(Math.clamp(getMaxSpawnDelay().getAs(TimePeriod.TICK), 0, Integer.MAX_VALUE));
		spawner.setMinSpawnDelay(Math.clamp(getMinSpawnDelay().getAs(TimePeriod.TICK), 0, Integer.MAX_VALUE));

		// todo: update spawner
	}

	public int getMaxNearbyEntities() {
		return maxNearbyEntities;
	}

	public void setMaxNearbyEntities(int maxNearbyEntities) {
		this.maxNearbyEntities = maxNearbyEntities;
	}

	public int getSpawnCount() {
		return spawnCount;
	}

	public void setSpawnCount(int spawnCount) {
		this.spawnCount = spawnCount;
	}

	public @NotNull Timespan getMaxSpawnDelay() {
		return maxSpawnDelay;
	}

	public void setMaxSpawnDelay(@NotNull Timespan maxSpawnDelay) {
		Preconditions.checkArgument(maxSpawnDelay.compareTo(minSpawnDelay) >= 0,
				"Maximum spawn delay cannot be less than minimum spawn delay");
		this.maxSpawnDelay = maxSpawnDelay;
	}

	public @NotNull Timespan getMinSpawnDelay() {
		return minSpawnDelay;
	}

	public void setMinSpawnDelay(@NotNull Timespan minSpawnDelay) {
		Preconditions.checkArgument(minSpawnDelay.compareTo(maxSpawnDelay) <= 0,
				"Minimum spawn delay cannot be greater than the maximum spawn delay");
		this.minSpawnDelay = minSpawnDelay;
	}

}

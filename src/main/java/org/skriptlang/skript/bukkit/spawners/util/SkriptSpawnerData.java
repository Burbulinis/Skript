package org.skriptlang.skript.bukkit.spawners.util;

import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import ch.njol.yggdrasil.YggdrasilSerializable;
import com.google.common.base.Preconditions;
import org.bukkit.spawner.Spawner;

public class SkriptSpawnerData extends AbstractSpawnerData implements YggdrasilSerializable {

	private int maxNearbyEntities = SpawnerUtils.DEFAULT_MAX_NEARBY_ENTITIES;
	private int spawnCount = SpawnerUtils.DEFAULT_SPAWN_COUNT;
	private Timespan maxSpawnDelay = SpawnerUtils.DEFAULT_MAX_SPAWN_DELAY;
	private Timespan minSpawnDelay = SpawnerUtils.DEFAULT_MIN_SPAWN_DELAY;

	public static SkriptSpawnerData fromBukkitSpawner(Spawner spawner) {
		SkriptSpawnerData data = new SkriptSpawnerData();

		data.setRequiredPlayerRange(spawner.getRequiredPlayerRange());
		data.setSpawnRange(spawner.getSpawnRange());

		if (spawner.getSpawnedEntity() != null)
			data.setSpawnerEntitySnapshot(spawner.getSpawnedEntity());
		else if (spawner.getSpawnedType() != null)
			data.setSpawnerType(EntityUtils.toSkriptEntityData(spawner.getSpawnedType()));
		else if (!spawner.getPotentialSpawns().isEmpty())
			data.setSpawnerEntries(spawner.getPotentialSpawns());

		data.setMaxNearbyEntities(spawner.getMaxNearbyEntities());
		data.setSpawnCount(spawner.getSpawnCount());
		data.setMaxSpawnDelay(new Timespan(TimePeriod.TICK, spawner.getMaxSpawnDelay()));
		data.setMinSpawnDelay(new Timespan(TimePeriod.TICK, spawner.getMinSpawnDelay()));

		return data;
	}

	public void applyDataToBukkitSpawner(Spawner spawner) {
		spawner.setRequiredPlayerRange(getRequiredPlayerRange());
		spawner.setSpawnRange(getSpawnRange());

		if (getSpawnerEntitySnapshot() != null)
			spawner.setSpawnedEntity(getSpawnerEntitySnapshot());
		else if (getSpawnerType() != null)
			spawner.setSpawnedType(EntityUtils.toBukkitEntityType(getSpawnerType()));
		else if (!getSpawnerEntries().isEmpty())
			spawner.setPotentialSpawns(getSpawnerEntries());

		spawner.setMaxNearbyEntities(getMaxNearbyEntities());
		spawner.setSpawnCount(getSpawnCount());
		spawner.setMaxSpawnDelay(Math.clamp(getMaxSpawnDelay().getAs(TimePeriod.TICK), 0, Integer.MAX_VALUE));
		spawner.setMinSpawnDelay(Math.clamp(getMinSpawnDelay().getAs(TimePeriod.TICK), 0, Integer.MAX_VALUE));

		// todo: update spawners
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

	public Timespan getMaxSpawnDelay() {
		return maxSpawnDelay;
	}

	public void setMaxSpawnDelay(Timespan maxSpawnDelay) {
		Preconditions.checkArgument(maxSpawnDelay.compareTo(minSpawnDelay) >= 0, "Maximum spawn delay cannot be less than minimum spawn delay");
		this.maxSpawnDelay = maxSpawnDelay;
	}

	public Timespan getMinSpawnDelay() {
		return minSpawnDelay;
	}

	public void setMinSpawnDelay(Timespan minSpawnDelay) {
		Preconditions.checkArgument(minSpawnDelay.compareTo(maxSpawnDelay) <= 0, "Minimum spawn delay cannot be greater than the maximum spawn delay");
		this.minSpawnDelay = minSpawnDelay;
	}

}

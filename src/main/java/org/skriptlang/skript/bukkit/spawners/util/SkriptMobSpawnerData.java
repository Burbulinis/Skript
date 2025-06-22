package org.skriptlang.skript.bukkit.spawners.util;

import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import ch.njol.yggdrasil.YggdrasilSerializable;
import com.google.common.base.Preconditions;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.spawner.Spawner;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the data of a {@link Spawner}, which may be a {@link CreatureSpawner} or a {@link SpawnerMinecart}
 *
 * @see SkriptTrialSpawnerData
 * @see SkriptSpawnerData
 */
public class SkriptMobSpawnerData extends SkriptSpawnerData implements YggdrasilSerializable {

	private int maxNearbyEntities = SpawnerUtils.DEFAULT_MAX_NEARBY_ENTITIES;
	private int spawnCount = SpawnerUtils.DEFAULT_SPAWN_COUNT;
	private @NotNull Timespan maxSpawnDelay = SpawnerUtils.DEFAULT_MAX_SPAWN_DELAY;
	private @NotNull Timespan minSpawnDelay = SpawnerUtils.DEFAULT_MIN_SPAWN_DELAY;

	/**
	 * Creates a new SkriptSpawnerData instance from the given Bukkit {@link Spawner}.
	 *
	 * @param spawner the Bukkit spawner to convert
	 * @return a new SkriptSpawnerData instance containing the data from the Bukkit spawner
	 */
	public static SkriptMobSpawnerData fromBukkitSpawner(@NotNull Spawner spawner) {
		SkriptMobSpawnerData data = new SkriptMobSpawnerData();

		SpawnerUtils.applySpawnerDataToSpawnerData(spawner, data);
		data.setMaxNearbyEntities(spawner.getMaxNearbyEntities());
		data.setSpawnCount(spawner.getSpawnCount());
		data.setMaxSpawnDelay(new Timespan(TimePeriod.TICK, spawner.getMaxSpawnDelay()));
		data.setMinSpawnDelay(new Timespan(TimePeriod.TICK, spawner.getMinSpawnDelay()));

		return data;
	}

	/**
	 * Applies this SkriptSpawnerData to the given Bukkit spawners.
	 *
	 * @param spawners the Bukkit spawners to apply the data to
	 */
	public void applyDataToBukkitSpawners(@NotNull Spawner[] spawners) {
		Preconditions.checkNotNull(spawners, "spawners cannot be null");
		for (Spawner spawner : spawners) {
			applyDataToBukkitSpawner(spawner);
		}
	}

	/**
	 * Applies this SkriptSpawnerData to the given Bukkit spawner.
	 *
	 * @param spawner the Bukkit spawner to apply the data to
	 */
	public void applyDataToBukkitSpawner(@NotNull Spawner spawner) {
		Preconditions.checkNotNull(spawner, "spawner cannot be null");
		SpawnerUtils.applySpawnerDataToSpawner(this, spawner);

		spawner.setMaxNearbyEntities(getMaxNearbyEntities());
		spawner.setSpawnCount(getSpawnCount());
		spawner.setMaxSpawnDelay(Math.clamp(getMaxSpawnDelay().getAs(TimePeriod.TICK), 0, Integer.MAX_VALUE));
		spawner.setMinSpawnDelay(Math.clamp(getMinSpawnDelay().getAs(TimePeriod.TICK), 0, Integer.MAX_VALUE));

		if (spawner instanceof CreatureSpawner creatureSpawner)
			creatureSpawner.update(true, false);
	}

	/**
	 * Returns the maximum number of nearby similar entities that can be spawned by this spawner.
	 * <p>
	 * The default value is 6.
	 * @return the maximum number of nearby entities
	 */
	public int getMaxNearbyEntities() {
		return maxNearbyEntities;
	}

	/**
	 * Sets the maximum number of nearby similar entities that can be spawned by this spawner.
	 * <p>
	 * The default value is 6.
	 * @param maxNearbyEntities the maximum number of nearby entities
	 */
	public void setMaxNearbyEntities(int maxNearbyEntities) {
		this.maxNearbyEntities = maxNearbyEntities;
	}

	/**
	 * Returns the number of entities that the spawner will attempt to spawn each spawn attempt.
	 * <p>
	 * The default value is 4.
	 * @return the spawn count
	 */
	public int getSpawnCount() {
		return spawnCount;
	}

	/**
	 * Sets the number of entities that the spawner will attempt to spawn each spawn attempt.
	 * <p>
	 * The default value is 4.
	 * @param spawnCount the spawn count
	 */
	public void setSpawnCount(int spawnCount) {
		this.spawnCount = spawnCount;
	}

	/**
	 * Returns the maximum spawn delay of the spawner.
	 * <br>
	 * The spawn delay is chosen randomly between the minimum and maximum spawn delays,
	 * which determines how long the spawner will wait before attempting to spawn entities again.
	 * <p>
	 * The maximum spawn delay is always greater than or equal to the minimum spawn delay,
	 * <p>
	 * The default value is 40 seconds (800 ticks).
	 * @return the maximum spawn delay
	 */
	public @NotNull Timespan getMaxSpawnDelay() {
		return maxSpawnDelay;
	}

	/**
	 * Sets the maximum spawn delay of the spawner.
	 * <br>
	 * The spawn delay is chosen randomly between the minimum and maximum spawn delays,
	 * which determines how long the spawner will wait before attempting to spawn entities again.
	 * <p>
	 * The maximum spawn delay must be greater than the minimum spawn delay.
	 * <p>
	 * The default value is 40 seconds (800 ticks).
	 * @param maxSpawnDelay the maximum spawn delay to set
	 */
	public void setMaxSpawnDelay(@NotNull Timespan maxSpawnDelay) {
		Preconditions.checkNotNull(maxSpawnDelay, "Maximum spawn delay cannot be null");
		Preconditions.checkArgument(maxSpawnDelay.compareTo(minSpawnDelay) >= 0,
				"Maximum spawn delay cannot be less than minimum spawn delay");
		this.maxSpawnDelay = maxSpawnDelay;
	}

	/**
	 * Returns the minimum spawn delay of the spawner.
	 * <br>
	 * The spawn delay is chosen randomly between the minimum and maximum spawn delays,
	 * which determines how long the spawner will wait before attempting to spawn entities again.
	 * <p>
	 * The minimum spawn delay is always less than or equal to the maximum spawn delay,
	 * <p>
	 * The default value is 10 seconds (200 ticks).
	 * @return the minimum spawn delay
	 */
	public @NotNull Timespan getMinSpawnDelay() {
		return minSpawnDelay;
	}

	/**
	 * Sets the minimum spawn delay of the spawner.
	 * <br>
	 * The spawn delay is chosen randomly between the minimum and maximum spawn delays,
	 * which determines how long the spawner will wait before attempting to spawn entities again.
	 * <p>
	 * The minimum spawn delay must not be greater than the maximum spawn delay.
	 * <p>
	 * The default value is 10 seconds (200 ticks).
	 * @param minSpawnDelay the minimum spawn delay to set
	 */
	public void setMinSpawnDelay(@NotNull Timespan minSpawnDelay) {
		Preconditions.checkNotNull(minSpawnDelay, "Minimum spawn delay cannot be null");
		Preconditions.checkArgument(minSpawnDelay.compareTo(maxSpawnDelay) <= 0,
				"Minimum spawn delay cannot be greater than the maximum spawn delay");
		this.minSpawnDelay = minSpawnDelay;
	}

}

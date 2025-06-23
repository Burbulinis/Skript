package org.skriptlang.skript.bukkit.spawners.util;

import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import ch.njol.yggdrasil.YggdrasilSerializable;
import com.google.common.base.Preconditions;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.spawner.Spawner;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.bukkit.spawners.util.legacy.LegacySkriptMobSpawner;

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

	// todo: add support to make the spawner spawn items

	/**
	 * Creates a new SkriptSpawnerData instance from the given Bukkit {@link Spawner}.
	 *
	 * @param skriptSpawner the Bukkit spawner to convert
	 * @return a new SkriptSpawnerData instance containing the data from the Bukkit spawner
	 */
	public static SkriptMobSpawnerData fromSpawner(@NotNull SkriptMobSpawner skriptSpawner) {
		Preconditions.checkNotNull(skriptSpawner, "skriptMobSpawner cannot be null");

		Spawner spawner = skriptSpawner.getBukkitSpawner();
		SkriptMobSpawnerData data = new SkriptMobSpawnerData();

		SpawnerUtils.applySpawnerDataToSpawnerData(spawner, data);
		data.setMaxNearbyEntities(spawner.getMaxNearbyEntities());
		data.setSpawnCount(spawner.getSpawnCount());
		data.setMaxSpawnDelay(new Timespan(TimePeriod.TICK, spawner.getMaxSpawnDelay()));
		data.setMinSpawnDelay(new Timespan(TimePeriod.TICK, spawner.getMinSpawnDelay()));

		return data;
	}

	//<editor-fold desc="Legacy spawner handling" defaultstate="collapsed">
	/**
	 * Creates a new SkriptSpawnerData instance from the given legacy SkriptMobSpawner.
	 * @param legacySpawner the legacy SkriptMobSpawner to convert
	 * @return a new SkriptSpawnerData instance containing the data from the legacy spawner
	 */
	public static SkriptMobSpawnerData fromLegacySpawner(@NotNull LegacySkriptMobSpawner legacySpawner) {
		Preconditions.checkNotNull(legacySpawner, "legacySpawner cannot be null");

		CreatureSpawner spawner = legacySpawner.getBukkitSpawner();
		SkriptMobSpawnerData data = new SkriptMobSpawnerData();

		data.setActivationRange(spawner.getRequiredPlayerRange());
		data.setSpawnRange(spawner.getSpawnRange());
		if (SpawnerUtils.IS_RUNNING_1_20_3) {
			if (!spawner.getPotentialSpawns().isEmpty())
				data.setSpawnerEntries(spawner.getPotentialSpawns());
			else if (spawner.getSpawnedEntity() != null)
				data.setSpawnerEntitySnapshot(spawner.getSpawnedEntity());
		}

		if (spawner.getSpawnedType() != null)
			data.setSpawnerType(EntityUtils.toSkriptEntityData(spawner.getSpawnedType()));

		data.setMaxNearbyEntities(spawner.getMaxNearbyEntities());
		data.setSpawnCount(spawner.getSpawnCount());
		data.setMaxSpawnDelay(new Timespan(TimePeriod.TICK, spawner.getMaxSpawnDelay()));
		data.setMinSpawnDelay(new Timespan(TimePeriod.TICK, spawner.getMinSpawnDelay()));

		return data;
	}
	//</editor-fold>

	/**
	 * Applies this SkriptSpawnerData to the given spawners.
	 *
	 * @param skriptSpawners the spawners to apply the data to
	 */
	public void applyDataToSpawner(@NotNull SkriptMobSpawner[] skriptSpawners) {
		Preconditions.checkNotNull(skriptSpawners, "skriptSpawners cannot be null");
		for (SkriptMobSpawner skriptSpawner : skriptSpawners) {
			applyDataToSpawner(skriptSpawner);
		}
	}

	/**
	 * Applies this SkriptSpawnerData to the given spawner.
	 *
	 * @param skriptSpawner the spawner to apply the data to
	 */
	public void applyDataToSpawner(@NotNull SkriptMobSpawner skriptSpawner) {
		Preconditions.checkNotNull(skriptSpawner, "skriptSpawner cannot be null");

		Spawner spawner = skriptSpawner.getBukkitSpawner();
		SpawnerUtils.applySpawnerDataToSpawner(this, spawner);

		spawner.setMaxNearbyEntities(getMaxNearbyEntities());
		spawner.setSpawnCount(getSpawnCount());
		spawner.setMaxSpawnDelay(Math.clamp(getMaxSpawnDelay().getAs(TimePeriod.TICK), 0, Integer.MAX_VALUE));
		spawner.setMinSpawnDelay(Math.clamp(getMinSpawnDelay().getAs(TimePeriod.TICK), 0, Integer.MAX_VALUE));

		if (spawner instanceof CreatureSpawner creatureSpawner)
			creatureSpawner.update(true, false);
	}

	//<editor-fold desc="Legacy spawner handling" defaultstate="collapsed">
	/**
	 * Applies this SkriptSpawnerData to the given legacy spawners.
	 * @param legacySpawners the legacy spawners to apply the data to
	 */
	public void applyDataToLegacySpawners(@NotNull LegacySkriptMobSpawner[] legacySpawners) {
		Preconditions.checkNotNull(legacySpawners, "legacySpawners cannot be null");
		for (LegacySkriptMobSpawner legacySpawner : legacySpawners) {
			applyDataToLegacySpawner(legacySpawner);
		}
	}

	/**
	 * Applies this SkriptSpawnerData to the given legacy spawner.
	 *
	 * @param legacySpawner the legacy spawner to apply the data to
	 */
	public void applyDataToLegacySpawner(@NotNull LegacySkriptMobSpawner legacySpawner) {
		Preconditions.checkNotNull(legacySpawner, "legacySpawner cannot be null");

		CreatureSpawner spawner = legacySpawner.getBukkitSpawner();

		spawner.setRequiredPlayerRange(getActivationRange());
		spawner.setSpawnRange(getSpawnRange());

		if (!getSpawnerEntries().isEmpty())
			spawner.setPotentialSpawns(getSpawnerEntries());
		else if (getSpawnerEntitySnapshot() != null)
			spawner.setSpawnedEntity(getSpawnerEntitySnapshot());
		else
			spawner.setSpawnedType(EntityUtils.toBukkitEntityType(getSpawnerType()));

		spawner.setMaxNearbyEntities(getMaxNearbyEntities());
		spawner.setSpawnCount(getSpawnCount());
		spawner.setMaxSpawnDelay(Math.clamp(getMaxSpawnDelay().getAs(TimePeriod.TICK), 0, Integer.MAX_VALUE));
		spawner.setMinSpawnDelay(Math.clamp(getMinSpawnDelay().getAs(TimePeriod.TICK), 0, Integer.MAX_VALUE));

		spawner.update(true, false);
	}
	//</editor-fold>

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
	 * The maximum spawn delay is always greater than or equal to the minimum spawn delay.
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
	 * The maximum spawn delay must be greater than or equal to the minimum spawn delay.
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
	 * The minimum spawn delay is always less than or equal to the maximum spawn delay.
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

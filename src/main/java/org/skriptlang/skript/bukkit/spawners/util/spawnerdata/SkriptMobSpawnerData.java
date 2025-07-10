package org.skriptlang.skript.bukkit.spawners.util.spawnerdata;

import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import ch.njol.yggdrasil.YggdrasilSerializable;
import com.google.common.base.Preconditions;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.spawner.Spawner;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;

/**
 * Represents the data of a {@link Spawner}, which may be a {@link CreatureSpawner} or a {@link SpawnerMinecart}
 *
 * @see SkriptTrialSpawnerData
 * @see SkriptSpawnerData
 */
public class SkriptMobSpawnerData extends SkriptSpawnerData implements YggdrasilSerializable {

	private int maxNearbyEntityCap = SpawnerUtils.DEFAULT_MAX_NEARBY_ENTITIES;
	private int spawnCount = SpawnerUtils.DEFAULT_SPAWN_COUNT;

	/**
	 * Creates a new SkriptSpawnerData instance from the given Bukkit {@link Spawner}.
	 * @param spawner the Bukkit spawner to convert
	 * @return a new SkriptSpawnerData instance containing the data from the Bukkit spawner
	 */
	public static SkriptMobSpawnerData fromSpawner(@NotNull Spawner spawner) {
		Preconditions.checkNotNull(spawner, "spawner cannot be null");

		SkriptMobSpawnerData data = new SkriptMobSpawnerData();

		SkriptSpawnerData.applyToSpawnerData(spawner, data);
		data.setMaxNearbyEntityCap(spawner.getMaxNearbyEntities());
		data.setSpawnCount(spawner.getSpawnCount());
		data.setMaxSpawnDelay(new Timespan(TimePeriod.TICK, spawner.getMaxSpawnDelay()));
		data.setMinSpawnDelay(new Timespan(TimePeriod.TICK, spawner.getMinSpawnDelay()));

		return data;
	}

	//<editor-fold desc="Legacy spawner handling" defaultstate="collapsed">
	/**
	 * Creates a new SkriptSpawnerData instance from the given creature spawner.
	 * This is used for versions under 1.21 to allow legacy spawner support.
	 * @param creatureSpawner the creature spawner to convert
	 * @return a new SkriptSpawnerData instance containing the data from the legacy spawner
	 */
	public static SkriptMobSpawnerData fromSpawner(@NotNull CreatureSpawner creatureSpawner) {
		Preconditions.checkNotNull(creatureSpawner, "creatureSpawner cannot be null");

		SkriptMobSpawnerData data = new SkriptMobSpawnerData();

		data.setActivationRange(creatureSpawner.getRequiredPlayerRange());
		data.setSpawnRange(creatureSpawner.getSpawnRange());
		data.setSpawnerEntries(creatureSpawner.getPotentialSpawns());

		data.setMaxNearbyEntityCap(creatureSpawner.getMaxNearbyEntities());
		data.setSpawnCount(creatureSpawner.getSpawnCount());
		data.setMaxSpawnDelay(new Timespan(TimePeriod.TICK, creatureSpawner.getMaxSpawnDelay()));
		data.setMinSpawnDelay(new Timespan(TimePeriod.TICK, creatureSpawner.getMinSpawnDelay()));

		return data;
	}
	//</editor-fold>

	/**
	 * Applies this SkriptSpawnerData to the given spawners.
	 * @param spawners the spawners to apply the data to
	 */
	public void applyDataToSpawners(@NotNull Spawner[] spawners) {
		Preconditions.checkNotNull(spawners, "spawners cannot be null");
		for (Spawner spawner : spawners) {
			applyDataToSpawner(spawner);
		}
	}

	/**
	 * Applies this SkriptSpawnerData to the given spawner.
	 * @param spawner the spawner to apply the data to
	 */
	public void applyDataToSpawner(@NotNull Spawner spawner) {
		Preconditions.checkNotNull(spawner, "spawner cannot be null");

		super.applyToSpawner(spawner);
		spawner.setMaxNearbyEntities(getMaxNearbyEntityCap());
		spawner.setSpawnCount(getSpawnCount());
		spawner.setMaxSpawnDelay(Math.clamp(getMaxSpawnDelay().getAs(TimePeriod.TICK), 0, Integer.MAX_VALUE));
		spawner.setMinSpawnDelay(Math.clamp(getMinSpawnDelay().getAs(TimePeriod.TICK), 0, Integer.MAX_VALUE));

		if (spawner instanceof CreatureSpawner creatureSpawner)
			creatureSpawner.update(true, false);
	}

	//<editor-fold desc="Legacy spawner handling" defaultstate="collapsed">
	/**
	 * Applies this SkriptSpawnerData to the given creature spawners.
	 * This is used for versions under 1.21 to allow legacy spawner support.
	 * @param creatureSpawners the creature spawners to apply the data to
	 */
	public void applyDataToSpawners(@NotNull CreatureSpawner[] creatureSpawners) {
		Preconditions.checkNotNull(creatureSpawners, "creatureSpawners cannot be null");
		for (CreatureSpawner creatureSpawner : creatureSpawners) {
			applyDataToSpawner(creatureSpawner);
		}
	}

	/**
	 * Applies this SkriptSpawnerData to the given creature spawner.
	 * * This is used for versions under 1.21 to allow legacy spawner support.
	 * @param creatureSpawner the creature spawner to apply the data to
	 */
	public void applyDataToSpawner(@NotNull CreatureSpawner creatureSpawner) {
		Preconditions.checkNotNull(creatureSpawner, "creatureSpawner cannot be null");

		creatureSpawner.setRequiredPlayerRange(getActivationRange());
		creatureSpawner.setSpawnRange(getSpawnRange());

		if (!getSpawnerEntries().isEmpty())
			creatureSpawner.setPotentialSpawns(getSpawnerEntries());

		creatureSpawner.setMaxNearbyEntities(getMaxNearbyEntityCap());
		creatureSpawner.setSpawnCount(getSpawnCount());
		creatureSpawner.setMaxSpawnDelay(Math.clamp(getMaxSpawnDelay().getAs(TimePeriod.TICK), 0, Integer.MAX_VALUE));
		creatureSpawner.setMinSpawnDelay(Math.clamp(getMinSpawnDelay().getAs(TimePeriod.TICK), 0, Integer.MAX_VALUE));

		creatureSpawner.update(true, false);
	}
	//</editor-fold>

	/**
	 * Returns the maximum number of nearby similar entities that can be spawned by this spawner.
	 * <p>
	 * The default value is 6.
	 * @return the maximum nearby entity cap
	 */
	public int getMaxNearbyEntityCap() {
		return maxNearbyEntityCap;
	}

	/**
	 * Sets the maximum number of nearby similar entities that can be spawned by this spawner.
	 * <p>
	 * The default value is 6.
	 * @param maxNearbyEntityCap the maximum nearby entity cap
	 */
	public void setMaxNearbyEntityCap(int maxNearbyEntityCap) {
		this.maxNearbyEntityCap = maxNearbyEntityCap;
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

}

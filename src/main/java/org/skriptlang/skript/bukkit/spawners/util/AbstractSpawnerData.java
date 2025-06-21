package org.skriptlang.skript.bukkit.spawners.util;

import ch.njol.skript.entity.EntityData;
import com.google.common.base.Preconditions;
import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.entity.EntitySnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing the data of a spawner.
 */
public abstract class AbstractSpawnerData {

	private int activationRange = SpawnerUtils.DEFAULT_ACTIVATION_RANGE;
	private int spawnRange = SpawnerUtils.DEFAULT_SPAWN_RANGE;

	private @Nullable EntitySnapshot spawnerEntitySnapshot = null;
	private @Nullable EntityData<?> spawnerType = null;
	private @NotNull List<SpawnerEntry> spawnerEntries = new ArrayList<>();

	/**
	 * The distance a player must be from the spawner for it to be activated.
	 * <p>
	 * If the value is less than or equal to 0, the spawner will always be active (given that there are players online).
	 * <p>
	 * The default value is 16.
	 * @return the activation range
	 */
	public int getActivationRange() {
		return activationRange;
	}

	/**
	 * Sets the distance a player must be from the spawner for it to be activated.
	 * <p>
	 * If the value is less than or equal to 0, the spawner will always be active (given that there are players online).
	 * <p>
	 * The default value is 16.
	 * @param activationRange the activation range
	 */
	public void setActivationRange(int activationRange) {
		this.activationRange = activationRange;
	}

	/**
	 * Gets the radius around which the spawner will attempt to spawn mobs.
	 * <p>
	 * This area is square, includes the block the spawner is in, and is centered on the spawner's x, z coordinates - not the spawner itself.
	 * <p>
	 * The default value is 4.
	 * @return the spawn range
	 */
	public int getSpawnRange() {
		return spawnRange;
	}

	/**
	 * Sets the radius around which the spawner will attempt to spawn mobs.
	 * <p>
	 * This area is square, includes the block the spawner is in, and is centered on the spawner's x, z coordinates - not the spawner itself.
	 * <p>
	 * The default value is 4.
	 * @param spawnRange the spawn range
	 */
	public void setSpawnRange(int spawnRange) {
		Preconditions.checkArgument(spawnRange > 0, "Spawn range must be > 0");
		this.spawnRange = spawnRange;
	}

	/**
	 * Gets the entity snapshot of the spawner entity, if it exists.
	 * <p>
	 * If this is not null, the spawner will spawn the entity represented by this snapshot.
	 * @return the spawner entity snapshot, or null if not set
	 */
	public @Nullable EntitySnapshot getSpawnerEntitySnapshot() {
		return spawnerEntitySnapshot;
	}

	/**
	 * Sets the entity snapshot of the spawner entity.
	 * <p>
	 * If this is not null, the spawner will spawn the entity represented by this snapshot.
	 * <p>
	 * This will override any existing entity data or spawner entries.
	 * @param entitySnapshot the spawner entity snapshot to set, or null to clear
	 */
	public void setSpawnerEntitySnapshot(@Nullable EntitySnapshot entitySnapshot) {
		spawnerEntitySnapshot = entitySnapshot;
		if (spawnerEntitySnapshot != null) {
			spawnerType = null;
			spawnerEntries.clear();
		}
	}

	/**
	 * Gets the type of entity that the spawner will spawn.
	 * <p>
	 * If this is not null, the spawner will spawn entities of the type represented by this EntityData.
	 * @return the spawner type, or null if not set
	 */
	public @Nullable EntityData<?> getSpawnerType() {
		return spawnerType;
	}

	/**
	 * Sets the type of entity that the spawner will spawn.
	 * <p>
	 * If this is not null, the spawner will spawn entities of the type represented by this EntityData.
	 * <p>
	 * This will override any existing entity snapshot or spawner entries.
	 * @param spawnerType the spawner type to set, or null to clear
	 */
	public void setSpawnerType(@Nullable EntityData<?> spawnerType) {
		this.spawnerType = spawnerType;
		if (spawnerType != null) {
			spawnerEntitySnapshot = null;
			spawnerEntries.clear();
		}
	}

	/**
	 * Gets the list of spawner entries that the spawner will use to spawn entities.
	 * <p>
	 * If this is not empty, the spawner will use these entries to determine what entities to spawn.
	 * @return a list of spawner entries, or an empty list
	 */
	public @NotNull List<SpawnerEntry> getSpawnerEntries() {
		return List.copyOf(spawnerEntries);
	}

	/**
	 * Sets the list of spawner entries that the spawner will use to spawn entities.
	 * <p>
	 * If this is not empty, the spawner will use these entries to determine what entities to spawn.
	 * <p>
	 * This will override any existing entity snapshot or spawner type.
	 * @param spawnerEntries the list of spawner entries to set, or an empty list to clear
	 */
	public void setSpawnerEntries(@NotNull List<SpawnerEntry> spawnerEntries) {
		Preconditions.checkNotNull(spawnerEntries, "spawnerEntries cannot be null");
		this.spawnerEntries = new ArrayList<>(spawnerEntries);
		spawnerEntitySnapshot = null;
	}

	/**
	 * Adds a specific spawner entry to the list of spawner entries.
	 * <p>
	 * This will override any existing entity snapshot or spawner type.
	 * @param spawnerEntry the spawner entry to add
	 */
	public void addSpawnerEntry(@NotNull SpawnerEntry spawnerEntry) {
		Preconditions.checkNotNull(spawnerEntry, "spawnerEntry cannot be null");
		spawnerEntries.add(spawnerEntry);

		spawnerEntitySnapshot = null;
		spawnerType = null;
	}

	/**
	 * Removes a specific spawner entry from the list of spawner entries.
	 * @param spawnerEntry the spawner entry to remove
	 */
	public void removeSpawnerEntry(@NotNull SpawnerEntry spawnerEntry) {
		Preconditions.checkNotNull(spawnerEntry, "spawnerEntry cannot be null");
		spawnerEntries.remove(spawnerEntry);
	}

	/**
	 * Clears the list of spawner entries.
	 * <p>
	 * This will remove all entries and set the spawner to not use any entries for spawning.
	 */
	public void clearSpawnerEntries() {
		spawnerEntries.clear();
	}

}

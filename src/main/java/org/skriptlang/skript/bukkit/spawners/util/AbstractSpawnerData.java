package org.skriptlang.skript.bukkit.spawners.util;

import ch.njol.skript.entity.EntityData;
import com.google.common.base.Preconditions;
import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.entity.EntitySnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSpawnerData {

	private int requiredPlayerRange = SpawnerUtils.DEFAULT_REQUIRED_PLAYER_RANGE;
	private int spawnRange = SpawnerUtils.DEFAULT_SPAWN_RANGE;

	private @Nullable EntitySnapshot spawnerEntitySnapshot = null;
	private @Nullable EntityData<?> entityData = null;
	private @NotNull List<SpawnerEntry> spawnerEntries = new ArrayList<>();

	public int getRequiredPlayerRange() {
		return requiredPlayerRange;
	}

	public void setRequiredPlayerRange(int requiredPlayerRange) {
		Preconditions.checkArgument(requiredPlayerRange > 0, "Required player range must be > 0");
		this.requiredPlayerRange = requiredPlayerRange;
	}

	public int getSpawnRange() {
		return spawnRange;
	}

	public void setSpawnRange(int spawnRange) {
		Preconditions.checkArgument(spawnRange > 0, "Spawn range must be > 0");
		this.spawnRange = spawnRange;
	}

	public @Nullable EntitySnapshot getSpawnerEntitySnapshot() {
		return spawnerEntitySnapshot;
	}

	public void setSpawnerEntitySnapshot(@Nullable EntitySnapshot entitySnapshot) {
		spawnerEntitySnapshot = entitySnapshot;
		spawnerEntries.clear();
	}

	public @Nullable EntityData<?> getSpawnerType() {
		return entityData;
	}

	public void setSpawnerType(@Nullable EntityData<?> entityData) {
		this.entityData = entityData;
		spawnerEntitySnapshot = null;
		spawnerEntries.clear();
	}

	public @NotNull List<SpawnerEntry> getSpawnerEntries() {
		return List.copyOf(spawnerEntries);
	}

	public void setSpawnerEntries(@NotNull List<SpawnerEntry> spawnerEntries) {
		Preconditions.checkNotNull(spawnerEntries, "Spawner entries cannot be null");
		this.spawnerEntries = spawnerEntries;
		spawnerEntitySnapshot = null;
	}

	public void addSpawnerEntry(@NotNull SpawnerEntry spawnerEntry) {
		Preconditions.checkNotNull(spawnerEntry, "Spawner entry cannot be null");
		spawnerEntries.add(spawnerEntry);
		spawnerEntitySnapshot = null;
	}

}

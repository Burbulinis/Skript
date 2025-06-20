package org.skriptlang.skript.bukkit.spawners.util;

import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.entity.EntitySnapshot;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SkriptSpawnerData extends BaseSpawnerData {

	private Set<Object> assignedSpawners;
	private boolean autoUpdating = true;

	private List<SpawnerEntry> spawnerEntries = new ArrayList<>();
	private EntitySnapshot spawnerEntity;

	public @NotNull Set<Object> getAssignedSpawners() {
		return Set.copyOf(assignedSpawners);
	}

	public void setAssignedSpawners(@NotNull Set<Object> spawners, boolean update) {
		this.assignedSpawners = spawners;
		if (update)
			updateAssignedSpawners();
	}

	public void assignSpawners(@NotNull Set<Object> spawners, boolean update) {
		assignedSpawners.addAll(spawners);
		if (update)
			updateAssignedSpawners();
	}

	public void assignSpawner(@NotNull Object spawner, boolean update) {
		assignedSpawners.add(spawner);
		if (update)
			updateAssignedSpawners();
	}

	public SpawnerEntry @NotNull [] getSpawnerEntries() {
		return spawnerEntries.toArray(SpawnerEntry[]::new);
	}

	public void setSpawnerEntries(@NotNull List<SpawnerEntry> spawnerEntries) {
		this.spawnerEntries = spawnerEntries;
	}

	public void addSpawnerEntry(@NotNull SpawnerEntry spawnerEntry) {
		spawnerEntries.add(spawnerEntry);
	}

	public void removeSpawnerEntry(@NotNull SpawnerEntry spawnerEntry) {
		spawnerEntries.remove(spawnerEntry);
	}

	public EntitySnapshot getSpawnerEntity() {
		return spawnerEntity;
	}

	public void setSpawnerEntity(EntitySnapshot spawnerEntity) {

	}

	public void updateAssignedSpawners() {

	}

	public boolean isAutoUpdating() {
		return autoUpdating;
	}

	public void setAutoUpdating(boolean autoUpdating) {
		this.autoUpdating = autoUpdating;
	}

}

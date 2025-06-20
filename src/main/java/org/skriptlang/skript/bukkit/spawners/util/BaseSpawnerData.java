package org.skriptlang.skript.bukkit.spawners.util;

import com.google.common.base.Preconditions;
import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class BaseSpawnerData {

	private int requiredPlayerRange = SpawnerUtils.DEFAULT_REQUIRED_PLAYER_RANGE;
	private @Nullable SpawnerEntry spawnerEntity = null;
	private @Nullable EntityType entityType = null;

	private boolean autoUpdatingFlag = true;
	private Set<Spawner> assignedSpawners;

	private boolean canUpdate() {
		return autoUpdatingFlag && assignedSpawners != null && !assignedSpawners.isEmpty();
	}

	@NotNull Set<Spawner> getAssignedSpawners() {
		return Set.copyOf(assignedSpawners);
	}

	void setAssignedSpawners(@NotNull Set<Spawner> spawners) {
		assignedSpawners = spawners;

		if (canUpdate())
			updateAssignedSpawners();
	}

	boolean isAutoUpdating() {
		return autoUpdatingFlag;
	}

	void setAutoUpdating(boolean autoUpdating) {
		this.autoUpdatingFlag = autoUpdating;
	}

	int getRequiredPlayerRange() {
		return requiredPlayerRange;
	}

	void setRequiredPlayerRange(int requiredPlayerRange) {
		Preconditions.checkArgument(requiredPlayerRange > 0, "Required player range must be > 0");
		this.requiredPlayerRange = requiredPlayerRange;

		if (canUpdate())
			updateRequiredPlayerRange();
	}

	@Nullable SpawnerEntry getSpawnerEntity() {
		return spawnerEntity;
	}

	void setSpawnerEntity(@Nullable SpawnerEntry spawnerEntry) {
		this.spawnerEntity = spawnerEntry;

		if (canUpdate())
			updateSpawnerEntity();
	}

	@Nullable EntityType getSpawnerType() {
		return entityType;
	}

	void setSpawnerType(@Nullable EntityType entityType) {
		this.entityType = entityType;

		if (autoUpdatingFlag)
			updateSpawnerType();
	}

	void getSpawnerEntries() {

	}

	void setSpawnerEntries(Set<SpawnerEntry> spawnerEntries);

	abstract void updateRequiredPlayerRange();

	abstract void updateSpawnerEntity();

	abstract void updateSpawnerType();

	abstract void updateAssignedSpawners();
}

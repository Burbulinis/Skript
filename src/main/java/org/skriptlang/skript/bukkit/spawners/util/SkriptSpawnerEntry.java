package org.skriptlang.skript.bukkit.spawners.util;

import ch.njol.skript.lang.util.common.AnyWeighted;
import com.google.common.base.Preconditions;
import org.bukkit.block.spawner.SpawnRule;
import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.block.spawner.SpawnerEntry.Equipment;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkriptSpawnerEntry implements AnyWeighted {

	private @NotNull EntitySnapshot entitySnapshot;
	private @Nullable SpawnRule spawnRule;
	private @Nullable LootTable equipmentLootTable;
	private @NotNull List<SkriptEquipmentDropChance> equipmentDropChances = new ArrayList<>();

	private int weight = 1;

	public SkriptSpawnerEntry(@NotNull EntitySnapshot entitySnapshot) {
		Preconditions.checkNotNull(entitySnapshot, "snapshot cannot be null");
		this.entitySnapshot = entitySnapshot;
	}

	public static SkriptSpawnerEntry fromSpawnerEntry(@NotNull SpawnerEntry entry) {
		Preconditions.checkNotNull(entry, "entry cannot be null");

		SkriptSpawnerEntry skriptEntry = new SkriptSpawnerEntry(entry.getSnapshot());
		skriptEntry.setWeight(entry.getSpawnWeight());
		skriptEntry.setSpawnRule(entry.getSpawnRule());

		Equipment equipment = entry.getEquipment();
		if (equipment != null) {
			skriptEntry.setEquipmentLootTable(equipment.getEquipmentLootTable());
			skriptEntry.setEquipmentDropChances(
				equipment.getDropChances().entrySet().stream()
					.map(chance -> new SkriptEquipmentDropChance(chance.getKey(), chance.getValue()))
					.toList()
			);
		}

		return skriptEntry;
	}

	public static SpawnerEntry toSpawnerEntry(@NotNull SkriptSpawnerEntry skriptEntry) {
		Preconditions.checkNotNull(skriptEntry, "skriptEntry cannot be null");

		SpawnerEntry entry = new SpawnerEntry(
			skriptEntry.getEntitySnapshot(),
			skriptEntry.weight().intValue(),
			skriptEntry.getSpawnRule()
		);

		LootTable lootTable = skriptEntry.getEquipmentLootTable();
		List<SkriptEquipmentDropChance> dropChances = skriptEntry.getEquipmentDropChances();
		if (lootTable != null && !dropChances.isEmpty()) {
			Map<EquipmentSlot, Float> map = dropChances.stream()
				.collect(Collectors.toMap(SkriptEquipmentDropChance::slot, SkriptEquipmentDropChance::chance));
			entry.setEquipment(new Equipment(lootTable, map));
		}

		return entry;
	}

	@Override
	public @UnknownNullability Number weight() {
		return weight;
	}

	@Override
	public boolean supportsWeightChange() {
		return true;
	}

	@Override
	public void setWeight(Number weight) {
		this.weight = weight.intValue();
	}

	public @NotNull EntitySnapshot getEntitySnapshot() {
		return entitySnapshot;
	}

	public void setEntitySnapshot(@NotNull EntitySnapshot entitySnapshot) {
		Preconditions.checkNotNull(entitySnapshot, "snapshot cannot be null");
		this.entitySnapshot = entitySnapshot;
	}

	public @Nullable SpawnRule getSpawnRule() {
		return spawnRule;
	}

	public void setSpawnRule(@Nullable SpawnRule spawnRule) {
		this.spawnRule = spawnRule;
	}

	public @Nullable LootTable getEquipmentLootTable() {
		return equipmentLootTable;
	}

	public void setEquipmentLootTable(@Nullable LootTable equipmentLootTable) {
		this.equipmentLootTable = equipmentLootTable;
	}

	public @NotNull List<SkriptEquipmentDropChance> getEquipmentDropChances() {
		return List.copyOf(equipmentDropChances);
	}

	public void setEquipmentDropChances(@NotNull List<SkriptEquipmentDropChance> dropChances) {
		Preconditions.checkNotNull(dropChances, "drop chances cannot be null");
		this.equipmentDropChances = new ArrayList<>(dropChances);
	}

	public void addEquipmentDropChances(@NotNull List<SkriptEquipmentDropChance> dropChances) {
		Preconditions.checkNotNull(dropChances, "drop chances cannot be null");
		this.equipmentDropChances.addAll(dropChances);
	}

	public void addEquipmentDropChance(@NotNull SkriptEquipmentDropChance dropChance) {
		Preconditions.checkNotNull(dropChance, "drop chance cannot be null");
		this.equipmentDropChances.add(dropChance);
	}

	public void removeEquipmentDropChances(@NotNull List<SkriptEquipmentDropChance> dropChances) {
		Preconditions.checkNotNull(dropChances, "drop chances cannot be null");
		this.equipmentDropChances.removeAll(dropChances);
	}

	public void removeEquipmentDropChance(@NotNull SkriptEquipmentDropChance dropChance) {
		Preconditions.checkNotNull(dropChance, "drop chance cannot be null");
		this.equipmentDropChances.remove(dropChance);
	}

	public void clearEquipmentDropChances() {
		this.equipmentDropChances.clear();
	}

}

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

import java.util.HashMap;
import java.util.Map;

public class SkriptSpawnerEntry implements AnyWeighted {

	private @NotNull EntitySnapshot entitySnapshot;
	private @Nullable SpawnRule spawnRule;
	private @Nullable LootTable equipmentLootTable;
	private @NotNull Map<EquipmentSlot, Float> dropChances = new HashMap<>();

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
			skriptEntry.setDropChances(equipment.getDropChances());
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
		Map<EquipmentSlot, Float> dropChances = skriptEntry.getDropChances();
		if (lootTable != null)
			entry.setEquipment(new Equipment(lootTable, dropChances));

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

	public @NotNull Map<EquipmentSlot, Float> getDropChances() {
		return Map.copyOf(dropChances);
	}

	public void setDropChances(@NotNull Map<EquipmentSlot, Float> dropChances) {
		Preconditions.checkNotNull(dropChances, "dropChances cannot be null");
		this.dropChances = new HashMap<>(dropChances);
	}

	public void setDropChance(@NotNull EquipmentSlot slot, float chance) {
		Preconditions.checkNotNull(slot, "slot cannot be null");
		this.dropChances.put(slot, chance);
	}

	public void removeDropChance(@NotNull EquipmentSlot slot) {
		Preconditions.checkNotNull(slot, "slot cannot be null");
		this.dropChances.remove(slot);
	}

	public void clearDropChances() {
		this.dropChances.clear();
	}

}

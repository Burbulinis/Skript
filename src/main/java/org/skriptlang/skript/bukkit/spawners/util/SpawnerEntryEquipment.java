package org.skriptlang.skript.bukkit.spawners.util;

import org.bukkit.block.spawner.SpawnerEntry.Equipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A wrapper for {@link Equipment} to allow the usage of {@link DropChance}.
 */
public class SpawnerEntryEquipment {

	private @NotNull LootTable lootTable;
	private @NotNull List<DropChance> drops;
	private transient @Nullable Equipment cachedBukkitEquipment;

	public SpawnerEntryEquipment(@NotNull LootTable lootTable, @NotNull List<DropChance> drops) {
		this.lootTable = lootTable;
		this.drops = drops;
	}

	public Equipment getAsBukkitEquipment() {
		if (cachedBukkitEquipment == null) {
			// conversion to map for bukkit's Equipment constructor
			Map<EquipmentSlot, Float> dropChances = new HashMap<>();
			for (DropChance chance : this.drops) {
				dropChances.put(chance.getEquipmentSlot(), chance.getDropChance());
			}
			cachedBukkitEquipment = new Equipment(lootTable, dropChances);
		}
		return cachedBukkitEquipment;
	}

	public @NotNull LootTable getLootTable() {
		return lootTable;
	}

	public @NotNull List<DropChance> getDropChances() {
		return drops;
	}

	public void setLootTable(@NotNull LootTable lootTable) {
		cachedBukkitEquipment = null;
		this.lootTable = lootTable;
	}

	public void setDropChances(@NotNull List<DropChance> drops) {
		cachedBukkitEquipment = null;
		this.drops = drops;
	}

	public void addDropChance(@NotNull SpawnerEntryEquipment.DropChance dropChance) {
		cachedBukkitEquipment = null;
		this.drops.add(dropChance);
	}

	public void removeDropChance(@NotNull SpawnerEntryEquipment.DropChance dropChance) {
		cachedBukkitEquipment = null;
		this.drops.remove(dropChance);
	}

	/**
	 * A helper class to represent the drop chance for a specific equipment slot.
	 */
	public static class DropChance {

		private @NotNull EquipmentSlot slot;
		private float dropChance;

		public DropChance(@NotNull EquipmentSlot slot, float dropChance) {
			this.slot = slot;
			this.dropChance = dropChance;
		}

		public @NotNull EquipmentSlot getEquipmentSlot() {
			return slot;
		}

		public float getDropChance() {
			return dropChance;
		}

		public void setEquipmentSlot(@NotNull EquipmentSlot slot) {
			this.slot = slot;
		}

		public void setDropChance(float dropChance) {
			this.dropChance = dropChance;
		}

	}

}

package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawnerentry.equipment;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.SpawnerModule;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerEntryEquipment;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Spawner Entry - Equipment with Loot Table")
@Description("Returns the equipment loot table of a spawner entry equipment.")
@Examples("set {_loot table} to spawner loot table of {_equipment}")
@Since("INSERT VERSION")
public class ExprEquipmentWithLootTable extends SimplePropertyExpression<SpawnerEntryEquipment, LootTable> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(ExprEquipmentWithLootTable.class, LootTable.class,
			"spawner equipment loot[ ]table[s]", "spawnerentryequipments", false)
				.supplier(ExprEquipmentWithLootTable::new)
				.build()
		);
	}

	@Override
	public @NotNull LootTable convert(SpawnerEntryEquipment equipment) {
		return equipment.getLootTable();
	}

	@Override
	public Class<? extends LootTable> getReturnType() {
		return LootTable.class;
	}

	@Override
	protected String getPropertyName() {
		return "spawner equipment loot table";
	}

}

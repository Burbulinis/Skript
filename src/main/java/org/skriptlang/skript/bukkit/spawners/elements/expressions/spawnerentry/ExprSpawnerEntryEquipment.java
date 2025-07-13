package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawnerentry;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SkriptSpawnerEntry;
import org.skriptlang.skript.registration.SyntaxRegistry;

public class ExprSpawnerEntryEquipment extends SimplePropertyExpression<SkriptSpawnerEntry, LootTable> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(ExprSpawnerEntryEquipment.class, LootTable.class,
			"spawner entry equipment[s]", "spawnerentries", true)
				.supplier(ExprSpawnerEntryEquipment::new)
				.build()
		);
	}

	@Override
	public @Nullable LootTable convert(SkriptSpawnerEntry entry) {
		return entry.getEquipmentLootTable();
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, DELETE -> CollectionUtils.array(LootTable.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		LootTable lootTable = delta != null ? (LootTable) delta[0] : null;
		for (SkriptSpawnerEntry entry : getExpr().getArray(event)) {
			entry.setEquipmentLootTable(lootTable);
		}
	}

	@Override
	public Class<? extends LootTable> getReturnType() {
		return LootTable.class;
	}

	@Override
	protected String getPropertyName() {
		return "spawner entry equipment";
	}

}

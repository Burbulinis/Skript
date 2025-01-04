package org.skriptlang.skript.bukkit.spawner.elements.expressions.spawnerentry.equipment.chances;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawner.SpawnerModule;
import org.skriptlang.skript.bukkit.spawner.util.SpawnerEntryEquipmentWrapper.DropChance;

public class ExprSpawnerEntryEquipmentWithSlot extends SimplePropertyExpression<DropChance, EquipmentSlot> {

	static {
		register(SpawnerModule.SYNTAX_REGISTRY, ExprSpawnerEntryEquipmentWithSlot.class, EquipmentSlot.class,
			"equipment slot", "equipmentdropchances");
	}

	@Override
	public @NotNull EquipmentSlot convert(DropChance equipment) {
		return equipment.getEquipmentSlot();
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(EquipmentSlot.class);
		return null;
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		assert delta != null;
		EquipmentSlot slot = (EquipmentSlot) delta[0];

		for (DropChance chance : getExpr().getArray(event)) {
			chance.setEquipmentSlot(slot);
		}
	}

	@Override
	public Class<? extends EquipmentSlot> getReturnType() {
		return EquipmentSlot.class;
	}

	@Override
	protected String getPropertyName() {
		return "spawner entry equipment slot";
	}

}
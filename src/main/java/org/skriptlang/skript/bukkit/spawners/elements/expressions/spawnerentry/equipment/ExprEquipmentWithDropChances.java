package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawnerentry.equipment;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.SpawnerModule;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerEntryEquipment;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerEntryEquipment.DropChance;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxOrigin;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.ArrayList;
import java.util.List;

@Name("Spawner Entry - Equipment with DropChance")
@Description("Returns the drops of a spawner entry equipment.")
@Examples("set {_chances::*} to spawner drop chances of {_equipment}")
@Since("INSERT VERSION")
public class ExprEquipmentWithDropChances extends PropertyExpression<SpawnerEntryEquipment, DropChance> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(ExprEquipmentWithDropChances.class, DropChance.class,
			"drop chance[s]", "%spawnerentryequipments%", false)
				.supplier(ExprEquipmentWithDropChances::new)
				.build()
		);
	}

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		//noinspection unchecked
		setExpr((Expression<? extends SpawnerEntryEquipment>) exprs[0]);
		return true;
	}

	@Override
	protected DropChance[] get(Event event, SpawnerEntryEquipment[] source) {
		List<DropChance> drops = new ArrayList<>();
		for (SpawnerEntryEquipment equipment : source) {
			drops.addAll(equipment.getDropChances());
		}
		return drops.toArray(DropChance[]::new);
	}

	@Override
	public Class<? extends DropChance> getReturnType() {
		return DropChance.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "spawner entry drop chances of " + getExpr().toString(event, debug);
	}

}

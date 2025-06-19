package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawnerentry.equipment.chances;

import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.SpawnerModule;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerEntryEquipment.DropChance;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxOrigin;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Spawner Entry - Equipment DropChance")
@Description({
	"Returns a drop for an equipment slot with a specified drop chance. ",
	"This is used in spawner entry equipments to determine the drop chance of the equipment slot. "
		+ "The drop chance is a float value between 0 and 1, where 0 is 0% and 1 is 100%."
})
@Examples({
	"add helmet slot with drop chance 50% to {_drop chances::*}",
	"add chestplate slot with drop chance 75% to {_drop chances::*}",
	"set {_equipment} to loot table \"minecraft:equipment/trial_chamber\" with {_drop chances::*}"
})
@Since("INSERT VERSION")
@RequiredPlugins("MC 1.21+")
public class ExprEquipmentDrops extends SimpleExpression<DropChance> {

	static {
		var info = SyntaxInfo.Expression.builder(ExprEquipmentDrops.class, DropChance.class)
			.origin(SyntaxOrigin.of(SpawnerModule.ADDON))
			.supplier(ExprEquipmentDrops::new)
			.priority(SyntaxInfo.COMBINED)
			.addPattern("%equipmentslot% with %number% drop chance")
			.build();

		SpawnerModule.SYNTAX_REGISTRY.register(SyntaxRegistry.EXPRESSION, info);
	}

	private Expression<EquipmentSlot> slot;
	private Expression<Number> chance;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		slot = (Expression<EquipmentSlot>) exprs[0];
		chance = (Expression<Number>) exprs[1];
		return true;
	}

	@Override
	protected DropChance @Nullable [] get(Event event) {
		EquipmentSlot slot = this.slot.getSingle(event);
		if (slot == null)
			return null;

		Number chance = this.chance.getSingle(event);
		if (chance == null)
			return null;

		return new DropChance[]{new DropChance(slot, chance.floatValue())};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends DropChance> getReturnType() {
		return DropChance.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);

		builder.append("spawner entry drop chance for", slot, "with drop chance");
		if (chance != null) {
			builder.append(chance);
		} else {
			builder.append(1);
		}

		return builder.toString();
	}

}

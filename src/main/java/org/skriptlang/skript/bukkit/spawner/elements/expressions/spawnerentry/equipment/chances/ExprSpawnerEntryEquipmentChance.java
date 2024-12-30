package org.skriptlang.skript.bukkit.spawner.elements.expressions.spawnerentry.equipment.chances;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawner.SpawnerModule;
import org.skriptlang.skript.bukkit.spawner.util.SpawnerEquipmentWrapper.DropChance;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxOrigin;
import org.skriptlang.skript.registration.SyntaxRegistry;

public class ExprSpawnerEntryEquipmentChance extends SimpleExpression<DropChance> {

	static {
		var info = SyntaxInfo.Expression.builder(ExprSpawnerEntryEquipmentChance.class, DropChance.class)
			.origin(SyntaxOrigin.of(Skript.instance()))
			.supplier(ExprSpawnerEntryEquipmentChance::new)
			.priority(SyntaxInfo.COMBINED)
			.addPattern("[a] spawner [entry] [drop|equipment] chance (for|of) %equipmentslot% [with drop chance %-number%]")
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
			return new DropChance[0];

		float chance = 1f;
		if (this.chance != null) {
			Number number = this.chance.getSingle(event);
			if (number != null)
				chance = number.floatValue();
		}

		return new DropChance[]{new DropChance(slot, chance)};
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

		builder.append("spawner entry drop chance for")
			.append(slot)
			.append("with drop chance")
			.append(chance != null ? chance : 1);

		return builder.toString();
	}

}

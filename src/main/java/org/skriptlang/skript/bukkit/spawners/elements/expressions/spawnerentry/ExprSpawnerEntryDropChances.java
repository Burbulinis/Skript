package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawnerentry;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SkriptSpawnerEntry;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExprSpawnerEntryDropChances extends PropertyExpression<SkriptSpawnerEntry, Float> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprSpawnerEntryDropChances.class, Float.class)
			.supplier(ExprSpawnerEntryDropChances::new)
			.priority(DEFAULT_PRIORITY)
			.addPatterns(
				"[the] drop chance[s] [of %spawnerentries%] for %equipmentslots%",
				"%spawnerentries%'[s] drop chance[s] for %equipmentslots%")
			.build()
		);
	}

	private Expression<EquipmentSlot> slots;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		//noinspection unchecked
		setExpr((Expression<SkriptSpawnerEntry>) exprs[0]);
		//noinspection unchecked
		slots = (Expression<EquipmentSlot>) exprs[1];
		return true;
	}

	@Override
	protected Float[] get(Event event, SkriptSpawnerEntry[] source) {
		EquipmentSlot[] slots = this.slots.getArray(event);

		List<Float> dropChances = new ArrayList<>(slots.length * source.length);

		for (SkriptSpawnerEntry entry : source) {
			Map<EquipmentSlot, Float> dropChanceMap = entry.getDropChances();
			for (EquipmentSlot slot : slots) {
				Float chance = dropChanceMap.get(slot);
				if (chance == null)
					continue;

				dropChances.add(chance);
			}
		}

		return dropChances.toArray(Float[]::new);
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, ADD, REMOVE, DELETE -> CollectionUtils.array(Float.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		EquipmentSlot[] slots = this.slots.getArray(event);
		float chance = delta != null ? (float) delta[0] : 0;

		for (SkriptSpawnerEntry entry : getExpr().getArray(event)) {
			for (EquipmentSlot slot : slots) {
				float value = switch (mode) {
					case SET -> chance;
					case ADD -> entry.getDropChances().getOrDefault(slot, 0f) + chance;
					case REMOVE -> entry.getDropChances().getOrDefault(slot, 0f) - chance;
					default -> 0;
				};

				if (mode == ChangeMode.DELETE)
					entry.removeDropChance(slot);

				entry.setDropChance(slot, value);
			}
		}
	}

	@Override
	public Class<? extends Float> getReturnType() {
		return Float.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);
		builder.append("the drop chances of", getExpr(), "for", slots);
		return builder.toString();
	}

}

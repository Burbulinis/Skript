package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner.trialspawnerdata;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptTrialSpawnerData;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredPlugins("Minecraft 1.21+")
public class ExprRewardEntryWeight extends PropertyExpression<SkriptTrialSpawnerData, Integer> {

	public static void register(SyntaxRegistry registry) {
		if (!SpawnerUtils.IS_RUNNING_1_21)
			return;
		registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprRewardEntryWeight.class, Integer.class)
			.supplier(ExprRewardEntryWeight::new)
			.priority(DEFAULT_PRIORITY)
			.addPatterns(
				"[the] reward [entry] weight [of %trialspawnerdatas%] for %loottables%",
				"%trialspawnerdatas%'[s] reward [entry] weight for %loottables%")
			.build()
		);
	}

	private Expression<LootTable> lootTables;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		//noinspection unchecked
		setExpr((Expression<SkriptTrialSpawnerData>) exprs[0]);
		//noinspection unchecked
		lootTables = (Expression<LootTable>) exprs[1];
		return true;
	}

	@Override
	protected Integer[] get(Event event, SkriptTrialSpawnerData[] source) {
		LootTable[] lootTables = this.lootTables.getArray(event);

		List<Integer> weights = new ArrayList<>(lootTables.length * source.length);

		for (SkriptTrialSpawnerData data : source) {
			Map<LootTable, Integer> weightedMap = data.getRewardEntries();
			for (LootTable lootTable : lootTables) {
				Integer weight = weightedMap.get(lootTable);
				if (weight == null)
					continue;

				weights.add(weight);
			}
		}

		return weights.toArray(Integer[]::new);
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, ADD, REMOVE, RESET -> CollectionUtils.array(Integer.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		LootTable[] lootTables = this.lootTables.getArray(event);
		int weight = delta != null ? (int) delta[0] : 1;

		for (SkriptTrialSpawnerData data : getExpr().getArray(event)) {
			for (LootTable lootTable : lootTables) {
				data.setRewardEntry(lootTable, switch (mode) {
					case SET, RESET -> weight;
					case ADD -> Optional.of(data.getRewardWeight(lootTable)).orElse(0) + weight;
					case REMOVE -> Optional.of(data.getRewardWeight(lootTable)).orElse(0) - weight;
					default -> 1;
				});
			}
		}
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);
		builder.append("the reward weight of", getExpr(), "for", lootTables);
		return builder.toString();
	}

}

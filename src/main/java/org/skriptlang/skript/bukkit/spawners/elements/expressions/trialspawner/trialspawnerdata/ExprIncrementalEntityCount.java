package org.skriptlang.skript.bukkit.spawners.elements.expressions.trialspawner.trialspawnerdata;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptTrialSpawnerData;
import org.skriptlang.skript.registration.SyntaxRegistry;

public class ExprIncrementalEntityCount extends SimplePropertyExpression<SkriptTrialSpawnerData, Integer> {

	public static void register(SyntaxRegistry registry) {
		registerDefault(registry, ExprIncrementalEntityCount.class, Integer.class,
			"(incremental|additional) [concurrent:(concurrent|simultaneous)] (mob|entity) [spawn] (count|amount)",
			"trialspawnerdatas"
		);
	}

	private boolean concurrent;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		concurrent = parseResult.hasTag("concurrent");
		return super.init(expressions, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public @Nullable Integer convert(SkriptTrialSpawnerData data) {
		if (concurrent)
			return data.getConcurrentMobAmountIncrement();
		return data.getBaseMobAmountIncrement();
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
		int count = delta != null ? ((int) delta[0]) : 0;
		for (SkriptTrialSpawnerData data : getExpr().getArray(event)) {
			if (concurrent) {
				switch (mode) {
					case SET -> data.setConcurrentMobAmountIncrement(count);
					case ADD -> data.setConcurrentMobAmountIncrement(data.getConcurrentMobAmountIncrement() + count);
					case REMOVE -> data.setConcurrentMobAmountIncrement(data.getConcurrentMobAmountIncrement() - count);
					case RESET -> data.setConcurrentMobAmountIncrement(SpawnerUtils.DEFAULT_CONCURRENT_PER_PLAYER_INCREMENT);
				}
			} else {
				switch (mode) {
					case SET -> data.setBaseMobAmountIncrement(count);
					case ADD -> data.setBaseMobAmountIncrement(data.getBaseMobAmountIncrement() + count);
					case REMOVE -> data.setBaseMobAmountIncrement(data.getBaseMobAmountIncrement() - count);
					case RESET -> data.setBaseMobAmountIncrement(SpawnerUtils.DEFAULT_BASE_PER_PLAYER_INCREMENT);
				}
			}
		}
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	protected String getPropertyName() {
		return "incremental" + (concurrent ? " concurrent " : " ") + "mob spawn count";
	}

}

package org.skriptlang.skript.bukkit.spawners.elements.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SkriptMobSpawner;
import org.skriptlang.skript.bukkit.spawners.util.SkriptTrialSpawner;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.ArrayList;
import java.util.List;

public class ExprSpawner extends SimpleExpression<Object> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprSpawner.class, Object.class)
			.supplier(ExprSpawner::new)
			.priority(SyntaxInfo.COMBINED)
			.addPatterns(
				"spawner[s] (from|of) %blocks/entities%",
				"trial spawner[s] (from|of) %blocks%"
			)
			.build());
	}

	private Expression<?> spawnerObjectExpr;
	private boolean trial;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		spawnerObjectExpr = exprs[0];
		trial = matchedPattern == 1;
		return true;
	}

	@Override
	protected Object @Nullable [] get(Event event) {
		Object[] spawnerObjects = this.spawnerObjectExpr.getArray(event);
		if (spawnerObjects == null)
			return null;

		List<Object> spawners = new ArrayList<>();
		for (Object spawner : spawnerObjects) {
			if (trial)
				spawner = SpawnerUtils.getAsSkriptTrialSpawner(spawner);
			else
				spawner = SpawnerUtils.getAsSkriptSpawner(spawner);

			if (spawner == null)
				continue;
			spawners.add(spawner);
		}

		if (spawners.isEmpty())
			return null;

		if (trial)
			//noinspection SuspiciousToArrayCall
			return spawners.toArray(SkriptTrialSpawner[]::new);
		//noinspection SuspiciousToArrayCall
		return spawners.toArray(SkriptMobSpawner[]::new);
	}

	@Override
	public boolean isSingle() {
		return spawnerObjectExpr.isSingle();
	}

	@Override
	public Class<?> getReturnType() {
		if (trial)
			return SkriptTrialSpawner.class;
		return SkriptMobSpawner.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		SyntaxStringBuilder ssb = new SyntaxStringBuilder(event, debug);
		if (trial)
			ssb.append("trial");
		ssb.append("spawner from", spawnerObjectExpr);
		return ssb.toString();
	}

}

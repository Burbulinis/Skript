package org.skriptlang.skript.bukkit.spawner.elements.expressions.basespawner;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.event.Event;
import org.bukkit.spawner.BaseSpawner;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawner.SpawnerModule;
import org.skriptlang.skript.bukkit.spawner.util.SpawnerUtils;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxOrigin;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.ArrayList;
import java.util.List;

public class ExprPotentialSpawns extends PropertyExpression<Object, SpawnerEntry> {

	static {
		var info = SyntaxInfo.Expression.builder(ExprPotentialSpawns.class, SpawnerEntry.class)
			.origin(SyntaxOrigin.of(Skript.instance()))
			.supplier(ExprPotentialSpawns::new)
			.priority(PropertyExpression.DEFAULT_PRIORITY)
			.addPatterns(
				"[the] potential spawns (of|from) %entities/blocks/trialspawnerconfigs%",
				"%entities/blocks/trialspawnerconfigs%'[s] potential spawns")
			.build();

		SpawnerModule.SYNTAX_REGISTRY.register(SyntaxRegistry.EXPRESSION, info);
	}

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		setExpr(exprs[0]);
		return true;
	}

	@Override
	protected SpawnerEntry[] get(Event event, Object[] source) {
		List<SpawnerEntry> entries = new ArrayList<>();
		for (Object object : source) {
			if (!SpawnerUtils.isBaseSpawner(object))
				continue;

			BaseSpawner spawner = SpawnerUtils.getAsBaseSpawner(object);
			entries.addAll(spawner.getPotentialSpawns());
		}
		return entries.toArray(new SpawnerEntry[0]);
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, ADD, REMOVE, RESET, DELETE -> CollectionUtils.array(SpawnerEntry[].class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		List<SpawnerEntry> entries = new ArrayList<>();
		if (delta != null) {
			for (Object object : delta) {
				entries.add((SpawnerEntry) object);
			}
		}

		for (Object object : getExpr().getArray(event)) {
			if (!SpawnerUtils.isBaseSpawner(object))
				continue;

			BaseSpawner spawner = SpawnerUtils.getAsBaseSpawner(object);

			switch (mode) {
				case SET -> spawner.setPotentialSpawns(entries);
				case ADD -> {
					for (SpawnerEntry entry : entries) {
						spawner.addPotentialSpawn(entry);
					}
				}
				case REMOVE -> {
					List<SpawnerEntry> potentialSpawns = spawner.getPotentialSpawns();
					potentialSpawns.removeAll(entries);
					spawner.setPotentialSpawns(potentialSpawns);
				}
				case RESET, DELETE -> spawner.setPotentialSpawns(new ArrayList<>());
			}

			SpawnerUtils.updateState(spawner);
		}
	}

	@Override
	public Class<? extends SpawnerEntry> getReturnType() {
		return SpawnerEntry.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "the potential spawns of " + getExpr().toString(event, debug);
	}

}
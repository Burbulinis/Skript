package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.EventRestrictedSyntax;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.events.MobSpawnerDataEvent;
import org.skriptlang.skript.bukkit.spawners.util.events.SpawnerDataEvent;
import org.skriptlang.skript.bukkit.spawners.util.events.TrialSpawnerDataEvent;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptMobSpawnerData;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptSpawnerData;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptTrialSpawnerData;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

public class ExprEventSpawnerData extends SimpleExpression<SkriptSpawnerData> implements EventRestrictedSyntax {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprEventSpawnerData.class, SkriptSpawnerData.class)
			.supplier(ExprEventSpawnerData::new)
			.priority(SyntaxInfo.SIMPLE)
			.addPattern("[the] [1:trial|2:mob] spawner data")
			.build()
		);
	}

	private enum SpawnerType {
		ANY, TRIAL, MOB
	}

	private SpawnerType type;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		type = SpawnerType.values()[parseResult.mark];

		if (type == SpawnerType.TRIAL && !getParser().isCurrentEvent(TrialSpawnerDataEvent.class)) {
			Skript.error("'trial spawner data' can only be used in the trial spawner data events.");
			return false;
		} else if (type == SpawnerType.MOB && !getParser().isCurrentEvent(MobSpawnerDataEvent.class)) {
			Skript.error("'mob spawner data' can only be used in the mob spawner data events.");
			return false;
		}

		return true;
	}

	@Override
	public Class<? extends Event>[] supportedEvents() {
		return CollectionUtils.array(SpawnerDataEvent.class);
	}

	@Override
	protected SkriptSpawnerData @Nullable [] get(Event event) {
		if (!(event instanceof SpawnerDataEvent dataEvent))
			return null;

		return new SkriptSpawnerData[]{dataEvent.getSpawnerData()};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends SkriptSpawnerData> getReturnType() {
		return switch (type) {
			case TRIAL -> SkriptTrialSpawnerData.class;
			case MOB -> SkriptMobSpawnerData.class;
			default -> SkriptSpawnerData.class;
		};
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return (type == SpawnerType.TRIAL ? "trial " : "") + "spawner data";
	}

}

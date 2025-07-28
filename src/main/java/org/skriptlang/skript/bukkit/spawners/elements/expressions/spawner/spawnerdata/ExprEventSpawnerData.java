package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner.spawnerdata;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.lang.EventRestrictedSyntax;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.bukkit.spawners.util.events.MobSpawnerDataEvent;
import org.skriptlang.skript.bukkit.spawners.util.events.SpawnerDataEvent;
import org.skriptlang.skript.bukkit.spawners.util.events.TrialSpawnerDataEvent;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptMobSpawnerData;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptSpawnerData;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptTrialSpawnerData;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.StringJoiner;

@RequiredPlugins("Minecraft 1.21+ (for trial spawner data)")
public class ExprEventSpawnerData extends SimpleExpression<SkriptSpawnerData> implements EventRestrictedSyntax {

	public static void register(SyntaxRegistry registry) {
		String pattern = "[the] [1:mob] spawner data";
		if (SpawnerUtils.IS_RUNNING_1_21)
			pattern = "[the] [1:mob|2:trial] spawner data";

		registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprEventSpawnerData.class, SkriptSpawnerData.class)
			.supplier(ExprEventSpawnerData::new)
			.priority(SyntaxInfo.SIMPLE)
			.addPattern(pattern)
			.build()
		);
	}

	private enum SpawnerType {
		ANY, MOB, TRIAL
	}

	private SpawnerType type;
	private Class<? extends Event>[] events;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		type = SpawnerType.values()[parseResult.mark];
		events = getParser().getCurrentEvents();

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
		if (CollectionUtils.isAnyInstanceOf(events, MobSpawnerDataEvent.class)) {
			return SkriptMobSpawnerData.class;
		} else if (CollectionUtils.isAnyInstanceOf(events, TrialSpawnerDataEvent.class)) {
			return SkriptTrialSpawnerData.class;
		}
		return SkriptSpawnerData.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		StringJoiner joiner = new StringJoiner(" ", "the", "spawner data");
		if (type == SpawnerType.TRIAL) {
			joiner.add("trial");
		} else if (type == SpawnerType.MOB) {
			joiner.add("mob");
		}
		return joiner.toString();
	}

}

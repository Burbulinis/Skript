package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.util.SectionUtils;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.events.MobSpawnerDataEvent;
import org.skriptlang.skript.bukkit.spawners.util.events.TrialSpawnerDataEvent;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptMobSpawnerData;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptSpawnerData;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptTrialSpawnerData;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.List;
import java.util.Locale;

public class ExprSecSpawnerData extends SectionExpression<SkriptSpawnerData> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprSecSpawnerData.class, SkriptSpawnerData.class)
			.supplier(ExprSecSpawnerData::new)
			.priority(SyntaxInfo.SIMPLE)
			.addPatterns(
				"[the] mob spawner data",
				"[the] [:ominous] trial spawner data")
			.build()
		);
	}

	private enum DataType {
		MOB, TRIAL
	}

	private DataType type;
	private boolean ominous;
	private Trigger trigger;

	@Override
	public boolean init(
		Expression<?>[] exprs, int pattern, Kleenean delayed, ParseResult result,
		@Nullable SectionNode node, @Nullable List<TriggerItem> triggerItems
	) {
		type = DataType.values()[pattern];
		ominous = result.hasTag("ominous");
		if (node != null) {
			String name = type.name().toLowerCase(Locale.ENGLISH) + " spawner data";
			trigger = SectionUtils.loadLinkedCode(name, (beforeLoading, afterLoading) ->
				loadCode(node, name, beforeLoading, afterLoading, MobSpawnerDataEvent.class));
			return trigger != null;
		}
		return true;
	}

	@Override
	protected SkriptSpawnerData @Nullable [] get(Event event) {
		SkriptSpawnerData data = (type == DataType.MOB)
			? new SkriptMobSpawnerData()
			: new SkriptTrialSpawnerData(ominous);

		if (trigger != null) {
			Event dataEvent = (type == DataType.MOB)
				? new MobSpawnerDataEvent((SkriptMobSpawnerData) data)
				: new TrialSpawnerDataEvent((SkriptTrialSpawnerData) data);

			Variables.withLocalVariables(event, dataEvent, () ->
				TriggerItem.walk(trigger, dataEvent)
			);
		}
		return new SkriptSpawnerData[]{data};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends SkriptSpawnerData> getReturnType() {
		if (type == DataType.MOB)
			return SkriptMobSpawnerData.class;
		return SkriptTrialSpawnerData.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);
		if (ominous)
			builder.append("ominous");
		builder.append(type.name().toLowerCase(Locale.ENGLISH), "spawner data");
		return builder.toString();
	}

}

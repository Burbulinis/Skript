package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner.spawnerdata;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.util.SectionUtils;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.bukkit.spawners.util.events.MobSpawnerDataEvent;
import org.skriptlang.skript.bukkit.spawners.util.events.TrialSpawnerDataEvent;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptMobSpawnerData;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptSpawnerData;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptTrialSpawnerData;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.List;
import java.util.Locale;

@RequiredPlugins("Minecraft 1.21+ (for trial spawner data)")
public class ExprSecSpawnerData extends SectionExpression<SkriptSpawnerData> {

	public static void register(SyntaxRegistry registry) {
		var info = SyntaxInfo.Expression.builder(ExprSecSpawnerData.class, SkriptSpawnerData.class)
			.supplier(ExprSecSpawnerData::new)
			.priority(SyntaxInfo.SIMPLE)
			.addPattern("[the] mob spawner data");

		if (SpawnerUtils.IS_RUNNING_1_21)
			info.addPattern("[the] trial spawner data");

		registry.register(SyntaxRegistry.EXPRESSION, info.build());
	}

	private enum DataType {
		MOB, TRIAL
	}

	private DataType type;
	private Trigger trigger;

	@Override
	public boolean init(
		Expression<?>[] exprs, int pattern, Kleenean delayed, ParseResult result,
		@Nullable SectionNode node, @Nullable List<TriggerItem> triggerItems
	) {
		type = DataType.values()[pattern];
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
			: new SkriptTrialSpawnerData();

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
		return type.name().toLowerCase(Locale.ENGLISH) + " spawner data";
	}

}

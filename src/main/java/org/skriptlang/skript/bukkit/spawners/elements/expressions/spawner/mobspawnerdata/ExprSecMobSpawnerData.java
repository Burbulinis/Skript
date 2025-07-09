package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner.mobspawnerdata;

import ch.njol.skript.config.SectionNode;
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
import org.skriptlang.skript.bukkit.spawners.util.events.MobSpawnerDataEvent;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptMobSpawnerData;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.List;

public class ExprSecMobSpawnerData extends SectionExpression<SkriptMobSpawnerData> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprSecMobSpawnerData.class, SkriptMobSpawnerData.class)
			.supplier(ExprSecMobSpawnerData::new)
			.priority(SyntaxInfo.SIMPLE)
			.addPattern("[the] mob spawner data")
			.build()
		);
	}

	private Trigger trigger;

	@Override
	public boolean init(
		Expression<?>[] exprs, int pattern, Kleenean delayed, ParseResult result,
		@Nullable SectionNode node, @Nullable List<TriggerItem> triggerItems
	) {
		if (node != null) {
			trigger = SectionUtils.loadLinkedCode("mob spawner data", (beforeLoading, afterLoading) ->
				loadCode(node, "mob spawner data", beforeLoading, afterLoading, MobSpawnerDataEvent.class));
			return trigger != null;
		}
		return true;
	}

	@Override
	protected SkriptMobSpawnerData @Nullable [] get(Event event) {
		SkriptMobSpawnerData data = new SkriptMobSpawnerData();
		if (trigger != null) {
			MobSpawnerDataEvent dataEvent = new MobSpawnerDataEvent(data);
			Variables.withLocalVariables(event, dataEvent, () ->
				TriggerItem.walk(trigger, dataEvent)
			);
		}
		return new SkriptMobSpawnerData[]{data};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends SkriptMobSpawnerData> getReturnType() {
		return SkriptMobSpawnerData.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "mob spawner data";
	}

}

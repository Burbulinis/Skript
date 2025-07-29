package org.skriptlang.skript.bukkit.spawners.elements.sections;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SectionUtils;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
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

import java.util.List;

public class SecModifySpawnerData extends Section {

	public static void register(SyntaxRegistry registry) {
		var info = SyntaxInfo.builder(SecModifySpawnerData.class)
			.supplier(SecModifySpawnerData::new)
			.priority(SyntaxInfo.COMBINED)
			.addPattern("modify [the] mob spawner data of %" + SpawnerUtils.spawnerPropertyType + '%');

		if (SpawnerUtils.IS_RUNNING_1_21)
			info.addPattern("modify [the] [:ominous] trial spawner data of %blocks%");

		registry.register(SyntaxRegistry.SECTION, info.build());
	}

	private Expression<?> spawners;
	private boolean mob;
	private boolean ominous;

	private Trigger trigger;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
		spawners = exprs[0];
		mob = matchedPattern == 0;
		ominous = parseResult.hasTag("ominous");

		trigger = SectionUtils.loadLinkedCode("modify spawner data", (beforeLoading, afterLoading)
			-> loadCode(sectionNode, "modify spawner data", beforeLoading, afterLoading, SpawnerDataEvent.class));
		return trigger != null;
	}

	@Override
	protected @Nullable TriggerItem walk(Event event) {
		for (Object object : spawners.getArray(event)) {
			SkriptSpawnerData data = null;

			if (mob) {
				if (SpawnerUtils.isCreatureSpawner(object)) {
					data = SkriptMobSpawnerData.fromSpawner(SpawnerUtils.getCreatureSpawner(object));
				} else if (SpawnerUtils.isSpawnerMinecart(object)) {
					data = SkriptMobSpawnerData.fromSpawner(SpawnerUtils.getSpawnerMinecart(object));
				}
			} else if (SpawnerUtils.isTrialSpawner(object)) {
				data = SkriptTrialSpawnerData.fromTrialSpawner(SpawnerUtils.getTrialSpawner(object), ominous);
			}

			if (data == null)
				continue;

			SpawnerDataEvent dataEvent = mob
				? new MobSpawnerDataEvent((SkriptMobSpawnerData) data)
				: new TrialSpawnerDataEvent((SkriptTrialSpawnerData) data);

			Variables.withLocalVariables(event, dataEvent, () ->
				TriggerItem.walk(trigger, dataEvent)
			);

			if (mob) {
				SkriptMobSpawnerData mobData = ((SkriptMobSpawnerData) data);
				if (SpawnerUtils.isCreatureSpawner(object)) {
					mobData.applyDataToSpawner(SpawnerUtils.getCreatureSpawner(object));
				} else if (SpawnerUtils.isSpawnerMinecart(object)) {
					mobData.applyDataToSpawner(SpawnerUtils.getSpawnerMinecart(object));
				}
			} else if (SpawnerUtils.isTrialSpawner(object)) {
				SkriptTrialSpawnerData trialData = ((SkriptTrialSpawnerData) data);
				trialData.applyDataToTrialSpawner(SpawnerUtils.getTrialSpawner(object), ominous);
			}
		}

		return super.walk(event, false);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);

		builder.append("modify the");
		if (mob) {
			builder.append("mob");
		} else {
			if (ominous)
				builder.append("ominous");
			builder.append("trial");
		}
		builder.append("spawner data of", spawners);

		return builder.toString();
	}

}

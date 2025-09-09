package org.skriptlang.skript.bukkit.spawners.elements.sections;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SectionUtils;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import org.bukkit.block.TrialSpawner;
import org.bukkit.event.Event;
import org.bukkit.spawner.Spawner;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerDataType;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.bukkit.spawners.util.events.MobSpawnerDataEvent;
import org.skriptlang.skript.bukkit.spawners.util.events.SpawnerDataEvent;
import org.skriptlang.skript.bukkit.spawners.util.events.TrialSpawnerDataEvent;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptMobSpawnerData;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptTrialSpawnerData;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.List;

public class SecModifySpawnerData extends Section {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.SECTION, SyntaxInfo.builder(SecModifySpawnerData.class)
			.supplier(SecModifySpawnerData::new)
			.priority(SyntaxInfo.COMBINED)
			.addPatterns(
				"modify [the] [:mob] spawner data of %" + SpawnerUtils.SPAWNER_PROPERTY_TYPE + '%',
				"modify [the] [:ominous|:regular|:ominous and regular] trial:trial spawner data of %blocks%")
			.build());
	}

	private enum TrialSpawnerState {
		OMINOUS, REGULAR, BOTH;

		public static TrialSpawnerState fromTags(List<String> tags) {
			if (tags.contains("ominous")) {
				return OMINOUS;
			} else if (tags.contains("ominous and regular")) {
				return BOTH;
			} else {
				return REGULAR;
			}
		}
	}

	private Expression<?> spawners;
	private SpawnerDataType dataType;
	private TrialSpawnerState state;

	private Trigger trigger;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
		spawners = exprs[0];
		dataType = SpawnerDataType.fromTags(parseResult.tags);
		state = TrialSpawnerState.fromTags(parseResult.tags);

		trigger = SectionUtils.loadLinkedCode("modify spawner data", (beforeLoading, afterLoading)
			-> loadCode(sectionNode, "modify spawner data", beforeLoading, afterLoading, SpawnerDataEvent.class));
		return trigger != null;
	}

	@Override
	protected @Nullable TriggerItem walk(Event event) {
		for (Object spawnerObject : spawners.getArray(event)) {
			if (!dataType.matches(spawnerObject))
				continue;

			if (SpawnerUtils.isMobSpawner(spawnerObject)) {
				Spawner mobSpawner = SpawnerUtils.getMobSpawner(spawnerObject);
				SkriptMobSpawnerData mobData = SkriptMobSpawnerData.fromSpawner(mobSpawner);

				MobSpawnerDataEvent mobEvent = new MobSpawnerDataEvent(mobData);
				Variables.withLocalVariables(event, mobEvent, () -> TriggerItem.walk(trigger, mobEvent));

				mobData.applyData(mobSpawner);
			} else {
				TrialSpawner trialSpawner = SpawnerUtils.getTrialSpawner(spawnerObject);
				SkriptTrialSpawnerData trialData = SkriptTrialSpawnerData.fromTrialSpawner(trialSpawner,
					state == TrialSpawnerState.OMINOUS);

				TrialSpawnerDataEvent regularEvent = new TrialSpawnerDataEvent(trialData);
				Variables.withLocalVariables(event, regularEvent, () -> TriggerItem.walk(trigger, regularEvent));

				if (state == TrialSpawnerState.BOTH) {
					// guaranteed to be the regular data here
					trialData.applyData(trialSpawner, false);

					// modify the ominous data
					trialData = SkriptTrialSpawnerData.fromTrialSpawner(trialSpawner, true);
					TrialSpawnerDataEvent ominousEvent = new TrialSpawnerDataEvent(trialData);
					Variables.withLocalVariables(event, ominousEvent, () -> TriggerItem.walk(trigger, ominousEvent));
					trialData.applyData(trialSpawner, true);
				} else {
					trialData.applyData(trialSpawner, state == TrialSpawnerState.OMINOUS);
				}
			}
		}

		return super.walk(event, false);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);

		builder.append("modify the");
		if (dataType.isTrial()) {
			if (state == TrialSpawnerState.REGULAR) {
				builder.append("regular");
			} else if (state == TrialSpawnerState.OMINOUS) {
				builder.append("ominous");
			} else {
				builder.append("ominous and regular");
			}
		}
		builder.append(dataType.toString(), "spawner data of", spawners);

		return builder.toString();
	}

}

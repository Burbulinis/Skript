package org.skriptlang.skript.bukkit.spawner.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import org.bukkit.event.Event;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.entity.TrialSpawnerSpawnEvent;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.registration.BukkitRegistryKeys;
import org.skriptlang.skript.bukkit.registration.BukkitSyntaxInfos;
import org.skriptlang.skript.bukkit.spawner.SpawnerModule;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxOrigin;

public class EvtSpawnerSpawn extends SkriptEvent {

	static {
		//noinspection unchecked
		var info = BukkitSyntaxInfos.Event.builder(EvtSpawnerSpawn.class, "Spawner Spawn")
			.origin(SyntaxOrigin.of(Skript.instance()))
			.supplier(EvtSpawnerSpawn::new)
			.priority(SyntaxInfo.COMBINED)
			.addEvents(SpawnerSpawnEvent.class, TrialSpawnerSpawnEvent.class)
			.addPattern("[:trial] spawner spawn[ing] [of %-entitydatas%]")
			.addDescription("Called when a spawner spawns an entity.")
			.addExamples(
				"on spawner spawn of pig:",
					"\tbroadcast \"A little piggy spawned!\"",
				"",
				"on trial spawner spawn of pig:",
					"\tbroadcast \"A little piggy spawned from a trial spawner!\"")
			.since("INSERT VERSION")
			.addRequiredPlugin("MC 1.21+")
			.build();

		SpawnerModule.SYNTAX_REGISTRY.register(BukkitRegistryKeys.EVENT, info);
	}

	private Literal<EntityData<?>> entityDatas;
	private boolean trial;

	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
		//noinspection unchecked
		this.entityDatas =  (Literal<EntityData<?>>) args[0];
		trial = parseResult.hasTag("trial");
		return true;
	}

	@Override
	public boolean check(Event event) {
		boolean pass = true;
		if (entityDatas != null) {
			EntityData<?> type = null;
			if (event instanceof SpawnerSpawnEvent spawnerEvent) {
				type = EntityUtils.toSkriptEntityData(spawnerEvent.getEntityType());
			} else if (event instanceof TrialSpawnerSpawnEvent trialEvent) {
				type = EntityUtils.toSkriptEntityData(trialEvent.getEntityType());
			}

			assert type != null;

			for (EntityData<?> entityType : entityDatas.getArray()) {
				if (type.equals(entityType)) {
					pass = true;
					break;
				}
				pass = false;
			}
		}

		if (trial && pass) {
			return event instanceof TrialSpawnerSpawnEvent;
		} else if (!trial && pass) {
			return event instanceof SpawnerSpawnEvent;
		}

		return false;
	}


	@Override
	public String toString(@Nullable Event event, boolean debug) {
		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);

		if (trial)
			builder.append("trial");
		builder.append("spawner spawn");
		if (entityDatas != null)
			builder.append("of", entityDatas);

		return builder.toString();
	}

}

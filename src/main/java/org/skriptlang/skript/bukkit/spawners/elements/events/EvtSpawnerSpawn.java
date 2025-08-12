package org.skriptlang.skript.bukkit.spawners.elements.events;

import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.coll.CollectionUtils;
import com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.entity.TrialSpawnerSpawnEvent;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.registration.BukkitRegistryKeys;
import org.skriptlang.skript.bukkit.registration.BukkitSyntaxInfos;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@SuppressWarnings("UnstableApiUsage")
public class EvtSpawnerSpawn extends SkriptEvent {

	public static void register(SyntaxRegistry registry) {
		Class<? extends Event>[] events = CollectionUtils.array(SpawnerSpawnEvent.class);
		String pattern = "mob:mob spawner spawn[ing] [of %-entitydatas%]";

		if (SpawnerUtils.IS_RUNNING_1_21) {
			events = CollectionUtils.array(SpawnerSpawnEvent.class, TrialSpawnerSpawnEvent.class);
			pattern = "[:trial|:mob] spawner spawn[ing] [of %-entitydatas%]";
		}

		registry.register(BukkitRegistryKeys.EVENT, BukkitSyntaxInfos.Event.builder(EvtSpawnerSpawn.class, "Spawner Spawn")
			.priority(SyntaxInfo.COMBINED)
			.supplier(EvtSpawnerSpawn::new)
			.addEvents(events)
			.addPattern(pattern)
			.addDescription("Called when a spawner spawns an entity or is about to.")
			.addExamples("todo")
			.addSince("INSERT VERSION")
			.addRequiredPlugin("Minecraft 1.21+ (for trial spawners)")
			.build()
		);
	}

	private boolean trial;
	private boolean mob;
	private Literal<EntityData<?>> entityDatas;

	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
		trial = parseResult.hasTag("trial");
		mob	= parseResult.hasTag("mob");
		System.out.println(mob);
		//noinspection unchecked
		entityDatas = (Literal<EntityData<?>>) args[0];
		return true;
	}


	@Override
	public boolean check(Event event) {
		if (entityDatas != null) {
			EntityType currentType = null;

			if (event instanceof EntityEvent entityEvent) {
				currentType = entityEvent.getEntityType();
			} else if (event instanceof PreSpawnerSpawnEvent preEvent) {
				currentType = preEvent.getType();
			}

			boolean match = false;
			for (EntityData<?> entityData : entityDatas.getArray()) {
				if (entityData.isSupertypeOf(EntityUtils.toSkriptEntityData(currentType))) {
					match = true;
					break;
				}
			}

			if (!match)
				return false;
		}

		if (trial) {
			return event instanceof TrialSpawnerSpawnEvent;
		} else if (mob) {
			return event instanceof SpawnerSpawnEvent;
		}

		return true;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);

		if (trial) {
			builder.append("trial");
		} else if (mob) {
			builder.append("mob");
		}

		builder.append("spawner spawn");
		if (entityDatas != null)
			builder.append("of", entityDatas);

		return builder.toString();
	}

}

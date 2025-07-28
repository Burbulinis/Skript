package org.skriptlang.skript.bukkit.spawners.elements.events;

import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.coll.CollectionUtils;
import com.destroystokyo.paper.event.entity.PreSpawnerSpawnEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
		Class<? extends Event>[] events = CollectionUtils.array(SpawnerSpawnEvent.class, PreSpawnerSpawnEvent.class);
		String pattern = "[:pre] spawner spawn[ing] [of %-entitydatas%]";

		if (SpawnerUtils.IS_RUNNING_1_21) {
			events = CollectionUtils.array(SpawnerSpawnEvent.class, PreSpawnerSpawnEvent.class, TrialSpawnerSpawnEvent.class);
			pattern = "[:pre] [:trial] spawner spawn[ing] [of %-entitydatas%]";
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

	private boolean pre;
	private boolean trial;
	private Literal<EntityData<?>> entityDatas;

	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
		pre = parseResult.hasTag("pre");
		trial = parseResult.hasTag("trial");
		//noinspection unchecked
		entityDatas = (Literal<EntityData<?>>) args[0];
		return true;
	}


	@Override
	public boolean check(Event event) {
		if (pre && event instanceof PreSpawnerSpawnEvent preEvent) {
			Block block = preEvent.getSpawnerLocation().getBlock();
			if (trial && block.getType() != Material.TRIAL_SPAWNER)
				return false;
		}

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

		if (pre) {
			return event instanceof PreSpawnerSpawnEvent;
		} else if (trial) {
			return event instanceof TrialSpawnerSpawnEvent;
		} else {
			return event instanceof SpawnerSpawnEvent;
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);

		if (pre)
			builder.append("pre");
		if (trial)
			builder.append("trial");
		builder.append("spawner spawn");
		if (entityDatas != null)
			builder.append("of", entityDatas);

		return builder.toString();
	}

}

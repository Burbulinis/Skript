package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.doc.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.TrialSpawner;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.skriptlang.skript.bukkit.spawners.util.SkriptSpawnerData;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Spawner Type")
@Description("Retrieves, sets, or resets the spawner's entity type")
@Example("""
	on right click:
		if event-block is spawner:
			send "Spawner's type is %target block's entity type%"
	""")
@Since("2.4, 2.9.2 (trial spawner), INSERT VERSION (spawner data)")
public class ExprSpawnerType extends SimplePropertyExpression<Object, EntityData> {

	public static void register(SyntaxRegistry registry) {
		String fromType = "blocks";
		if (SpawnerUtils.IS_RUNNING_1_21)
			fromType += "/spawnerdatas";

		registerDefault(registry, ExprSpawnerType.class, EntityData.class,
			"(spawner|entity|creature) type[s]", fromType
		);
	}

	private static final boolean HAS_TRIAL_SPAWNER = Skript.classExists("org.bukkit.block.TrialSpawner");

	@Override
	public @Nullable EntityData<?> convert(Object object) {
		if (object instanceof Block block)
			object = block.getState();

		if (object instanceof CreatureSpawner creatureSpawner) {
			if (creatureSpawner.getSpawnedType() == null)
				return null;
			return EntityUtils.toSkriptEntityData(creatureSpawner.getSpawnedType());
		} else if (HAS_TRIAL_SPAWNER && object instanceof TrialSpawner trialSpawner) {
			var config = SpawnerUtils.getTrialSpawnerConfiguration(
				trialSpawner,
				trialSpawner.isOminous()
			);
			if (config.getSpawnedType() == null)
				return null;
			return EntityUtils.toSkriptEntityData(config.getSpawnedType());
		} else if (object instanceof SkriptSpawnerData skriptSpawnerData) {
			return skriptSpawnerData.getSpawnerType();
		}

		return null;
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, DELETE, RESET -> CollectionUtils.array(EntityData.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		//noinspection ConstantConditions
		EntityData<?> entityData = (EntityData<?>) delta[0];

		for (Object spawnerObject : getExpr().getArray(event)) {
			if (spawnerObject instanceof Block block)
				spawnerObject = block.getState();

			if (spawnerObject instanceof SkriptSpawnerData skriptSpawnerData) {
				switch (mode) {
					case SET -> skriptSpawnerData.setSpawnerType(entityData);
					case DELETE, RESET -> skriptSpawnerData.setSpawnerType(null);
				}
			} else if (spawnerObject instanceof CreatureSpawner creatureSpawner) {
				switch (mode) {
					case SET -> creatureSpawner.setSpawnedType(EntityUtils.toBukkitEntityType(entityData));
					case DELETE, RESET -> creatureSpawner.setSpawnedType(null);
				}
				creatureSpawner.update(true, false);
			} else if (HAS_TRIAL_SPAWNER && spawnerObject instanceof TrialSpawner trialSpawner) {
				var config = SpawnerUtils.getTrialSpawnerConfiguration(
					trialSpawner,
					trialSpawner.isOminous()
				);
				switch (mode) {
					case SET -> config.setSpawnedType(EntityUtils.toBukkitEntityType(entityData));
					case DELETE, RESET -> config.setSpawnedType(null);
				}
				trialSpawner.update(true, false);
			}
		}
	}

	@Override
	public Class<EntityData> getReturnType() {
		return EntityData.class;
	}

	@Override
	protected String getPropertyName() {
		return "spawner type";
	}

}

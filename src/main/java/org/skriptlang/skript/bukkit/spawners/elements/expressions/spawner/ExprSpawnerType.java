package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner;

import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.TrialSpawner;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Spawner Type")
@Description("Retrieves, sets, or resets the spawner's entity type")
@Example("""
	on right click:
		if event-block is spawner:
			send "Spawner's type is %target block's entity type%"
	""")
@Since("2.4, 2.9.2 (trial spawner), INSERT VERSION (spawner minecart)")
public class ExprSpawnerType extends SimplePropertyExpression<Object, EntityData> {

	public static void register(SyntaxRegistry registry) {
		registerDefault(registry, ExprSpawnerType.class, EntityData.class,
			"(spawner|entity|creature) type[s]", SpawnerUtils.spawnerPropertyType
		);
	}

	@Override
	public @Nullable EntityData<?> convert(Object object) {
		if (SpawnerUtils.isCreatureSpawner(object)) {
			CreatureSpawner spawner = SpawnerUtils.getCreatureSpawner(object);
			if (spawner.getSpawnedType() == null)
				return null;

			return EntityUtils.toSkriptEntityData(spawner.getSpawnedType());
		} else if (SpawnerUtils.isTrialSpawner(object)) {
			var config = SpawnerUtils.getTrialSpawnerConfiguration(SpawnerUtils.getTrialSpawner(object));
			if (config.getSpawnedType() == null)
				return null;

			return EntityUtils.toSkriptEntityData(config.getSpawnedType());
		} else if (SpawnerUtils.isSpawnerMinecart(object)) {
			SpawnerMinecart spawner = SpawnerUtils.getSpawnerMinecart(object);
			if (spawner.getSpawnedType() == null)
				return null;

			return EntityUtils.toSkriptEntityData(spawner.getSpawnedType());
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

		for (Object object : getExpr().getArray(event)) {
			if (SpawnerUtils.isCreatureSpawner(object)) {
				CreatureSpawner creatureSpawner = SpawnerUtils.getCreatureSpawner(object);
				switch (mode) {
					case SET -> creatureSpawner.setSpawnedType(EntityUtils.toBukkitEntityType(entityData));
					case DELETE, RESET -> creatureSpawner.setSpawnedType(null);
				}
				creatureSpawner.update(true, false);
			} else if (SpawnerUtils.isTrialSpawner(object)) {
				TrialSpawner trialSpawner = SpawnerUtils.getTrialSpawner(object);
				var config = SpawnerUtils.getTrialSpawnerConfiguration(trialSpawner);
				switch (mode) {
					case SET -> config.setSpawnedType(EntityUtils.toBukkitEntityType(entityData));
					case DELETE, RESET -> config.setSpawnedType(null);
				}
				trialSpawner.update(true, false);
			} else if (SpawnerUtils.isSpawnerMinecart(object)) {
				SpawnerMinecart spawnerMinecart = SpawnerUtils.getSpawnerMinecart(object);
				switch (mode) {
					case SET -> spawnerMinecart.setSpawnedType(EntityUtils.toBukkitEntityType(entityData));
					case DELETE, RESET -> spawnerMinecart.setSpawnedType(null);
				}
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

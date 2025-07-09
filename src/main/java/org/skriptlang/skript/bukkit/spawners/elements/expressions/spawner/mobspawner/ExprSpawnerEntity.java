package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner.mobspawner;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.TrialSpawner;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Base Spawner - Spawner Entity")
@Description({
	"Get the spawner entity of a base spawner.",
	"This is the entity that the spawner will spawn and displays the small entity inside the spawner.",
	"Setting this will override any previous entries that have been added to potential spawns of the spawner",
	"You can set the spawner entity to an item, though that is paper-exclusive and only for spawners, not base spawners. "
		+ "Spawners are spawner minecarts and creature spawners.",
	"",
	"This expression gets the trial spawner configuration "
		+ "with the current state (i.e. ominous, normal) of the trial spawner block, if one is provided.",
	"",
	"Base spawners are trial spawner configurations, spawner minecarts and creature spawners."
})
@Examples({
	"set {_entry} to a spawner entry with entity snapshot of a zombie:",
		"\tset weight to 5",
	"set spawner entity of event-block to {_entry}",
	"set spawner entity of event-block to entity snapshot of a zombie",
	"set spawner entity of event-block to event-entity",
	"set spawner entity of event-block to a pig",
	"set spawner entity of event-block to 16 golden apples # paper exclusive and only for spawners",
})
@Since("INSERT VERSION")
public class ExprSpawnerEntity extends SimplePropertyExpression<Object, EntitySnapshot> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(ExprSpawnerEntity.class, EntitySnapshot.class,
			"spawner entity snapshot", SpawnerUtils.spawnerPropertyType, true)
				.supplier(ExprSpawnerEntity::new)
				.build()
		);
	}

	@Override
	public @Nullable EntitySnapshot convert(Object object) {
		if (SpawnerUtils.isCreatureSpawner(object)) {
			CreatureSpawner creatureSpawner = SpawnerUtils.getCreatureSpawner(object);
			return creatureSpawner.getSpawnedEntity();
		} else if (SpawnerUtils.isTrialSpawner(object)) {
			TrialSpawner trialSpawner = SpawnerUtils.getTrialSpawner(object);
			var config = SpawnerUtils.getTrialSpawnerConfiguration(
				trialSpawner,
				trialSpawner.isOminous()
			);
			return config.getSpawnedEntity();
		} else if (SpawnerUtils.isSpawnerMinecart(object)) {
			SpawnerMinecart spawner = SpawnerUtils.getSpawnerMinecart(object);
			return spawner.getSpawnedEntity();
		}

		return null;
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, RESET, DELETE -> CollectionUtils.array(EntitySnapshot.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		EntitySnapshot entitySnapshot = delta != null ? (EntitySnapshot) delta[0] : null;

		for (Object object : getExpr().getArray(event)) {
			if (SpawnerUtils.isCreatureSpawner(object)) {
				CreatureSpawner creatureSpawner = SpawnerUtils.getCreatureSpawner(object);
				creatureSpawner.setSpawnedEntity(entitySnapshot);
				creatureSpawner.update(true, false);
			} else if (SpawnerUtils.isTrialSpawner(object)) {
				TrialSpawner trialSpawner = SpawnerUtils.getTrialSpawner(object);
				var config = SpawnerUtils.getTrialSpawnerConfiguration(trialSpawner);
				config.setSpawnedEntity(entitySnapshot);
				trialSpawner.update(true, false);
			} else if (SpawnerUtils.isSpawnerMinecart(object)) {
				SpawnerMinecart spawnerMinecart = SpawnerUtils.getSpawnerMinecart(object);
				spawnerMinecart.setSpawnedEntity(entitySnapshot);
			}
		}
	}

	@Override
	public Class<? extends EntitySnapshot> getReturnType() {
		return EntitySnapshot.class;
	}

	@Override
	protected String getPropertyName() {
		return "spawner entity snapshot";
	}

}

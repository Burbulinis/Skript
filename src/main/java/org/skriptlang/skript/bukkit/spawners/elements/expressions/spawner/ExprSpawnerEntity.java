package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner;

import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.*;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.TrialSpawner;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Spawner Entity")
@Description("""
    Returns the entity type or snapshot of a spawner. This represents the entity the spawner will spawn \
    and is displayed inside the spawner. If the spawner has multiple entries, \
    the type or snapshot corresponds to the entity that will spawn next.
    """)
@Example("""
	on right click:
		if event-block is spawner:
			send "Spawner's type is %target block's entity type%"
			send "Spawner's snapshot is %target block's entity snapshot%"
	""")
@Since("2.4, 2.9.2 (trial spawner), INSERT VERSION (spawner minecart)")
@RequiredPlugins("Minecraft 1.21+ (for trial spawners, spawner minecarts)")
public class ExprSpawnerEntity extends SimplePropertyExpression<Object, Object> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(ExprSpawnerEntity.class, Object.class,
			"spawner [entity] (type|:snapshot)[s]", SpawnerUtils.spawnerPropertyType, false)
				.supplier(ExprSpawnerEntity::new)
				.build()
		);
	}

	private boolean snapshot;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		snapshot = parseResult.hasTag("snapshot");
		return super.init(expressions, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public @Nullable Object convert(Object object) {
		Object entity = null;

		if (SpawnerUtils.isCreatureSpawner(object)) {
			CreatureSpawner spawner = SpawnerUtils.getCreatureSpawner(object);
			entity = snapshot ? spawner.getSpawnedEntity() : spawner.getSpawnedType();
		} else if (SpawnerUtils.isTrialSpawner(object)) {
			var spawner = SpawnerUtils.getTrialSpawnerConfiguration(SpawnerUtils.getTrialSpawner(object));
			entity = snapshot ? spawner.getSpawnedEntity() : spawner.getSpawnedType();
		} else if (SpawnerUtils.isSpawnerMinecart(object)) {
			SpawnerMinecart spawner = SpawnerUtils.getSpawnerMinecart(object);
			entity = snapshot ? spawner.getSpawnedEntity() : spawner.getSpawnedType();
		}

		if (!snapshot && entity != null)
			entity = EntityUtils.toSkriptEntityData((EntityType) entity);

		return entity;
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
		Object value = (delta != null) ? delta[0] : null;

		for (Object object : getExpr().getArray(event)) {
			if (SpawnerUtils.isCreatureSpawner(object)) {
				CreatureSpawner spawner = SpawnerUtils.getCreatureSpawner(object);
				if (snapshot) {
					spawner.setSpawnedEntity((EntitySnapshot) value);
				} else {
					spawner.setSpawnedType(EntityUtils.toBukkitEntityType((EntityData<?>) value));
				}
				spawner.update(true, false);
			} else if (SpawnerUtils.isTrialSpawner(object)) {
				TrialSpawner trial = SpawnerUtils.getTrialSpawner(object);
				var config = SpawnerUtils.getTrialSpawnerConfiguration(trial);
				if (snapshot) {
					config.setSpawnedEntity((EntitySnapshot) value);
				} else {
					config.setSpawnedType(EntityUtils.toBukkitEntityType((EntityData<?>) value));
				}
				trial.update(true, false);
			} else if (SpawnerUtils.isSpawnerMinecart(object)) {
				SpawnerMinecart minecart = SpawnerUtils.getSpawnerMinecart(object);
				if (snapshot) {
					minecart.setSpawnedEntity((EntitySnapshot) value);
				} else {
					minecart.setSpawnedType(EntityUtils.toBukkitEntityType((EntityData<?>) value));
				}
			}
		}
	}

	@Override
	public Class<?> getReturnType() {
		if (snapshot)
			return EntitySnapshot.class;
		return EntityData.class;
	}

	@Override
	protected String getPropertyName() {
		return "spawner type";
	}

}

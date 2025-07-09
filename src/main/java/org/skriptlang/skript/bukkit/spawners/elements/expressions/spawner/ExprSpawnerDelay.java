package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.TrialSpawner;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Base Spawner - Spawn Delay")
@Description({
	"The spawn delay of a base spawner is the time until the spawner will "
		+ "attempt to spawn its potential spawns.",
	"If the spawner is inactive during the spawn attempt, the delay will remain as 0 seconds, "
		+ "and the spawner will attempt to spawn the potential spawns every tick until it is successful.",
	"After such successful attempt, the delay will be reset to a random value between the minimum and maximum "
		+ "spawn delays of the spawner.",
	"Keep in mind that this is not the case for trial spawner configurations. Their spawn delay will remain as 2 seconds "
		+ "instead of the time until the next spawn attempt.",
	"",
	"This expression gets the trial spawner configuration "
		+ "with the current state (i.e. ominous, normal) of the trial spawner block, if one is provided.",
	"",
	"Base spawners are trial spawner configurations, spawner minecarts and creature spawners."
})
@Examples({
	"set the spawner delay of the target block to 5 seconds",
	"add 2 seconds to the spawner delay of the target block",
	"remove 1 second from the spawner delay of the target block",
	"reset the spawner delay of the target block"
})
@Since("INSERT VERSION")
public class ExprSpawnerDelay extends SimplePropertyExpression<Object, Timespan> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(
			ExprSpawnerDelay.class, Timespan.class,
			"spawn delay", "spawners", true
		).build());
		registerDefault(registry, ExprSpawnerDelay.class, Timespan.class,
			"spawn delay", SpawnerUtils.spawnerPropertyType
		);
	}

	@Override
	public @Nullable Timespan convert(Object object) {
		if (SpawnerUtils.isCreatureSpawner(object)) {
			CreatureSpawner creatureSpawner = SpawnerUtils.getCreatureSpawner(object);
			return new Timespan(TimePeriod.TICK, creatureSpawner.getDelay());
		} else if (SpawnerUtils.isTrialSpawner(object)) {
			var config = SpawnerUtils.getTrialSpawnerConfiguration(SpawnerUtils.getTrialSpawner(object));
			return new Timespan(TimePeriod.TICK, config.getDelay());
		} else if (SpawnerUtils.isSpawnerMinecart(object)) {
			SpawnerMinecart spawnerMinecart = SpawnerUtils.getSpawnerMinecart(object);
			return new Timespan(TimePeriod.TICK, spawnerMinecart.getDelay());
		}

		return null;
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, ADD, REMOVE, RESET -> CollectionUtils.array(Timespan.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		Timespan timespan = delta != null ? (Timespan) delta[0] : null;

		int ticks = 0;
		if (timespan != null)
			ticks = Math.clamp(timespan.getAs(TimePeriod.TICK), 0, Integer.MAX_VALUE);

		for (Object object : getExpr().getArray(event)) {
			if (SpawnerUtils.isCreatureSpawner(object)) {
				CreatureSpawner creatureSpawner = SpawnerUtils.getCreatureSpawner(object);
				switch (mode) {
					case SET -> creatureSpawner.setDelay(ticks);
					case ADD -> creatureSpawner.setDelay(Math.clamp(creatureSpawner.getDelay() + ticks, 0, Integer.MAX_VALUE));
					case REMOVE -> creatureSpawner.setDelay(Math.clamp(creatureSpawner.getDelay() - ticks, 0, Integer.MAX_VALUE));
					case RESET -> creatureSpawner.setDelay(-1);
				}
				creatureSpawner.update(true, false);
			} else if (SpawnerUtils.isTrialSpawner(object)) {
				TrialSpawner trialSpawner = SpawnerUtils.getTrialSpawner(object);
				var config = SpawnerUtils.getTrialSpawnerConfiguration(trialSpawner);
				switch (mode) {
					case SET -> config.setDelay(ticks);
					case ADD -> config.setDelay(Math.clamp(config.getDelay() + ticks, 0, Integer.MAX_VALUE));
					case REMOVE -> config.setDelay(Math.clamp(config.getDelay() - ticks, 0, Integer.MAX_VALUE));
					case RESET -> config.setDelay(
						Math.clamp(SpawnerUtils.DEFAULT_TRIAL_SPAWN_DELAY.getAs(TimePeriod.TICK), 0, Integer.MAX_VALUE)
					);
				}
				trialSpawner.update(true, false);
			} else if (SpawnerUtils.isSpawnerMinecart(object)) {
				SpawnerMinecart spawnerMinecart = SpawnerUtils.getSpawnerMinecart(object);
				switch (mode) {
					case SET -> spawnerMinecart.setDelay(ticks);
					case ADD -> spawnerMinecart.setDelay(Math.clamp(spawnerMinecart.getDelay() + ticks, 0, Integer.MAX_VALUE));
					case REMOVE -> spawnerMinecart.setDelay(Math.clamp(spawnerMinecart.getDelay() - ticks, 0, Integer.MAX_VALUE));
					case RESET -> spawnerMinecart.setDelay(-1);
				}
			}
		}
	}

	@Override
	public Class<? extends Timespan> getReturnType() {
		return Timespan.class;
	}

	@Override
	protected String getPropertyName() {
		return "spawn delay";
	}

}

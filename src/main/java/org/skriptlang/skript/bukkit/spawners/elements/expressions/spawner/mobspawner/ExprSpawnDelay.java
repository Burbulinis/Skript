package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner.mobspawner;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import ch.njol.util.Math2;
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
@RequiredPlugins("Minecraft 1.21.4+ (for trial spawners)")
public class ExprSpawnDelay extends SimplePropertyExpression<Object, Timespan> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(ExprSpawnDelay.class, Timespan.class,
			"spawn delay[s]", SpawnerUtils.spawnerPropertyType, false	)
				.supplier(ExprSpawnDelay::new)
				.build()
		);
	}

	@Override
	public @Nullable Timespan convert(Object object) {
		if (SpawnerUtils.isCreatureSpawner(object)) {
			CreatureSpawner creatureSpawner = SpawnerUtils.getCreatureSpawner(object);
			return new Timespan(TimePeriod.TICK, creatureSpawner.getDelay());
		} else if (SpawnerUtils.isTrialSpawner(object) && SpawnerUtils.IS_RUNNING_1_21_4) {
			TrialSpawner spawner = SpawnerUtils.getTrialSpawner(object);
			long ticks = Math2.fit(0, spawner.getNextSpawnAttempt() - spawner.getWorld().getGameTime(), Long.MAX_VALUE);
			return new Timespan(TimePeriod.TICK, ticks);
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
				creatureSpawner.setDelay(getNewDelay(mode, creatureSpawner.getDelay(), ticks));

				creatureSpawner.update(true, false);
			} else if (SpawnerUtils.isTrialSpawner(object) && SpawnerUtils.IS_RUNNING_1_21_4) {
				TrialSpawner trialSpawner = SpawnerUtils.getTrialSpawner(object);
				long gameTime = trialSpawner.getWorld().getGameTime();

				if (mode == ChangeMode.RESET) {
					long delay = SpawnerUtils.getTrialSpawnerConfiguration(trialSpawner).getDelay();
					trialSpawner.setNextSpawnAttempt(gameTime + delay);
				} else {
					long offset = mode == ChangeMode.REMOVE ? -ticks : ticks;
					trialSpawner.setNextSpawnAttempt(gameTime + offset);
				}

				trialSpawner.update(true, false);
			} else if (SpawnerUtils.isSpawnerMinecart(object)) {
				SpawnerMinecart spawnerMinecart = SpawnerUtils.getSpawnerMinecart(object);
				spawnerMinecart.setDelay(getNewDelay(mode, spawnerMinecart.getDelay(), ticks));
			}
		}
	}

	private int getNewDelay(ChangeMode mode, int current, int delta) {
		return switch (mode) {
			case SET -> delta;
			case ADD -> Math.clamp(current + delta, 0, Integer.MAX_VALUE);
			case REMOVE -> Math.clamp(current - delta, 0, Integer.MAX_VALUE);
			case RESET -> -1;
			default -> current;
		};
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

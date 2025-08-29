package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner;

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

@Name("Spawn Delay")
@Description("""
    Returns the spawn delay of the spawner. The spawn delay is the time before the spawner attempts to \
    spawn its entries. If the spawner is inactive at the time of the attempt, the delay remains 0 and \
    the spawner will try to spawn every tick until successful. After a successful spawn, the delay is \
    reset to a random value between the spawner’s minimum and maximum spawn delays.
    """)
@Example("""
	set the spawn delay of target block to 50 seconds
	add 10 seconds to the spawn delay of target block
	remove 5 seconds from the spawn delay of target block
	reset the spawn delay of target block
	""")
@Since("INSERT VERSION")
@RequiredPlugins({"Minecraft 1.21+ (spawner minecarts)", "Minecraft 1.21.4+ (for trial spawners)"})
public class ExprSpawnDelay extends SimplePropertyExpression<Object, Timespan> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(ExprSpawnDelay.class, Timespan.class,
			"spawn delay[s]", SpawnerUtils.spawnerPropertyType, false)
				.supplier(ExprSpawnDelay::new)
				.build()
		);
	}

	@Override
	public @Nullable Timespan convert(Object object) {
		Timespan timespan = null;

		if (SpawnerUtils.isCreatureSpawner(object)) {
			timespan = new Timespan(TimePeriod.TICK, SpawnerUtils.getCreatureSpawner(object).getDelay());
		} else if (SpawnerUtils.IS_RUNNING_1_21_4 && SpawnerUtils.isTrialSpawner(object)) {
			TrialSpawner spawner = SpawnerUtils.getTrialSpawner(object);
			long ticks = Math.max(0, spawner.getNextSpawnAttempt() - spawner.getWorld().getGameTime());
			timespan = new Timespan(TimePeriod.TICK, ticks);
		} else if (SpawnerUtils.isSpawnerMinecart(object)) {
			timespan = new Timespan(TimePeriod.TICK, SpawnerUtils.getSpawnerMinecart(object).getDelay());
		}

		return timespan;
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
			ticks = (int) Math.min(timespan.getAs(TimePeriod.TICK), Integer.MAX_VALUE);

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
			case ADD -> Math2.fit(0, current + delta, Integer.MAX_VALUE);
			case REMOVE -> Math2.fit(0, current - delta, Integer.MAX_VALUE);
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

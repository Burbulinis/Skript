package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner.mobspawnerdata;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptMobSpawnerData;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Spawner - Min/Max Spawn Delay")
@Description({
	"Get, set the maximum or minimum spawn delay of a spawner.",
	"Each reset of a spawner, the spawner chooses a new delay between its' "
		+ "minimum and maximum delays to use for the delay.",
	"By default, he maximum value is 40 seconds (800 ticks) and the minimum value is 10 seconds (200 ticks).",
	"Setting the minimum delay higher than the maximum delay and so on does nothing.",
	"",
	"Spawners are creature spawners and spawner minecarts."
})
@Examples({
	"set {_timespan} to minimum spawner delay of target block",
	"set max spawner delay of target block to 500 ticks",
	"add 100 ticks to min spawner delay of target block",
	"remove 50 ticks from max spawner delay of target block",
	"reset min spawner delay of target block"
})
@Since("INSERT VERSION")
public class ExprMinMaxDelay extends SimplePropertyExpression<SkriptMobSpawnerData, Timespan> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(ExprMinMaxDelay.class, Timespan.class,
			"(:max|min)[imum] spawn delay[s]", "mobspawnerdatas", true)
				.supplier(ExprMinMaxDelay::new)
				.build()
		);
	}

	private boolean max;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		max = parseResult.hasTag("max");
		return super.init(expressions, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public Timespan convert(SkriptMobSpawnerData data) {
		if (max)
			return data.getMaxSpawnDelay();
		return data.getMinSpawnDelay();
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

		for (SkriptMobSpawnerData data : getExpr().getArray(event)) {
			Timespan minMax;
			if (max) {
				minMax = data.getMaxSpawnDelay();
			} else {
				minMax = data.getMinSpawnDelay();
			}

			Timespan value = switch (mode) {
				case SET -> timespan;
				case ADD -> minMax.add(timespan);
				case REMOVE -> minMax.subtract(timespan);
				case RESET -> max ? SpawnerUtils.DEFAULT_MAX_SPAWN_DELAY : SpawnerUtils.DEFAULT_MIN_SPAWN_DELAY;
				default -> new Timespan();
			};

			assert value != null;

			if (max) {
				data.setMaxSpawnDelay(value);
			} else {
				data.setMinSpawnDelay(value);
			}

			if (max && value.compareTo(timespan) < 0) {
				warning("The maximum spawn delay cannot be lower than the minimum spawn delay, "
					+ "thus setting it to a value lower than the minimum spawn delay will do nothing.");
			} else if (!max && value.compareTo(timespan) > 0) {
				warning("The minimum spawn delay cannot be lower than the maximum spawn delay, "
					+ "thus setting it to a value lower than the maximum spawn delay will do nothing.");
			}
		}
	}

	@Override
	public Class<? extends Timespan> getReturnType() {
		return Timespan.class;
	}

	@Override
	protected String getPropertyName() {
		if (max)
			return "maximum spawn delay";
		return "minimum spawn delay";
	}

}

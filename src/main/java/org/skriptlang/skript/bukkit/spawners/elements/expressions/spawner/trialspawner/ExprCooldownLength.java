package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner.trialspawner;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.Block;
import org.bukkit.block.TrialSpawner;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Trial Spawner - Cooldown Length")
@Description({
	"Returns the cooldown length of a trial spawner.",
	"Once all the mobs have been killed, the trial spawner will wait for this amount of time before spawning more mobs.",
	"Default value is 30 minutes (36000 ticks)."
})
@Examples({
	"set {_cooldown} to trial spawner cooldown length of event-block",
	"broadcast \"The trial spawner will wait for %{_cooldown}% before spawning more mobs.\""
})
@Since("INSERT VERSION")
@RequiredPlugins("Minecraft 1.21+")
public class ExprCooldownLength extends SimplePropertyExpression<Block, Timespan> {

	public static void register(SyntaxRegistry registry) {
		if (!SpawnerUtils.IS_RUNNING_1_21)
			return;
		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(ExprCooldownLength.class, Timespan.class,
			"trial cool[ ]down [length]", "blocks", false)
				.supplier(ExprCooldownLength::new)
				.build()
		);
	}

	@Override
	public @Nullable Timespan convert(Block block) {
		if (SpawnerUtils.isTrialSpawner(block)) {
			TrialSpawner spawner = SpawnerUtils.getTrialSpawner(block);
			return new Timespan(TimePeriod.TICK, spawner.getCooldownLength());
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

		for (Block block : getExpr().getArray(event)) {
			if (!SpawnerUtils.isTrialSpawner(block))
				continue;

			TrialSpawner spawner = SpawnerUtils.getTrialSpawner(block);

			switch (mode) {
				case SET -> spawner.setCooldownLength(ticks);
				case ADD -> spawner.setCooldownLength(spawner.getCooldownLength() + ticks);
				case REMOVE -> spawner.setCooldownLength(spawner.getCooldownLength() - ticks);
				case RESET -> spawner.setCooldownLength((int) SpawnerUtils.DEFAULT_COOLDOWN_LENGTH.getAs(TimePeriod.TICK));
			}

			spawner.update(true, false);
		}
	}

	@Override
	public Class<? extends Timespan> getReturnType() {
		return Timespan.class;
	}

	@Override
	protected String getPropertyName() {
		return "trial cooldown length";
	}

}

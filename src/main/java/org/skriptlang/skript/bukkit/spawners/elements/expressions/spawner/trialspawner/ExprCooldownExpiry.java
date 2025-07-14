package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner.trialspawner;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.simplification.SimplifiedLiteral;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import ch.njol.util.Math2;
import org.bukkit.block.Block;
import org.bukkit.block.TrialSpawner;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.registration.SyntaxRegistry;

public class ExprCooldownExpiry extends SimplePropertyExpression<Block, Timespan> {

	public static void register(SyntaxRegistry registry) {
		if (!SpawnerUtils.IS_RUNNING_1_21_4)
			return;
		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(ExprCooldownExpiry.class, Timespan.class,
			"trial [spawner] cool[ ]down expir(y|ies)", "blocks", false)
				.supplier(ExprCooldownExpiry::new)
				.build()
		);
	}

	@Override
	public @Nullable Timespan convert(Block block) {
		if (!SpawnerUtils.isTrialSpawner(block))
			return null;

		TrialSpawner spawner = SpawnerUtils.getTrialSpawner(block);
		long ticks = Math2.fit(0, spawner.getCooldownEnd() - block.getWorld().getGameTime(), Long.MAX_VALUE);
		return new Timespan(TimePeriod.TICK, ticks);
	}

	@Override
	public Class<? extends Timespan> getReturnType() {
		return Timespan.class;
	}

	@Override
	protected String getPropertyName() {
		return "trial cooldown expiry";
	}

	@Override
	public Expression<? extends Timespan> simplify() {
		if (getExpr() instanceof Literal<? extends Block>)
			return SimplifiedLiteral.fromExpression(this);
		return this;
	}

}

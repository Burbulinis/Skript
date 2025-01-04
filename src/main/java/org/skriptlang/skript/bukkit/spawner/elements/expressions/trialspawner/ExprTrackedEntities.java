package org.skriptlang.skript.bukkit.spawner.elements.expressions.trialspawner;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.block.TrialSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawner.SpawnerModule;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxOrigin;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.ArrayList;
import java.util.List;

public class ExprTrackedEntities extends PropertyExpression<Block, Entity> {

	static {
		var info = SyntaxInfo.Expression.builder(ExprTrackedEntities.class, Entity.class)
			.origin(SyntaxOrigin.of(Skript.instance()))
			.supplier(ExprTrackedEntities::new)
			.priority(PropertyExpression.DEFAULT_PRIORITY)
			.addPatterns(
				"[the] tracked (1:player[s]|entit(y|ies)) (from|of) %blocks%",
				"%blocks%'[s] tracked (1:player[s]|entit(y|ies))")
			.build();

		SpawnerModule.SYNTAX_REGISTRY.register(SyntaxRegistry.EXPRESSION, info);
	}

	private boolean players;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		//noinspection unchecked
		setExpr((Expression<? extends Block>) exprs[0]);
		players = parseResult.mark == 1;
		return true;
	}

	@Override
	protected Entity[] get(Event event, Block[] source) {
		List<Entity> values = new ArrayList<>();

		for (Block block : source) {
			if (!(block.getState() instanceof TrialSpawner spawner))
				continue;

			if (players)
				values.addAll(spawner.getTrackedPlayers());
			else
				values.addAll(spawner.getTrackedEntities());
		}

		return values.toArray(new Entity[0]);
	}

	@Override
	public Class<? extends Entity> getReturnType() {
		return Entity.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);

		builder.append("tracked ");
		if (players) {
			builder.append("players");
		} else {
			builder.append("entities");
		}
		builder.append("of", getExpr());

		return builder.toString();
	}

}
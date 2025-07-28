package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner.trialspawner;

import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.simplification.SimplifiedLiteral;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.block.TrialSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.ArrayList;
import java.util.List;

@Name("Trial Spawner - Tracked Entities")
@Description({
	"Returns the tracked entities of the trial spawner.",
	"Tracked entities are entities that were spawned by the trial spawner, "
		+ "and the tracked players are players that were within the spawner's activation range.",
})
@Examples({
	"loop all tracked players of event-block:",
		"\tsend \"You're being tracked by %event-block%\" to loop-player"
})
@Since("INSERT VERSION")
@RequiredPlugins("Minecraft 1.21+")
public class ExprTrackedEntities extends PropertyExpression<Block, Entity> {

	public static void register(SyntaxRegistry registry) {
		if (!SpawnerUtils.IS_RUNNING_1_21)
			return;
		registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprTrackedEntities.class, Entity.class)
			.supplier(ExprTrackedEntities::new)
			.priority(DEFAULT_PRIORITY)
			.addPatterns(getPatterns("tracked player[s]", "blocks"))
			.addPatterns(getPatterns("tracked entit(y|ies)", "blocks"))
			.build()
		);
	}

	private boolean players;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		//noinspection unchecked
		setExpr((Expression<? extends Block>) exprs[0]);
		players = matchedPattern < 2;
		return true;
	}

	@Override
	protected Entity[] get(Event event, Block[] source) {
		List<Entity> values = new ArrayList<>();

		for (Block block : source) {
			if (!SpawnerUtils.isTrialSpawner(block))
				continue;

			TrialSpawner spawner = SpawnerUtils.getTrialSpawner(block);

			if (players) {
				values.addAll(spawner.getTrackedPlayers());
			} else {
				values.addAll(spawner.getTrackedEntities());
			}
		}

		return values.toArray(Entity[]::new);
	}

	@Override
	public Class<? extends Entity> getReturnType() {
		if (players)
			return Player.class;
		return Entity.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);

		builder.append("tracked");
		if (players) {
			builder.append("players");
		} else {
			builder.append("entities");
		}
		builder.append("of", getExpr());

		return builder.toString();
	}

}

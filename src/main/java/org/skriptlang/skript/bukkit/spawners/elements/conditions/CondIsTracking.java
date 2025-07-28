package org.skriptlang.skript.bukkit.spawners.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.block.TrialSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.SpawnerModule;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxOrigin;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Trial Spawner - Is Tracking")
@Description({
	"Check whether trial spawners or trial spawner configs are tracking players or entities.",
	"A player being tracked means the player has entered the activation range, meanwhile an entity being tracked means "
		+ "the entity was spawned by the trial spawner."
})
@Examples({
	"make the event-block start tracking player",
	"if the event-block is spawner player tracking player:",
		"\tsend \"indeed! you are being tracked..\""
})
@Since("INSERT VERSION")
@RequiredPlugins("Minecraft 1.21+")
public class CondIsTracking extends Condition {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.CONDITION, SyntaxInfo.builder(CondIsTracking.class)
			.supplier(CondIsTracking::new)
			.priority(SyntaxInfo.COMBINED)
			.addPatterns(
				"%blocks% (is|are) player tracking %players%",
				"%blocks% (isn't|is not|aren't|are not) player tracking %players%",
				"%blocks% (is|are) entity tracking %entities%",
				"%blocks% (isn't|is not|aren't|are not) entity tracking %entities%")
			.build()
		);
	}

	private Expression<Block> spawners;
	private Expression<Entity> entities;
	private boolean player;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		//noinspection unchecked
		spawners = (Expression<Block>) exprs[0];
		//noinspection unchecked
		entities = (Expression<Entity>) exprs[1];
		player = matchedPattern < 2;
		setNegated(matchedPattern == 1 || matchedPattern == 3);
		return true;
	}

	@Override
	public boolean check(Event event) {
		return spawners.check(event, block -> {
			if (!SpawnerUtils.isTrialSpawner(block))
				return false;

			TrialSpawner spawner = SpawnerUtils.getTrialSpawner(block);

			return entities.check(event, entity -> {
				if (player) {
					return spawner.isTrackingPlayer((Player) entity);
				} else {
					return spawner.isTrackingEntity(entity);
				}
			});

		}, isNegated());
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);

		builder.append(spawners);
		if (isNegated()) {
			builder.append("aren't");
		} else {
			builder.append("are");
		}

		if (player) {
			builder.append("player tracking");
		} else {
			builder.append("entity tracking");
		}
		builder.append(entities);

		return builder.toString();
	}

}

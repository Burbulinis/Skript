package org.skriptlang.skript.bukkit.spawners.elements.effects;

import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
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

@Name("Trial Spawner - Track")
@Description("Make the trial spawner or a trial spawner configuration start or stop tracking entities.")
@Examples({
	"if target block is not tracking player:",
		"\tmake the spawner target block start tracking player"
})
@Since("INSERT VERSION")
@RequiredPlugins("Minecraft 1.21+")
public class EffTrialSpawnerTrack extends Effect {

	public static void register(SyntaxRegistry registry) {
		if (!SpawnerUtils.IS_RUNNING_1_21)
			return;
		registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffTrialSpawnerTrack.class)
			.supplier(EffTrialSpawnerTrack::new)
			.priority(SyntaxInfo.COMBINED)
			.addPatterns(
				"make %blocks% (:start|stop) entity tracking %entities%",
				"make %blocks% (:start|stop) player tracking %players%")
			.build()
		);
	}

	private boolean start;
	private boolean player;
	private Expression<Block> blocks;
	private Expression<Entity> entities;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		start = parseResult.hasTag("start");
		player = matchedPattern == 1;
		//noinspection unchecked
		blocks = (Expression<Block>) exprs[0];
		//noinspection unchecked
		entities = (Expression<Entity>) exprs[1];
		return true;
	}

	@Override
	protected void execute(Event event) {
		for (Block spawner : blocks.getArray(event)) {
			if (!SpawnerUtils.isTrialSpawner(spawner))
				continue;

			TrialSpawner trialSpawner = SpawnerUtils.getTrialSpawner(spawner);

			assert trialSpawner != null;

			for (Entity entity : entities.getArray(event)) {
				if (player && entity instanceof Player playerEntity) {
					if (start) {
						trialSpawner.startTrackingPlayer(playerEntity);
					} else {
						trialSpawner.stopTrackingPlayer(playerEntity);
					}
				} else if (!player) {
					if (start) {
						trialSpawner.startTrackingEntity(entity);
					} else {
						trialSpawner.stopTrackingEntity(entity);
					}
				}
			}

			trialSpawner.update(true, false);
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);

		builder.append("make", blocks);
		if (start) {
			builder.append("start");
		} else {
			builder.append("stop");
		}
		builder.append("tracking", entities);

		return builder.toString();
	}

}

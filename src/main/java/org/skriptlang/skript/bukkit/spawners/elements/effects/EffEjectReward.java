package org.skriptlang.skript.bukkit.spawners.elements.effects;

import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.TrialSpawner;
import org.bukkit.block.data.type.TrialSpawner.State;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Trial Spawner - Eject Reward")
@Description("Make a trial spawner or a trial spawner configuration eject a reward out of it.")
@Examples("eject the trial spawner rewards of target block")
@Since("INSERT VERSION")
@RequiredPlugins("Minecraft 1.21+")
public class EffEjectReward extends Effect {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffEjectReward.class)
			.supplier(EffEjectReward::new)
			.priority(SyntaxInfo.COMBINED)
			.addPatterns(
				"(spit out|eject) [the] trial reward[s] of %blocks%",
				"(spit out|eject) %blocks%'[s] trial reward[s]")
			.build()
		);
	}

	private Expression<Block> blocks;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		//noinspection unchecked
		blocks = (Expression<Block>) exprs[0];
		return true;
	}

	@Override
	protected void execute(Event event) {
		for (Block block : blocks.getArray(event)) {
			if (!SpawnerUtils.isTrialSpawner(block))
				continue;

			org.bukkit.block.TrialSpawner state = SpawnerUtils.getTrialSpawner(block);
			TrialSpawner data = (TrialSpawner) state.getBlockData();

			data.setTrialSpawnerState(State.EJECTING_REWARD);

			state.setBlockData(data);
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "spit out the trial rewards of " + blocks.toString(event, debug);
	}

}

package org.skriptlang.skript.bukkit.spawners.elements.effects;

import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TrialSpawner;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.SpawnerModule;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxOrigin;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Trial Spawner - Make Ominous")
@Description("Make a trial spawner or its block data ominous or normal.")
@Examples({
	"if event-block is ominous:",
		"\tmake the trial spawner state of event-block normal",
	"else:",
		"\tmake the trial spawner state of event-block ominous"
})
@Since("INSERT VERSION")
@RequiredPlugins("Minecraft 1.21+")
public class EffMakeOminous extends Effect {

	public static void register(SyntaxRegistry registry) {
		if (!SpawnerUtils.IS_RUNNING_1_21)
			return;
		registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffMakeOminous.class)
			.supplier(EffMakeOminous::new)
			.priority(SyntaxInfo.COMBINED)
			.addPatterns("make %blocks/blockdatas% (:ominous|regular|normal)")
			.build()
		);
	}

	private boolean ominous;
	private Expression<?> spawners;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		ominous = parseResult.hasTag("ominous");
		spawners = exprs[0];
		return true;
	}

	@Override
	protected void execute(Event event) {
		for (Object object : spawners.getArray(event)) {
			if (object instanceof BlockData data && data instanceof TrialSpawner spawner) {
				spawner.setOminous(ominous);
			} else if (object instanceof Block block && block.getState() instanceof org.bukkit.block.TrialSpawner spawner) {
				spawner.setOminous(ominous);
				spawner.update(true, false);
			}
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);

		builder.append("make", spawners);
		if (ominous) {
			builder.append("ominous");
		} else {
			builder.append("normal");
		}

		return builder.toString();
	}

}

package org.skriptlang.skript.bukkit.spawners.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.*;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Trial Spawner - Is Ominous")
@Description(
	"Check if a spawner is ominous. This is used for trial spawners, "
		+ "trial spawner configurations and trial spawner block data."
)
@Examples({
	"if the block at player is ominous:",
		"\tsend \"The spawner is ominous!\"",
	"set {_config} to normal trial spawner config of block at player",
	"if {_config} is not ominous:",
		"\tsend \"That's true! The config is not ominous.\""
})
@Since("INSERT VERSION")
@RequiredPlugins("Minecraft 1.21+")
public class CondIsOminous extends PropertyCondition<Object> {

	public static void register(SyntaxRegistry registry) {
		if (!SpawnerUtils.IS_RUNNING_1_21)
			return;
		registry.register(SyntaxRegistry.CONDITION, infoBuilder(CondIsOminous.class, PropertyType.BE,
			"ominous", "blocks/blockdatas")
				.supplier(CondIsOminous::new)
				.build()
		);
	}

	@Override
	public boolean check(Object object) {
		if (SpawnerUtils.isTrialSpawner(object)) {
			return SpawnerUtils.getTrialSpawner(object).isOminous();
		} else if (object instanceof org.bukkit.block.data.type.TrialSpawner spawner) {
			return spawner.isOminous();
		}

		return false;
	}

	@Override
	protected String getPropertyName() {
		return "ominous";
	}

}

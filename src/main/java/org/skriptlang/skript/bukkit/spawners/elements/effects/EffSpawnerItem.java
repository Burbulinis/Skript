package org.skriptlang.skript.bukkit.spawners.elements.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

public class EffSpawnerItem extends Effect {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(EffSpawnerItem.class)
			.supplier(EffSpawnerItem::new)
			.priority(SyntaxInfo.COMBINED)
			.addPatterns(
				"make " + SpawnerUtils.spawnerPropertyType + " spawn %itemstack%",
				"force " + SpawnerUtils.spawnerPropertyType + " to spawn %itemstack%")
			.build()
		);
	}

	private Expression<?> spawners;
	private Expression<ItemStack> itemStack;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		spawners = exprs[0];
		//noinspection unchecked
		itemStack = (Expression<ItemStack>) exprs[1];
		return true;
	}

	@Override
	protected void execute(Event event) {
		ItemStack item = itemStack.getSingle(event);
		if (item == null)
			return;

		for (Object object : spawners.getArray(event)) {
			if (SpawnerUtils.isCreatureSpawner(object)) {
				CreatureSpawner spawner = SpawnerUtils.getCreatureSpawner(object);
				spawner.setSpawnedItem(item);
				spawner.update(true, false);
			} else if (SpawnerUtils.isSpawnerMinecart(object)) {
				SpawnerMinecart minecart = SpawnerUtils.getSpawnerMinecart(object);
				minecart.setSpawnedItem(item);
			}
		}
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "force " + spawners.toString(event, debug) + " to spawn " + itemStack.toString(event, debug);
	}

}

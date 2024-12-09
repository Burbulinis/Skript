package org.skriptlang.skript.bukkit.loottables.elements.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.Lootable;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.loottables.LootTableUtils;

import java.util.function.Consumer;

@Name("Loot Table")
@Description({
	"Returns the loot table of an entity, block or loot table type.",
	"Setting the loot table of a block will update the block state, and once opened will " +
		"generate loot of the specified loot table. Please note that doing so may cause " +
		"warnings in the console due to over-filling the chest.",
	"Please note that resetting/deleting the loot table of an ENTITY will do nothing.",
	"",
	"You can find all of the vanilla loot tables at https://mcreator.net/wiki/minecraft-vanilla-loot-tables-list."
})
@Examples({
	"set {_loot} to loot table of event-entity",
	"set {_loot} to loot table of event-block",
	"",
	"set loot table of event-entity to \"minecraft:entities/ghast\"",
	"# this will set the loot table of the entity to a ghast's loot table, thus dropping ghast tears and gunpowder",
	"",
	"set loot table of event-block to minecraft:chests/simple_dungeon",
	"set loot table of event-block to simple dungeon loot table"
})
@Since("INSERT VERSION")
public class ExprLootTable extends SimplePropertyExpression<Object, LootTable> {

	static {
		register(ExprLootTable.class, LootTable.class, "loot[ ]table[s]", "entities/blocks");
	}

	@Override
	public @Nullable LootTable convert(Object object) {
		return LootTableUtils.getLootTable(object);
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, DELETE, RESET -> CollectionUtils.array(LootTable.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		LootTable lootTable = delta != null ? ((LootTable) delta[0]) : null;

		Consumer<Lootable> consumer = (lootable) -> {};
		if (mode == ChangeMode.SET)
			consumer = (lootable) -> lootable.setLootTable(lootTable);
		else if (mode == ChangeMode.DELETE || mode == ChangeMode.RESET)
			consumer = (lootable) -> lootable.setLootTable(null);

		for (Object object : getExpr().getArray(event)) {
			if (!LootTableUtils.isLootable(object))
				continue;

			Lootable lootable = LootTableUtils.getAsLootable(object);

			consumer.accept(lootable);
			LootTableUtils.updateState(lootable);
		}
	}

	@Override
	public Class<? extends LootTable> getReturnType() {
		return LootTable.class;
	}

	@Override
	protected String getPropertyName() {
		return "loot table";
	}

}
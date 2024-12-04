/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Name("Loot Table Items")
@Description({
	"Returns the items of a loot table using a loot context.",
	"Note that loot contexts only require the killer and looted entity to be set if the loot table is under the entities category.",
	"The loot context only requires the location if the loot table is not in the entities category, eg. blocks, chests."
})
@Examples({
	"set {_items::*} to loot items of the loot table \"minecraft:chests/simple_dungeon\" with loot context {_context}",
	"# this will set {_items::*} to the items that would be dropped from the simple dungeon loot table with the given loot context",
	"",
	"give player loot items of entity's loot table with loot context {_context}",
	"# this will give the player the items that the entity would drop with the given loot context"
})
@Since("INSERT VERSION")
public class ExprLootItems extends SimpleExpression<ItemStack> {

	static {
		Skript.registerExpression(ExprLootItems.class, ItemStack.class, ExpressionType.COMBINED,
			"[the] loot[[ ]item[s]] of %loottables% (with|using) [the] [[loot] context] %lootcontext%",
			"[the] %loottables%'[s] loot[[ ]item[s]] (with|using) [the] [[loot] context] %lootcontext%"
		);
	}

	private Expression<LootTable> lootTables;
	private Expression<LootContext> lootContext;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		lootTables = (Expression<LootTable>) exprs[0];
		lootContext = (Expression<LootContext>) exprs[1];
		return true;
	}

	@Override
	protected @Nullable ItemStack[] get(Event event) {
		List<ItemStack> items = new ArrayList<>();
		LootContext context = lootContext.getSingle(event);
		if (context == null)
			return new ItemStack[0];

		Random random = ThreadLocalRandom.current();
		for (LootTable lootTable : lootTables.getArray(event)) {
			try {
				items.addAll(lootTable.populateLoot(random, context));
			}
			catch (IllegalArgumentException ignore) {}
		}
		return items.toArray(new ItemStack[0]);
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends ItemStack> getReturnType() {
		return ItemStack.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "the loot items of " + lootTables.toString(event, debug) + " with loot context " + lootContext.toString(event, debug);
	}
}

package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner.trialspawnerdata;

import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.bukkit.spawners.util.TrialSpawnerRewardEntry;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Trial Spawner Config - Weighted Loot Table")
@Description({
	"Returns a weighted loot table with the specified weight",
	"Used for trial spawners to pick out a reward loot table from a list of loot tables, "
		+ "where the bigger the weight, the higher the chance of the loot table being picked."
})
@Examples({
	"set {_loot} to loot table \"minecraft:chests/simple_dungeon\"",
	"set {_weighted} to {_loot} with weight 5",
	"set {_weighted} to {_loot} with weight 10"
})
@Since("INSERT VERSION")
@RequiredPlugins("Minecraft 1.21+")
public class ExprRewardEntry extends SimpleExpression<TrialSpawnerRewardEntry> {

	public static void register(SyntaxRegistry registry) {
		if (!SpawnerUtils.IS_RUNNING_1_21)
			return;
		registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprRewardEntry.class, TrialSpawnerRewardEntry.class)
			.supplier(ExprRewardEntry::new)
			.priority(SyntaxInfo.COMBINED)
			.addPattern("[[trial] spawner] reward entry of %loottable% [with weight %-integer%]")
			.build()
		);
	}

	private Expression<LootTable> lootTable;
	private Expression<Integer> weight;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		//noinspection unchecked
		lootTable = (Expression<LootTable>) exprs[0];
		//noinspection unchecked
		weight = (Expression<Integer>) exprs[1];
		return true;
	}

	@Override
	protected TrialSpawnerRewardEntry @Nullable [] get(Event event) {
		LootTable lootTable = this.lootTable.getSingle(event);
		if (lootTable == null)
			return null;

		Integer weight = this.weight.getSingle(event);
		if (weight == null)
			weight = 1;

		return new TrialSpawnerRewardEntry[]{new TrialSpawnerRewardEntry(lootTable, weight)};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends TrialSpawnerRewardEntry> getReturnType() {
		return TrialSpawnerRewardEntry.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);
		builder.append("trial spawner reward entry of ", lootTable, "with weight");
		if (weight != null) {
			builder.append(weight);
		} else {
			builder.append("1");
		}
		return builder.toString();
	}

}

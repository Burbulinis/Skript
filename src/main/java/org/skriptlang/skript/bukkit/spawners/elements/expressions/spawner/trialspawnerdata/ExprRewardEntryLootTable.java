package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner.trialspawnerdata;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.TrialSpawnerRewardEntry;
import org.skriptlang.skript.registration.SyntaxRegistry;

public class ExprRewardEntryLootTable extends SimplePropertyExpression<TrialSpawnerRewardEntry, LootTable> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(ExprRewardEntryLootTable.class, LootTable.class,
			"reward [entry] loot table[s]", "trialspawnerrewardentries", true)
				.supplier(ExprRewardEntryLootTable::new)
				.build()
		);
	}

	@Override
	public @Nullable LootTable convert(TrialSpawnerRewardEntry reward) {
		return reward.lootTable();
	}

	@Override
	public Class<? extends LootTable> getReturnType() {
		return LootTable.class;
	}

	@Override
	protected String getPropertyName() {
		return "reward loot table";
	}

}

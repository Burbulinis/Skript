package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner.trialspawnerdata;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptTrialSpawnerData;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.*;

@Name("Trial Spawner Configuration with Weighted Loot Table")
@Description({
	"Returns the weighted loot tables of a trial spawner configuration.",
	"Weighted loot tables are loot tables with a weight, which determines the chance of the loot table "
		+ "being selected during the spawner's reward ejection state.",
	"Adding just a regular loot table to this list will default the weight to 1."
})
@Examples({
	"set {_loot tables::*} to weighted loot tables of {_trial config}",
	"add loot table \"minecraft:equipment/trial_chamber\" with weight 1 to weighted loot tables of {_trial config}",
	"add loot table \"minecraft:chests/simple_dungeon\" to weighted loot tables of {_trial config}",
	"# loot table with weight 1 ^"
})
@Since("INSERT VERSION")
@RequiredPlugins("Minecraft 1.21+")
public class ExprRewardEntries extends PropertyExpression<SkriptTrialSpawnerData, LootTable> {

	public static void register(SyntaxRegistry syntaxRegistry) {
		if (!SpawnerUtils.IS_RUNNING_1_21)
			return;
		syntaxRegistry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprRewardEntries.class, LootTable.class)
			.supplier(ExprRewardEntries::new)
			.priority(DEFAULT_PRIORITY)
			.addPatterns(getDefaultPatterns("reward entr(y|ies)", "trialspawnerdatas"))
			.build()
		);
	}

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		//noinspection unchecked
		setExpr((Expression<? extends SkriptTrialSpawnerData>) exprs[0]);
		return true;
	}

	@Override
	protected LootTable[] get(Event event, SkriptTrialSpawnerData[] source) {
		return Arrays.stream(source)
			.flatMap(data -> data.getRewardEntries().keySet().stream())
			.toArray(LootTable[]::new);
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, ADD, REMOVE, DELETE -> CollectionUtils.array(LootTable[].class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		Map<LootTable, Integer> lootTables = new HashMap<>();
		if (delta != null) {
			for (Object object : delta) {
				lootTables.put((LootTable) object, 1);
			}
		}

		for (SkriptTrialSpawnerData data : getExpr().getArray(event)) {
			if (mode == ChangeMode.DELETE) {
				data.clearRewardEntries();
				continue;
			}

			Map<LootTable, Integer> currentEntries = data.getRewardEntries();
			switch (mode) {
				case SET -> currentEntries = lootTables;
				case ADD -> currentEntries.putAll(lootTables);
				case REMOVE -> lootTables.keySet().forEach(currentEntries::remove);
			}

			data.setRewardEntries(currentEntries);
		}
	}

	@Override
	public Class<? extends LootTable> getReturnType() {
		return LootTable.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "reward entries of " + getExpr().toString(event, debug);
	}

}

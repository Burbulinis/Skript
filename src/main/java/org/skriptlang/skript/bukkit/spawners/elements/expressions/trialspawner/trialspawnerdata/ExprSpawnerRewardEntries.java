package org.skriptlang.skript.bukkit.spawners.elements.expressions.trialspawner.trialspawnerdata;

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
import org.skriptlang.skript.bukkit.spawners.util.TrialSpawnerRewardEntry;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptTrialSpawnerData;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.ArrayList;
import java.util.List;

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
public class ExprSpawnerRewardEntries extends PropertyExpression<SkriptTrialSpawnerData, TrialSpawnerRewardEntry> {

	public static void register(SyntaxRegistry syntaxRegistry) {
		if (!SpawnerUtils.IS_RUNNING_1_21)
			return;
		syntaxRegistry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprSpawnerRewardEntries.class, TrialSpawnerRewardEntry.class)
			.supplier(ExprSpawnerRewardEntries::new)
			.priority(PropertyExpression.DEFAULT_PRIORITY)
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
	protected TrialSpawnerRewardEntry[] get(Event event, SkriptTrialSpawnerData[] source) {
		List<TrialSpawnerRewardEntry> entries = new ArrayList<>();
		for (SkriptTrialSpawnerData data : source) {
			entries.addAll(data.getRewardEntries());
		}
		return entries.toArray(TrialSpawnerRewardEntry[]::new);
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, ADD, REMOVE, DELETE -> CollectionUtils.array(TrialSpawnerRewardEntry[].class, LootTable[].class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		List<TrialSpawnerRewardEntry> entries = new ArrayList<>();
		if (delta != null) {
			for (Object object : delta) {
				if (object instanceof TrialSpawnerRewardEntry entry) {
					entries.add(entry);
				} else if (object instanceof LootTable lootTable) {
					entries.add(new TrialSpawnerRewardEntry(lootTable, 1));
				}
			}
		}

		for (SkriptTrialSpawnerData data : getExpr().getArray(event)) {
			switch (mode) {
				case SET -> data.setRewardEntries(entries);
				case ADD -> data.addRewardEntries(entries);
				case REMOVE -> data.removeRewardEntries(entries);
				case DELETE -> data.clearRewardEntries();
			}
		}
	}

	@Override
	public Class<? extends TrialSpawnerRewardEntry> getReturnType() {
		return TrialSpawnerRewardEntry.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "reward entries of " + getExpr().toString(event, debug);
	}

}

package org.skriptlang.skript.bukkit.spawners.elements.expressions.trialspawner.trialspawnerdata;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptTrialSpawnerData;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Trial Spawner Configuration - Mob Count")
@Description({
	"Returns the total or simultaneous mob count of the trial spawner configuration.",
	"Both the simultaneous and total mob count "
		+ "increases once another player comes within the spawner's range. "
		+ "For each additional player present, the simultaneous mob count "
		+ "increases by the simultaneous mob count per player, "
		+ "and the total mob count increases by the total mob count per player. "
		+ "Assuming you are using the default values, with 2 players,"
		+ "8 mobs spawn in total with 3 at once, "
		+ "and with 3 players, 10 mobs spawn in total with 4 at once.",
	"The default value for the total mob count is 2, and the default value for the simultaneous mob count is 6.",
	"The trial spawner will stop spawning mobs if the number of living mobs spawned by it reached the total mob count."
})
@Examples({
	"send \"The trial spawner is spawning %the total trial spawner mob count of {_spawner}% mobs in total.\"",
	"send \"The trial spawner is spawning %the simultaneous trial spawner mob count of {_spawner}% mobs at once.\"",
	"",
	"set the total trial spawner mob count of {_spawner} to 10",
	"set the simultaneous trial spawner mob count of {_spawner} to 4",
})
@Since("INSERT VERSION")
@RequiredPlugins("Minecraft 1.21+")
public class ExprBaseEntityCount extends SimplePropertyExpression<SkriptTrialSpawnerData, Integer> {

	public static void register(SyntaxRegistry registry) {
		if (!SpawnerUtils.IS_RUNNING_1_21)
			return;
		registerDefault(registry, ExprBaseEntityCount.class, Integer.class,
			"base [concurrent:(concurrent|simultaneous)] (mob|entity) [spawn] (count|amount)",
			"trialspawnerdatas"
		);
	}

	private boolean concurrent;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		concurrent = parseResult.hasTag("concurrent");
		return super.init(expressions, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public Integer convert(SkriptTrialSpawnerData data) {
		if (concurrent)
			return data.getConcurrentMobAmount();
		return data.getBaseMobAmount();
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, ADD, REMOVE, RESET -> CollectionUtils.array(Integer.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		int count = delta != null ? ((int) delta[0]) : 0;
		for (SkriptTrialSpawnerData data : getExpr().getArray(event)) {
			if (concurrent) {
				switch (mode) {
					case SET -> data.setConcurrentMobAmount(count);
					case ADD -> data.setConcurrentMobAmount(data.getConcurrentMobAmount() + count);
					case REMOVE -> data.setConcurrentMobAmount(data.getConcurrentMobAmount() - count);
					case RESET -> data.setConcurrentMobAmount(SpawnerUtils.DEFAULT_CONCURRENT_MOB_AMOUNT);
				}
			} else {
				switch (mode) {
					case SET -> data.setBaseMobAmount(count);
					case ADD -> data.setBaseMobAmount(data.getBaseMobAmount() + count);
					case REMOVE -> data.setBaseMobAmount(data.getBaseMobAmount() - count);
					case RESET -> data.setBaseMobAmount(SpawnerUtils.DEFAULT_BASE_MOB_AMOUNT);
				}
			}
		}
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	protected String getPropertyName() {
		return "base" + (concurrent ? "concurrent " : " ") + "mob count";
	}

}

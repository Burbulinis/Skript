package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner.mobspawnerdata;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptMobSpawnerData;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Spawner - Spawn Count")
@Description({
	"Returns the spawn count.",
	"The spawn count is the number of entities "
		+ "that the spawner will attempt to spawn each spawn attempt. By default, the value is 4.",
	"If the spawner entity is an item, the spawn count is the number of stacks of items to spawn.",
	"",
	"Spawners are creature spawners and spawner minecarts."
})
@Examples({
	"set spawn count of target block to 5",
	"add 2 to spawn count of target block",
	"remove 1 from spawn count of target block",
	"reset spawn count of target block"
})
@Since("INSERT VERSION")
public class ExprSpawnCount extends SimplePropertyExpression<SkriptMobSpawnerData, Integer> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(ExprSpawnCount.class, Integer.class,
			"spawn (count|amount)", "mobspawnerdatas", true)
				.supplier(ExprSpawnCount::new)
				.build()
		);
	}

	@Override
	public Integer convert(SkriptMobSpawnerData data) {
		return data.getSpawnCount();
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

		for (SkriptMobSpawnerData data : getExpr().getArray(event)) {
			switch (mode) {
				case SET -> data.setSpawnCount(count);
				case ADD -> data.setSpawnCount(data.getSpawnCount() + count);
				case REMOVE -> data.setSpawnCount(data.getSpawnCount() - count);
				case RESET -> data.setSpawnCount(SpawnerUtils.DEFAULT_SPAWN_RANGE);
			}
		}
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	protected String getPropertyName() {
		return "spawn count";
	}

}

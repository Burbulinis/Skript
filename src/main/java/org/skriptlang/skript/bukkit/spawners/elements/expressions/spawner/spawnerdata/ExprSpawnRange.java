package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner.spawnerdata;

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
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptSpawnerData;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Spawner Spawn Range")
@Description({
	"Get the radius of the area in which the spawner can spawn entities, by default 4.",
	"This expression gets the trial spawner configuration "
		+ "with the current state (i.e. ominous, normal) of the trial spawner block, if one is provided.",
	"",
	"Base spawners are trial spawner configurations, spawner minecarts and creature spawners."
})
@Examples({
	"set the spawner spawn radius of the target block to 5",
	"add 2 to the spawner spawn radius of the target block",
	"remove 1 from the spawner spawn radius of the target block",
	"reset the spawner spawn radius of the target block"
})
@Since("INSERT VERSION")
public class ExprSpawnRange extends SimplePropertyExpression<SkriptSpawnerData, Integer> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(ExprSpawnRange.class, Integer.class,
			"spawn (radi(us[es]|i)|range[s])", "spawnerdatas", true)
				.supplier(ExprSpawnRange::new)
				.build()
		);
	}

	@Override
	public Integer convert(SkriptSpawnerData data) {
		return data.getSpawnRange();
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
		int range = delta != null ? ((int) delta[0]) : 0;

		for (SkriptSpawnerData data : getExpr().getArray(event)) {
			int base = data.getSpawnRange();
			data.setSpawnRange(switch (mode) {
				case ADD -> base + range;
				case REMOVE -> base - range;
				case RESET -> SpawnerUtils.DEFAULT_SPAWN_RANGE;
				default -> range;
			});
		}
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	protected String getPropertyName() {
		return "spawn range";
	}

}

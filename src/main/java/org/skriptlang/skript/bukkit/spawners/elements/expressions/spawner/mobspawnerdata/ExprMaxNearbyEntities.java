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

@Name("Spawner - Maximum Nearby Entity Amount")
@Description({
	"Get the maximum amount of similar entities within the spawn range. This is 6 by default.",
	"The spawner will no longer spawn entities if the value was surpassed.",
})
@Examples({
	"set {_max} to spawner nearby entity amount of target block",
	"add 5 to spawner nearby entity amount of target block",
	"remove 2 from spawner nearby entity amount of target block",
	"reset spawner nearby entity amount of target block"
})
@Since("INSERT VERSION")
public class ExprMaxNearbyEntities extends SimplePropertyExpression<SkriptMobSpawnerData, Integer> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(ExprMaxNearbyEntities.class, Integer.class,
			"max[imum] nearby entity (count|amount|cap)[s]", "mobspawnerdatas", true)
				.supplier(ExprMaxNearbyEntities::new)
				.build()
		);
	}

	@Override
	public Integer convert(SkriptMobSpawnerData data) {
		return data.getMaxNearbyEntityCap();
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

		for (SkriptMobSpawnerData data: getExpr().getArray(event)) {
			int base = data.getMaxNearbyEntityCap();
			data.setMaxNearbyEntityCap(switch (mode) {
				case ADD -> base + count;
				case REMOVE -> base - count;
				case RESET -> SpawnerUtils.DEFAULT_MAX_NEARBY_ENTITIES;
				default -> count;
			});
		}
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	protected String getPropertyName() {
		return "maximum nearby entity cap";
	}

}

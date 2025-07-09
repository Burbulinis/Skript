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
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptTrialSpawnerData;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Base Spawner - Activation Range")
@Description({
	"Gets the activation range of the base spawner. By default, this is 16.",
	"The activation range is the distance "
		+ "from the spawner that players must be within for the spawner to be active.",
	"Setting this value to less than or equal to 0, makes the spawner always active "
		+ "(given that there are players online).",
	"This expression allows trial spawners and trial spawner configurations.",
	"",
	"Base spawners are trial spawner configurations, spawner minecarts and creature spawners."
})
@Examples({
	"set {_range} to spawner activation range of target block",
	"set spawner activation range of target block to 32"
})
@Since("INSERT VERSION")
public class ExprActivationRange extends SimplePropertyExpression<SkriptSpawnerData, Integer> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(
			ExprActivationRange.class, Integer.class,
			"activation (radius|range)", "spawnerdatas", true
		).build());
	}

	@Override
	public Integer convert(SkriptSpawnerData data) {
		return data.getActivationRange();
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
		for (SkriptSpawnerData data : getExpr().getArray(event)) {
			switch (mode) {
				case SET -> data.setActivationRange(count);
				case ADD -> data.setActivationRange(data.getActivationRange() + count);
				case REMOVE -> data.setActivationRange(data.getActivationRange() - count);
				case RESET -> {
					int defaultValue = data instanceof SkriptTrialSpawnerData
						? SpawnerUtils.DEFAULT_TRIAL_ACTIVATION_RANGE
						: SpawnerUtils.DEFAULT_ACTIVATION_RANGE;
					data.setActivationRange(defaultValue);
				}
			}
		}
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	protected String getPropertyName () {
		return "activation range";
	}

}

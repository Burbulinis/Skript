package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner.spawnerdata;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptMobSpawnerData;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptSpawnerData;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptTrialSpawnerData;
import org.skriptlang.skript.registration.SyntaxRegistry;

@RequiredPlugins("Minecraft 1.21+ (for trial spawner data)")
public class ExprSpawnerData extends SimplePropertyExpression<Object, SkriptSpawnerData> {

	public static void register(SyntaxRegistry registry) {
		var info = infoBuilder(ExprSpawnerData.class, SkriptSpawnerData.class,
			"mob spawner data", SpawnerUtils.spawnerPropertyType, false)
				.supplier(ExprSpawnerData::new);

		if (SpawnerUtils.IS_RUNNING_1_21)
			info.addPatterns("[:ominous] trial spawner data", "blocks");

		registry.register(SyntaxRegistry.EXPRESSION, info.build());
	}

	private boolean trial;
	private boolean ominous;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		trial = matchedPattern == 1;
		ominous = parseResult.hasTag("ominous");
		return super.init(expressions, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public @Nullable SkriptSpawnerData convert(Object object) {
		if (!trial && SpawnerUtils.isCreatureSpawner(object)) {
			return SkriptMobSpawnerData.fromSpawner(SpawnerUtils.getCreatureSpawner(object));
		} else if (!trial && SpawnerUtils.isSpawnerMinecart(object)) {
			return SkriptMobSpawnerData.fromSpawner(SpawnerUtils.getSpawnerMinecart(object));
		} else if (trial && SpawnerUtils.isTrialSpawner(object)) {
			return SkriptTrialSpawnerData.fromTrialSpawner(SpawnerUtils.getTrialSpawner(object), ominous);
		}

		return null;
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, RESET -> {
				if (trial)
					yield CollectionUtils.array(SkriptTrialSpawnerData.class);
				yield CollectionUtils.array(SkriptMobSpawnerData.class);
			}
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		SkriptSpawnerData data = delta != null ? (SkriptSpawnerData) delta[0] : null;

		if (data == null)
			data = trial ? new SkriptTrialSpawnerData() : new SkriptMobSpawnerData();

		for (Object object : getExpr().getArray(event)) {
			if (!trial && SpawnerUtils.isCreatureSpawner(object)) {
				((SkriptMobSpawnerData) data).applyDataToSpawner(SpawnerUtils.getCreatureSpawner(object));
			} else if (!trial && SpawnerUtils.isSpawnerMinecart(object)) {
				((SkriptMobSpawnerData) data).applyDataToSpawner(SpawnerUtils.getSpawnerMinecart(object));
			} else if (trial && SpawnerUtils.isTrialSpawner(object)) {
				((SkriptTrialSpawnerData) data).applyDataToTrialSpawner(SpawnerUtils.getTrialSpawner(object), ominous);
			}
		}
	}

	@Override
	public Class<? extends SkriptSpawnerData> getReturnType() {
		if (trial)
			return SkriptTrialSpawnerData.class;
		return SkriptMobSpawnerData.class;
	}

	@Override
	protected String getPropertyName() {
		if (trial)
			return (ominous ? "ominous " : "") + "trial spawner data";
		return "mob spawner data";
	}

}

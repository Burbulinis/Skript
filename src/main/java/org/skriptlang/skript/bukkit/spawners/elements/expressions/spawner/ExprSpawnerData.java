package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptMobSpawnerData;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptSpawnerData;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptTrialSpawnerData;
import org.skriptlang.skript.registration.SyntaxRegistry;

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

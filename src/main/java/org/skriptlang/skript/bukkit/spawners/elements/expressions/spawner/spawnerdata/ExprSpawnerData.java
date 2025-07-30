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
import org.skriptlang.skript.bukkit.spawners.util.SpawnerDataType;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptMobSpawnerData;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptSpawnerData;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptTrialSpawnerData;
import org.skriptlang.skript.registration.SyntaxRegistry;

@RequiredPlugins("Minecraft 1.21+ (for trial spawner data)")
public class ExprSpawnerData extends SimplePropertyExpression<Object, SkriptSpawnerData> {

	public static void register(SyntaxRegistry registry) {
		String property = "[:mob] spawner data";
		if (SpawnerUtils.IS_RUNNING_1_21)
			property = "[trial:[:ominous] trial|:mob] spawner data";

		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(ExprSpawnerData.class, SkriptSpawnerData.class,
			property, SpawnerUtils.spawnerPropertyType, false)
				.supplier(ExprSpawnerData::new)
				.build()
		);
	}

	private SpawnerDataType dataType;
	private boolean ominous;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		dataType = SpawnerDataType.fromTags(parseResult.tags);
		ominous = parseResult.hasTag("ominous");
		return super.init(expressions, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public @Nullable SkriptSpawnerData convert(Object object) {
		return SpawnerUtils.getDataFromObject(object, dataType);
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, RESET -> CollectionUtils.array(dataType.getDataClass());
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		SkriptSpawnerData data = delta != null ? (SkriptSpawnerData) delta[0] : null;

		for (Object object : getExpr().getArray(event)) {
			if (data == null)
				data = SpawnerUtils.getDataFromObject(object, dataType);

			if (data == null)
				continue;

			if (!dataType.isTrial() && data instanceof SkriptMobSpawnerData mobData) {
				SpawnerUtils.applyToMobSpawner(object, mobData);
			} else if (dataType == SpawnerDataType.TRIAL
				&& SpawnerUtils.isTrialSpawner(object)
				&& data instanceof SkriptTrialSpawnerData trialData)
			{
				trialData.applyDataToTrialSpawner(SpawnerUtils.getTrialSpawner(object), ominous);
			}
		}
	}

	@Override
	public Class<? extends SkriptSpawnerData> getReturnType() {
		return dataType.getDataClass();
	}

	@Override
	protected String getPropertyName() {
		if (dataType == SpawnerDataType.MOB) {
			return "mob spawner data";
		} else if (dataType == SpawnerDataType.TRIAL) {
			return (ominous ? "ominous " : "") + "trial spawner data";
		}
		return "spawner data";
	}

}

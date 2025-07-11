package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawnrule;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.Math2;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.spawner.SpawnRule;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.SpawnerModule;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Spawn Rule - Sky Light Spawn Level")
@Description({
	"Returns the minimum/maximum sky light spawn levels of a spawn rule. "
		+ "The sky light spawn levels determine the light level of the sky "
		+ "that the spawner entry will spawn entities.",
	"Note that the sky light spawn levels must be between 0 and 15, "
		+ "the minimum sky light spawn level must be less than or equal to "
		+ "the maximum sky light spawn level and vice versa."
})
@Examples({
	"set {_entry} to a spawner entry using entity snapshot of a zombie:",
		"\tset the weight to 5",
		"\tset the spawn rule to a spawn rule:",
			"\t\tset the minimum block light spawn level to 10",
			"\t\tset the maximum block light spawn level to 15",
			"\t\tset the minimum sky light spawn level to 5",
			"\t\tset the maximum sky light spawn level to 15",
	"set spawner entity of event-block to {_entry}"
})
@Since("INSERT VERSION")
public class ExprSpawnRuleSkyLight extends SimplePropertyExpression<SpawnRule, Integer> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(ExprSpawnRuleBlockLight.class, Integer.class,
			"(1:max|min)[imum] sky light [entity] spawn [rule] (level|value)[s]", "spawnrules", true)
			.supplier(ExprSpawnRuleBlockLight::new)
			.build()
		);
	}


	private boolean max;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		max = parseResult.mark == 1;
		return super.init(expressions, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public Integer convert(SpawnRule rule) {
		if (max)
			return rule.getMaxSkyLight();
		return rule.getMinSkyLight();
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, ADD, REMOVE -> CollectionUtils.array(Integer.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		assert delta != null;
		int light = Math2.fit(0, (int) delta[0], 15);

		for (SpawnRule rule : getExpr().getArray(event)) {
			int minMax;
			if (max) {
				minMax = rule.getMaxSkyLight();
			} else {
				minMax = rule.getMinSkyLight();
			}

			int value = switch (mode) {
				case SET -> light;
				case ADD -> minMax + light;
				case REMOVE -> minMax - light;
				default -> 0;
			};

			value = Math2.fit(0, value, 15);

			String error = getErrorMessage(value, max ? rule.getMinSkyLight() : rule.getMaxSkyLight());
			if (error != null) {
				error(error);
				continue;
			}

			if (max) {
				rule.setMaxSkyLight(value);
			} else {
				rule.setMinSkyLight(value);
			}
		}
	}

	private String getErrorMessage(int value, int compare) {
		if (max && value < compare) {
			return "The maximum sky light spawn level cannot be less than the minimum sky light spawn level, "
				+ " thus setting it to a value less than the minimum sky light spawn level will do nothing.";
		} else if (!max && value > compare) {
			return "The minimum sky light spawn level cannot be greater than the maximum sky light spawn level, "
				+ "thus setting it to a value greater than the maximum sky light spawn level will do nothing.";
		}

		return null;
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

	@Override
	protected String getPropertyName() {
		return (max ? "maximum" : "minimum") + " sky light spawn level";
	}

}

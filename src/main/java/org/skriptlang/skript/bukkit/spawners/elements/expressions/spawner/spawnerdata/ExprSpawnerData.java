package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner.spawnerdata;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
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

import java.util.StringJoiner;

@Name("Spawner Data")
@Description("""
	Returns the spawner data of a spawner. Since trial spawners use different data in its ominous and regular states, \
	you can specifically set the data of the ominous state using 'ominous trial spawner data'. Additionally, \
	you can set the data of both states using 'ominous and regular trial spawner data', which will apply the data to \
	both states of the trial spawner.
	""")
@Example("""
	set the spawner data of event-block to the mob spawner data:
		set the spawn count to 5
		add 2 to the maximum nearby entity cap
		remove 5 from the activation range
	""")
@Example("""
	set {_data} to spawner data of event-block
	add {_spawner entries::*} to spawner entries of {_data}
	set spawn range of {_data} to 12
	add 6 to the activation range of {_data}
	set the spawner data of event-block to {_data}
	""")
@Example("""
	set {_trial data} to the trial spawner data:
		set the activation range to 32
		set the spawn range to 8
		add {_entries::*} to the spawner entries
		set the base entity spawn count to 12

	set the trial spawner data of event-block to {_trial data} # regular data
	set the ominous trial spawner data of event-block to {_trial data} # ominous data
	set the ominous and regular trial spawner datas of event-block to {_trial data} # both states
	""")
@RequiredPlugins("Minecraft 1.21+ (for trial spawner data)")
public class ExprSpawnerData extends SimplePropertyExpression<Object, SkriptSpawnerData> {

	public static void register(SyntaxRegistry registry) {
		String property = "[:mob] spawner data[s]";
		if (SpawnerUtils.IS_RUNNING_1_21)
			property = "[trial:[:ominous [regular:and (regular|normal]] trial|:mob] spawner data[s]";

		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(ExprSpawnerData.class, SkriptSpawnerData.class,
			property, SpawnerUtils.spawnerPropertyType, false)
				.supplier(ExprSpawnerData::new)
				.build()
		);
	}

	private SpawnerDataType dataType;
	private boolean ominous, regular;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		dataType = SpawnerDataType.fromTags(parseResult.tags);
		ominous = parseResult.hasTag("ominous");
		regular = parseResult.hasTag("regular");
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
			if (data == null) {
				if (SpawnerUtils.isCreatureSpawner(object) || SpawnerUtils.isSpawnerMinecart(object)) {
					data = new SkriptMobSpawnerData();
				} else if (SpawnerUtils.isTrialSpawner(object)) {
					data = new SkriptTrialSpawnerData();
				}
			}

			if (data == null)
				continue;

			SpawnerUtils.applyData(data, object, dataType, ominous, regular);
		}
	}

	@Override
	public Class<? extends SkriptSpawnerData> getReturnType() {
		return dataType.getDataClass();
	}

	@Override
	protected String getPropertyName() {
		StringJoiner joiner = new StringJoiner(" ", "", "spawner data");
		if (ominous)
			joiner.add("ominous");
		joiner.add(dataType.toString());
		return joiner.toString();
	}

}

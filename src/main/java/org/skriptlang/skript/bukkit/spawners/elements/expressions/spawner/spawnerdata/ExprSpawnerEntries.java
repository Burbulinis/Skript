package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawner.spawnerdata;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptSpawnerData;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.ArrayList;
import java.util.List;

@Name("Spawner Entries")
@Description({
	"Every spawn attempt, the spawner will pick a random entry "
		+ "from the list of potential spawner entries and spawn it."
		+ "The spawner entity will be overwritten to the "
		+ "entity snapshot of the highest weighted spawner entry from the list of potential spawns.",
	"",
	"This expression gets the trial spawner configuration "
		+ "with the current state (i.e. ominous, normal) of the trial spawner block, if one is provided.",
	"",
	"Base spawners are trial spawner configurations, spawner minecarts and creature spawners."
})
@Examples({
	"set {_entry::*} to potential spawner spawns of target block",
	"add a spawner entry with entity snapshot of a zombie to potential spawner spawns of target block",
})
@Since("INSERT VERSION")
public class ExprSpawnerEntries extends PropertyExpression<SkriptSpawnerData, SpawnerEntry> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprSpawnerEntries.class, SpawnerEntry.class)
			.supplier(ExprSpawnerEntries::new)
			.priority(PropertyExpression.DEFAULT_PRIORITY)
			.addPatterns(
				"[the] spawner entr(y|ies) [of %spawnerdatas%]",
				"[%spawnerdatas%'[s]] spawner entr(y|ies)")
			.build()
		);
	}

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		//noinspection unchecked
		setExpr((Expression<? extends SkriptSpawnerData>) exprs[0]);
		return true;
	}

	@Override
	protected SpawnerEntry @Nullable [] get(Event event, SkriptSpawnerData[] source) {
		List<SpawnerEntry> entries = new ArrayList<>();

		for (SkriptSpawnerData data : source) {
			entries.addAll(data.getSpawnerEntries());
		}

		return entries.toArray(SpawnerEntry[]::new);
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.REMOVE_ALL)
			return null;

		return CollectionUtils.array(SpawnerEntry[].class);
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		List<SpawnerEntry> entries = new ArrayList<>();
		if (delta != null) {
			for (Object object : delta)
				entries.add((SpawnerEntry) object);
		}

		for (SkriptSpawnerData data : getExpr().getArray(event)) {
			switch (mode) {
				case SET -> data.setSpawnerEntries(entries);
				case ADD -> data.addSpawnerEntries(entries);
				case REMOVE -> data.removeSpawnerEntries(entries);
				case RESET, DELETE -> data.clearSpawnerEntries();
			}
		}
	}

	@Override
	public Class<? extends SpawnerEntry> getReturnType() {
		return SpawnerEntry.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "the spawner entries of " + getExpr().toString(event, debug);
	}

}

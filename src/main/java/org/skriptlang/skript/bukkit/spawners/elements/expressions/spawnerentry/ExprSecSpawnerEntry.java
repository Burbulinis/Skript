package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawnerentry;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.util.SectionUtils;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.events.SpawnRuleEvent;
import org.skriptlang.skript.bukkit.spawners.util.events.SpawnerEntryEvent;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.List;

@Name("Create Spawner Entry")
@Description({
	"Creates a spawner entry from the given entity snapshot. "
		+ "Spawner entries are used to modify what type of entity the spawner will spawn, "
		+ "with what equipment, rules, etc."
})
@Examples({
	"set {_entry} to a spawner entry using entity snapshot of a zombie:",
		"\tset the weight to 5",
		"\tset the spawn rule to a spawn rule:",
			"\t\tset the minimum block light spawn level to 10",
			"\t\tset the maximum block light spawn level to 15",
			"\t\tset the maximum sky light spawn level to 15",
	"add {_entry} to potential spawns of target block",
	"",
	"set {_entry} to a spawner entry with event-entity:",
		"\tset the weight to 10",
		"\tset the spawn rule to a spawn rule:",
			"\t\tset the minimum block light spawn level to 12",
			"\t\tset the maximum block light spawn level to 12",
			"\t\tset the maximum sky light spawn level to 5",
	"add {_entry} to potential spawns of target block"
})
@Since("INSERT VERSION")
public class ExprSecSpawnerEntry extends SectionExpression<SpawnerEntry> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprSecSpawnerEntry.class, SpawnerEntry.class)
			.supplier(ExprSecSpawnerEntry::new)
			.priority(SyntaxInfo.COMBINED)
			.addPattern("[a] spawner entry (of|using) %entitysnapshot%")
			.build()
		);
	}

	private Trigger trigger;
	private Expression<EntitySnapshot> snapshot;

	@Override
	public boolean init(
		Expression<?>[] exprs, int pattern, Kleenean delayed, ParseResult result, @Nullable SectionNode node,
		@Nullable List<TriggerItem> triggerItems
	) {
		//noinspection unchecked
		snapshot = (Expression<EntitySnapshot>) exprs[0];
		if (node != null) {
			trigger = SectionUtils.loadLinkedCode("spawner entry create", (beforeLoading, afterLoading) ->
				loadCode(node, "spawner entry create", beforeLoading, afterLoading, SpawnRuleEvent.class)
			);
			return trigger != null;
		}
		return true;
	}

	@Override
	protected SpawnerEntry @Nullable [] get(Event event) {
		EntitySnapshot entitySnapshot = snapshot.getSingle(event);
		if (entitySnapshot == null)
			return null;

		SpawnerEntry entry = new SpawnerEntry(entitySnapshot, 1, null);
		if (trigger != null) {
			SpawnerEntryEvent entryEvent = new SpawnerEntryEvent(entry);
			Variables.withLocalVariables(event, entryEvent, () ->
					TriggerItem.walk(trigger, entryEvent)
			);
		}

		return new SpawnerEntry[]{entry};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends SpawnerEntry> getReturnType() {
		return SpawnerEntry.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "spawner entry of " + snapshot.toString(event, debug);
	}

}

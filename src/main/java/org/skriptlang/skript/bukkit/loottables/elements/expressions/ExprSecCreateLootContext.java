package org.skriptlang.skript.bukkit.loottables.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.*;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Direction;
import ch.njol.skript.util.Getter;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.loot.LootContext;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.loottables.LootContextCreateEvent;
import org.skriptlang.skript.bukkit.loottables.LootContextWrapper;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Name("New Loot Context")
@Description("Create a loot context.")
@Examples({
	"set {_player} to player",
	"set {_context} to a loot context at player:",
		"\tset loot context luck value to 10",
		"\tset loot context killer to {_player}",
		"\tset loot context entity to last spawned pig",
	"give player loot items of loot table \"minecraft:entities/iron_golem\" with loot context {_context}"
})
@Since("INSERT VERSION")
public class ExprSecCreateLootContext extends SectionExpression<LootContext> {

	static {
		Skript.registerExpression(ExprSecCreateLootContext.class, LootContext.class, ExpressionType.SIMPLE,
			"[a] loot context %direction% %location%");
		EventValues.registerEventValue(LootContextCreateEvent.class, LootContext.class, new Getter<>() {
			@Override
			public @Nullable LootContext get(LootContextCreateEvent event) {
				return event.getContextWrapper().getContext();
			}
		}, EventValues.TIME_NOW);
	}

	private Trigger trigger;
	private Expression<Location> location;

	@Override
	public boolean init(Expression<?>[] exprs, int pattern, Kleenean isDelayed, ParseResult result, @Nullable SectionNode node, @Nullable List<TriggerItem> triggerItems) {
		if (node != null) {
			AtomicBoolean delayed = new AtomicBoolean(false);
			Runnable afterLoading = () -> delayed.set(!getParser().getHasDelayBefore().isFalse());
			//noinspection unchecked
			trigger = loadCode(node, "create loot context", afterLoading, LootContextCreateEvent.class);
			if (delayed.get()) {
				Skript.error("Delays cannot be used within a 'create loot context' section.");
				return false;
			}
		}
		//noinspection unchecked
		location = Direction.combine((Expression<Direction>) exprs[0], (Expression<Location>) exprs[1]);
		return true;
	}

	@Override
	protected LootContext @Nullable [] get(Event event) {
		Location loc = location.getSingle(event);
		if (loc == null)
			return new LootContext[0];

		LootContextWrapper wrapper = new LootContextWrapper(loc);
		if (trigger == null)
			return new LootContext[]{wrapper.getContext()};

		LootContextCreateEvent contextEvent = new LootContextCreateEvent(wrapper);

		Variables.setLocalVariables(contextEvent, Variables.copyLocalVariables(event));
		TriggerItem.walk(trigger, contextEvent);
		Variables.setLocalVariables(event, Variables.copyLocalVariables(contextEvent));
		Variables.removeLocals(contextEvent);

		return new LootContext[]{contextEvent.getContextWrapper().getContext()};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends LootContext> getReturnType() {
		return LootContext.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "a loot context at " + location.toString(event, debug);
	}

}

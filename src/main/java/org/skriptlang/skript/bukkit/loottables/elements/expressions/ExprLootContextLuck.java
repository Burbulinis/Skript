package org.skriptlang.skript.bukkit.loottables.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.bukkit.loot.LootContext;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.loottables.LootContextCreateEvent;
import org.skriptlang.skript.bukkit.loottables.LootContextWrapper;

@Name("Luck of Loot Context")
@Description("Returns the luck of a loot context as a float.")
@Examples({
	"set {_luck} to loot context luck value of {_context}",
	"",
	"set {_context} to a loot context at player:",
		"\tset loot context luck value to 10",
		"\tset loot context killer to player",
		"\tset loot context entity to last spawned pig"
})
@Since("INSERT VERSION")
public class ExprLootContextLuck extends SimplePropertyExpression<LootContext, Float> {

	static {
		registerDefault(ExprLootContextLuck.class, Float.class, "loot [context] luck [value|factor]", "lootcontexts");
	}

	@Override
	public @Nullable Float convert(LootContext context) {
		return context.getLuck();
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		if (!getParser().isCurrentEvent(LootContextCreateEvent.class))
			Skript.error("You cannot set the loot context luck of an existing loot context.");
		else if (mode == ChangeMode.SET || mode == ChangeMode.DELETE || mode == ChangeMode.RESET)
			return CollectionUtils.array(Float.class);
		return null;
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		if (!(event instanceof LootContextCreateEvent createEvent))
			return;

		LootContextWrapper wrapper = createEvent.getContextWrapper();

		if (mode == ChangeMode.SET)
			wrapper.setLuck((float) delta[0]);
		else
			wrapper.setLuck(0f);
	}

	@Override
	public Class<? extends Float> getReturnType() {
		return Float.class;
	}

	@Override
	protected String getPropertyName() {
		return "loot luck factor";
	}

}
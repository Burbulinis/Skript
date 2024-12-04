package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.loot.LootContext;
import org.jetbrains.annotations.Nullable;

@Name("Loot Context with Entity")
@Description("Returns the given loot context with the specified looted entity added to it.")
@Examples("the loot context at {_location} with the looted entity {_entity}")
@Since("INSERT VERSION")
public class ExprLootContextWithEntity extends PropertyExpression<LootContext, LootContext> {

	static {
		Skript.registerExpression(ExprLootContextWithEntity.class, LootContext.class, ExpressionType.PROPERTY,
				"%lootcontext% with [the] [looted] entity %entity%"
		);
	}

	private Expression<Entity> entity;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		setExpr((Expression<LootContext>) exprs[0]);
		entity = (Expression<Entity>) exprs[1];
		return true;
	}

	@Override
	protected LootContext[] get(Event event, LootContext[] source) {
		LootContext context = getExpr().getSingle(event);
		if (context == null)
			return new LootContext[0];

		LootContext.Builder builder = new LootContext.Builder(context.getLocation());

		Entity lootedEntity = entity.getSingle(event);
		if (lootedEntity != null)
			builder.lootedEntity(lootedEntity);

		if (context.getKiller() != null)
			builder.killer(context.getKiller());
		builder.luck(context.getLuck());

		return new LootContext[]{builder.build()};
	}

	@Override
	public Class<? extends LootContext> getReturnType() {
		return LootContext.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return getExpr().toString(event, debug) + " with entity " + entity.toString(event, debug);
	}
}

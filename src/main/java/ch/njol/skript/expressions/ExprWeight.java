package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.util.common.AnyWeighted;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprWeight extends SimplePropertyExpression<AnyWeighted, Number> {

	static {
		register(ExprWeight.class, Number.class, "weight[s]", "anyweighteds");
	}

	@Override
	public @Nullable Number convert(AnyWeighted weighted) {
		return weighted.weight();
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, ADD, REMOVE -> CollectionUtils.array(Number.class);
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		assert delta != null;

		Number deltaValue = (Number) delta[0];
		for (AnyWeighted weighted : getExpr().getArray(event)) {
			if (!weighted.supportsWeightChange())
				error("This object does not support weight modification.");

			Number newValue = switch (mode) {
				case SET -> deltaValue;
				case ADD -> weighted.weight().doubleValue() + deltaValue.doubleValue();
				case REMOVE -> weighted.weight().doubleValue() - deltaValue.doubleValue();
				default -> 0;
			};

			weighted.setWeight(newValue);
		}
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	protected String getPropertyName() {
		return "weight";
	}

}

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
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(Number.class);
		return null;
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		assert delta != null;
		Number weightValue = (Number) delta[0];
		for (AnyWeighted weighted : getExpr().getArray(event)) {
			if (!weighted.supportsWeightChange())
				error("This object does not support weight modification.");
			weighted.setWeight(weightValue);
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

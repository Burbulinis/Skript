package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawnerentry;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.spawners.util.SkriptSpawnerEntry;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Spawner Entry - Entity Snapshot")
@Description("The entity snapshot of the spawner entry. Entity snapshots determines what entity the spawner will spawn.")
@Examples({
	"set {_entry} to a spawner entry using entity snapshot of a pig",
	"add {_entry} to potential spawns of target block",
	"# the spawner will now spawn pigs"
})
@Since("INSERT VERSION")
public class ExprSpawnerEntrySnapshot extends SimplePropertyExpression<SkriptSpawnerEntry, EntitySnapshot> {

	public static void register(SyntaxRegistry registry) {
		registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(ExprSpawnerEntrySnapshot.class, EntitySnapshot.class,
			"spawner entry [entity] snapshot[s]", "spawnerentries", true)
			.supplier(ExprSpawnerEntrySnapshot::new)
			.build()
		);
	}

	@Override
	public @NotNull EntitySnapshot convert(SkriptSpawnerEntry entry) {
		return entry.getEntitySnapshot();
	}

	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(EntitySnapshot.class, EntityData.class);
		return null;
	}

	@Override
	public void change(Event event, Object @Nullable [] delta, ChangeMode mode) {
		assert delta != null;
		Object object = delta[0];

		EntitySnapshot snapshot = null;
		if (object instanceof EntitySnapshot entitySnapshot) {
			snapshot = entitySnapshot;
		} else if (object instanceof EntityData<?> entityData) {
			Entity entity = entityData.create();
			if (entity == null)
				return;

			snapshot = entity.createSnapshot();
			if (snapshot == null)
				return;
			entity.remove();
		}

		assert snapshot != null;

		for (SkriptSpawnerEntry entry : getExpr().getArray(event)) {
			entry.setEntitySnapshot(snapshot);
		}
	}

	@Override
	public Class<? extends EntitySnapshot> getReturnType() {
		return EntitySnapshot.class;
	}

	@Override
	protected String getPropertyName() {
		return "spawner entry snapshot";
	}

}

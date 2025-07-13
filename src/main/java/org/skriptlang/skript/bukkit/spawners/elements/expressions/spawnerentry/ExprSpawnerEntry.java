package org.skriptlang.skript.bukkit.spawners.elements.expressions.spawnerentry;

import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.EventValueExpression;
import org.bukkit.block.spawner.SpawnerEntry;
import org.skriptlang.skript.bukkit.spawners.SpawnerModule;
import org.skriptlang.skript.bukkit.spawners.elements.expressions.spawnrule.ExprSpawnRule;
import org.skriptlang.skript.bukkit.spawners.util.SkriptSpawnerEntry;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Spawner Entry")
@Description("The spawner entry used in the create spawner entry section.")
@Examples("the spawner entry")
@Since("INSERT VERSION")
public class ExprSpawnerEntry extends EventValueExpression<SkriptSpawnerEntry> {

   public static void register(SyntaxRegistry registry) {
	   registry.register(SyntaxRegistry.EXPRESSION, infoBuilder(ExprSpawnerEntry.class, SkriptSpawnerEntry.class, "[the] spawner entry")
		   .supplier(ExprSpawnerEntry::new)
		   .build()
	   );
   }

    public ExprSpawnerEntry() {
        super(SkriptSpawnerEntry.class);
    }

    @Override
    public String toString() {
        return "the spawner entry";
    }

}

package org.skriptlang.skript.bukkit.loottables;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.NamespacedUtils;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.converter.Converters;

import java.io.IOException;

public class LootTableModule {

	public static void load() throws IOException {
		Skript.getAddonInstance().loadClasses("org.skriptlang.skript.bukkit.loottables", "elements");

		// --- CLASSES --- //

		Classes.registerClass(new ClassInfo<>(LootTable.class, "loottable")
			.user("loot ?tables?")
			.name("Loot Table")
			.description("Loot tables represent what items should be in naturally generated containers, " +
				"what items should be dropped when killing a mob, or what items can be fished.",
				"You can find more information about this in https://minecraft.wiki/w/Loot_table")
			.since("INSERT VERSION")
			.parser(new Parser<>() {
				@Override
				public @Nullable LootTable parse(String s, ParseContext context) {	
					return Bukkit.getLootTable(NamespacedUtils.parseNamespacedKey(s));
				}

				@Override
				public String toString(LootTable o, int flags) {
					return "loot table '" + o.getKey() + '\'';
				}

				@Override
				public String toVariableNameString(LootTable o) {
					return "loot table:" + o.getKey();
				}
			})
		);

		Classes.registerClass(new ClassInfo<>(LootContext.class, "lootcontext")
			.user("loot ?contexts?")
			.name("Loot Context")
			.description(
				"Represents additional information a loot table can use to modify its generated loot.",
				"",
				"Some loot tables will require some values (i.e. looter, location, looted entity) " +
				"in a loot context when generating loot whereas others may not.",
				"For example, the loot table of a simple dungeon chest will only require a location, " +
				"whereas the loot table of a cow will require a looting player, looted entity, and location.",
				"You can find more information about this in https://minecraft.wiki/w/Loot_context"
			)
			.since("INSERT VERSION")
			.defaultExpression(new EventValueExpression<>(LootContext.class))
			.parser(new Parser<>() {
				@Override
				public boolean canParse(ParseContext context) {
					return false;
				}

				@Override
				public @Nullable LootContext parse(String s, ParseContext context) {
					return null;
				}

				@Override
				public String toString(LootContext context, int flags) {
					return "loot context at " + Classes.toString(context.getLocation()) +
						((context.getLootedEntity() != null) ? (" with entity " + Classes.toString(context.getLootedEntity())) : "") +
						((context.getKiller() != null) ? " with killer " + Classes.toString(context.getKiller()) : "") +
						((context.getLuck() != 0) ? " with luck " + context.getLuck() : "");
				}

				@Override
				public String toVariableNameString(LootContext context) {
					return "loot context:" + context.hashCode();
				}
			})
		);

		// --- CONVERTERS --- //

		// String - LootTable
		Converters.registerConverter(String.class, LootTable.class, key -> Bukkit.getLootTable(NamespacedUtils.parseNamespacedKey(key)));

		// --- SIMPLE EVENTS --- //

		Skript.registerEvent("Loot Generate", SimpleEvent.class, LootGenerateEvent.class, "loot generat(e|ing)")
			.description(
				"Called when a loot table of an inventory is generated in the world.",
				"For example, when opening a shipwreck chest."
			)
			.examples(
				"on loot generate:",
				"\tchance of 10%",
				"\tadd 64 diamonds to the loot",
				"\tsend \"You hit the jackpot at %event-location%!\""
			)
			.since("2.7")
			.requiredPlugins("MC 1.16+");

		// --- EVENT VALUES --- //

		// LootGenerateEvent
		EventValues.registerEventValue(LootGenerateEvent.class, Entity.class, new Getter<Entity, LootGenerateEvent>() {
			@Override
			@Nullable
			public Entity get(LootGenerateEvent event) {
				return event.getEntity();
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(LootGenerateEvent.class, Location.class, new Getter<>() {
			@Override
			public @NotNull Location get(LootGenerateEvent event) {
				return event.getLootContext().getLocation();
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(LootGenerateEvent.class, LootTable.class, new Getter<>() {
			@Override
			public @NotNull LootTable get(LootGenerateEvent event) {
				return event.getLootTable();
			}
		}, EventValues.TIME_NOW);
		EventValues.registerEventValue(LootGenerateEvent.class, LootContext.class, new Getter<>() {
			@Override
			public @NotNull LootContext get(LootGenerateEvent event) {
				return event.getLootContext();
			}
		}, EventValues.TIME_NOW);
	}

}
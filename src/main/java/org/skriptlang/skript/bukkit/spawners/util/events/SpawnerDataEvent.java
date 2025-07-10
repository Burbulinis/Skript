package org.skriptlang.skript.bukkit.spawners.util.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptSpawnerData;

public abstract class SpawnerDataEvent extends Event {

	public abstract SkriptSpawnerData getSpawnerData();

	@Override
	public @NotNull HandlerList getHandlers() {
		throw new UnsupportedOperationException();
	}

}

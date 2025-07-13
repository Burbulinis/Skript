package org.skriptlang.skript.bukkit.spawners.util.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.bukkit.spawners.util.SkriptSpawnerEntry;

public class SpawnerEntryEvent extends Event {

	private final SkriptSpawnerEntry entry;

	public SpawnerEntryEvent(SkriptSpawnerEntry entry) {
		this.entry = entry;
	}

	public SkriptSpawnerEntry getSpawnerEntry() {
		return entry;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		throw new UnsupportedOperationException();
	}

}

package org.skriptlang.skript.bukkit.spawners.util.events;

import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpawnerEntryEvent extends Event {

	private final SpawnerEntry entry;

	public SpawnerEntryEvent(SpawnerEntry entry) {
		this.entry = entry;
	}

	public SpawnerEntry getSpawnerEntry() {
		return entry;
	}

	@Override
	public HandlerList getHandlers() {
		throw new UnsupportedOperationException();
	}

}

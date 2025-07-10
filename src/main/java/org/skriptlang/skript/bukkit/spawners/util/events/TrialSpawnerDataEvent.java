package org.skriptlang.skript.bukkit.spawners.util.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptTrialSpawnerData;

public class TrialSpawnerDataEvent extends Event {

	private final SkriptTrialSpawnerData data;

	public TrialSpawnerDataEvent(SkriptTrialSpawnerData data) {
		this.data = data;
	}

	public SkriptTrialSpawnerData getTrialSpawnerData() {
		return data;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		throw new UnsupportedOperationException();
	}

}

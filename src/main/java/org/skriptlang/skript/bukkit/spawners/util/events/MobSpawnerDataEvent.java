package org.skriptlang.skript.bukkit.spawners.util.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptMobSpawnerData;

public class MobSpawnerDataEvent extends Event {

	private final SkriptMobSpawnerData data;

	public MobSpawnerDataEvent(SkriptMobSpawnerData data) {
		this.data = data;
	}

	public SkriptMobSpawnerData getMobSpawnerData() {
		return data;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		throw new UnsupportedOperationException();
	}

}

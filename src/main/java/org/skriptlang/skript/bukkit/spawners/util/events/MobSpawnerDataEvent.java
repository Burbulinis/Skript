package org.skriptlang.skript.bukkit.spawners.util.events;

import org.skriptlang.skript.bukkit.spawners.util.SpawnerDataType;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptMobSpawnerData;

/**
 * Event to allow retrieving the mob spawner data in the spawner data sections.
 */
public class MobSpawnerDataEvent extends SpawnerDataEvent {

	private final SkriptMobSpawnerData data;

	public MobSpawnerDataEvent(SkriptMobSpawnerData data) {
		super(data, SpawnerDataType.MOB);
		this.data = data;
	}

	@Override
	public SkriptMobSpawnerData getSpawnerData() {
		return data;
	}

}

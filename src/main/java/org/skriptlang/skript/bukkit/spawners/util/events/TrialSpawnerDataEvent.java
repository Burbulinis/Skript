package org.skriptlang.skript.bukkit.spawners.util.events;

import org.skriptlang.skript.bukkit.spawners.util.SpawnerDataType;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptTrialSpawnerData;

/**
 * Event to allow retrieving the trial spawner data in the spawner data sections.
 */
public class TrialSpawnerDataEvent extends SpawnerDataEvent {

	private final SkriptTrialSpawnerData data;

	public TrialSpawnerDataEvent(SkriptTrialSpawnerData data) {
		super(data, SpawnerDataType.TRIAL);
		this.data = data;
	}

	@Override
	public SkriptTrialSpawnerData getSpawnerData() {
		return data;
	}

}

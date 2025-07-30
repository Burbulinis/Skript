package org.skriptlang.skript.bukkit.spawners.util.events;

import org.skriptlang.skript.bukkit.spawners.util.SpawnerDataType;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptTrialSpawnerData;

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

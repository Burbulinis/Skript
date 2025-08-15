package org.skriptlang.skript.bukkit.spawners.util.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerDataType;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptSpawnerData;

/**
 * Event to allow retrieving the spawner datas in the spawner data sections.
 */
public class SpawnerDataEvent extends Event {

	private final SkriptSpawnerData data;
	private final SpawnerDataType type;

	public SpawnerDataEvent(SkriptSpawnerData data, SpawnerDataType type) {
		this.data = data;
		this.type = type;
	}

	/**
	 * Gets the SkriptSpawnerData associated with this event.
	 * @return the SkriptSpawnerData for this event.
	 */
	public SkriptSpawnerData getSpawnerData() {
		return data;
	}

	/**
	 * Gets the type of spawner data this event is associated with.
	 * @return the SpawnerDataType for this event.
	 */
	public SpawnerDataType getType() {
		return type;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		throw new UnsupportedOperationException();
	}

}

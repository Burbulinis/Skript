package org.skriptlang.skript.bukkit.spawners.util;

/**
 * Enum representing the type of spawner.
 */
public enum SpawnerType {

	MINECART,
	CREATURE,
	TRIAL;

	public boolean isMinecart() {
		return this == MINECART;
	}

	public boolean isCreature() {
		return this == CREATURE;
	}

	public boolean isTrial() {
		return this == TRIAL;
	}

}

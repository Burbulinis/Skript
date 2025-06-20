package org.skriptlang.skript.bukkit.spawners.util;

public enum SpawnerType {

	MINECART,
	CREATURE;

	boolean isMinecart() {
		return this == MINECART;
	}

	boolean isCreature() {
		return this == CREATURE;
	}

}

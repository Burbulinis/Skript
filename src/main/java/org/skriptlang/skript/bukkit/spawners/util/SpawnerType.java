package org.skriptlang.skript.bukkit.spawners.util;

public enum SpawnerType {

	MINECART,
	CREATURE,
	TRIAL;

	boolean isMinecart() {
		return this == MINECART;
	}

	boolean isCreature() {
		return this == CREATURE;
	}

	boolean isTrial() {
		return this == TRIAL;
	}

}

package org.skriptlang.skript.bukkit.spawners.util;

import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptMobSpawnerData;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptSpawnerData;
import org.skriptlang.skript.bukkit.spawners.util.spawnerdata.SkriptTrialSpawnerData;

import java.util.List;
import java.util.Locale;

public enum SpawnerDataType {

	MOB(SkriptMobSpawnerData.class),
	TRIAL(SkriptTrialSpawnerData.class),
	ANY(SkriptSpawnerData.class);

	public static SpawnerDataType fromTags(List<String> tags) {
		if (tags.contains("trial")) {
			return TRIAL;
		} else if (tags.contains("mob")) {
			return MOB;
		}
		return ANY;
	}

	private final Class<? extends SkriptSpawnerData> dataClass;

	SpawnerDataType(Class<? extends SkriptSpawnerData> dataClass) {
		this.dataClass = dataClass;
	}

	public Class<? extends SkriptSpawnerData> getDataClass() {
		return dataClass;
	}

	public boolean isMob() {
		return this == MOB;
	}

	public boolean isTrial() {
		return this == TRIAL;
	}

	public boolean isAny() {
		return this == ANY;
	}

	@Override
	public String toString() {
		if (isAny())
			return "";
		return name().toLowerCase(Locale.ENGLISH);
	}

}

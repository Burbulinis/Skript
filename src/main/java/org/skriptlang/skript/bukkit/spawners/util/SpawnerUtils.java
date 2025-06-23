package org.skriptlang.skript.bukkit.spawners.util;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.TrialSpawner;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.spawner.BaseSpawner;
import org.bukkit.spawner.Spawner;
import org.bukkit.spawner.TrialSpawnerConfiguration;
import org.skriptlang.skript.bukkit.spawners.util.legacy.LegacySkriptMobSpawner;

/**
 * Utility class for spawners.
 */
public class SpawnerUtils {

	public static boolean IS_RUNNING_1_20_3 = Skript.isRunningMinecraft(1, 20, 3);
	public static boolean IS_RUNNING_1_21 = Skript.isRunningMinecraft(1, 21);
	public static boolean IS_RUNNING_1_21_4 = Skript.isRunningMinecraft(1, 20, 4);

	public static final int DEFAULT_ACTIVATION_RANGE = 16;
	public static final int DEFAULT_MAX_NEARBY_ENTITIES = 6;
	public static final int DEFAULT_SPAWN_RANGE = 4;
	public static final int DEFAULT_SPAWN_COUNT = 4;

	public static final Timespan DEFAULT_MAX_SPAWN_DELAY = new Timespan(TimePeriod.TICK, 800);
	public static final Timespan DEFAULT_MIN_SPAWN_DELAY = new Timespan(TimePeriod.TICK, 200);;
	public static final Timespan DEFAULT_COOLDOWN_LENGTH = new Timespan(TimePeriod.TICK, 36_000);

	public static final int DEFAULT_TRIAL_ACTIVATION_RANGE = 14;
	public static final int DEFAULT_BASE_MOB_AMOUNT = 6;
	public static final int DEFAULT_BASE_PER_PLAYER_INCREMENT = 2;
	public static final int DEFAULT_CONCURRENT_MOB_AMOUNT = 2;
	public static final int DEFAULT_CONCURRENT_PER_PLAYER_INCREMENT = 1;

	/**
	 * Applies the data from the spawner to the abstract data.
	 * @param spawner the spawner to apply the data from
	 * @param data the abstract data to apply the data to
	 */
	public static void applySpawnerDataToSpawnerData(BaseSpawner spawner, SkriptSpawnerData data) {
		data.setActivationRange(spawner.getRequiredPlayerRange());
		data.setSpawnRange(spawner.getSpawnRange());

		if (!spawner.getPotentialSpawns().isEmpty())
			data.setSpawnerEntries(spawner.getPotentialSpawns());
		else if (spawner.getSpawnedEntity() != null)
			data.setSpawnerEntitySnapshot(spawner.getSpawnedEntity());
		else
			data.setSpawnerType(EntityUtils.toSkriptEntityData(spawner.getSpawnedType()));
	}

	/**
	 * Applies the abstract data to the spawner.
	 * @param data the abstract data to apply
	 * @param spawner the spawner to apply the data to
	 */
	public static void applySpawnerDataToSpawner(SkriptSpawnerData data, BaseSpawner spawner) {
		spawner.setRequiredPlayerRange(data.getActivationRange());
		spawner.setSpawnRange(data.getSpawnRange());

		if (!data.getSpawnerEntries().isEmpty())
			spawner.setPotentialSpawns(data.getSpawnerEntries());
		else if (data.getSpawnerEntitySnapshot() != null)
			spawner.setSpawnedEntity(data.getSpawnerEntitySnapshot());
		else
			spawner.setSpawnedType(EntityUtils.toBukkitEntityType(data.getSpawnerType()));
	}

	/**
	 * Returns the trial spawner configuration for the given trial spawner.
	 * @param trialSpawner the trial spawner
	 * @param ominous whether to get the ominous configuration
	 * @return the trial spawner configuration
	 */
	public static TrialSpawnerConfiguration getTrialSpawnerConfiguration(TrialSpawner trialSpawner, boolean ominous) {
		if (ominous)
			return trialSpawner.getOminousConfiguration();
		return trialSpawner.getNormalConfiguration();
	}

	/**
	 * Returns the object as a {@link SkriptMobSpawner}, or null if the object is not a spawner.
	 * @param object the object
	 * @return the object as a {@link SkriptMobSpawner}
	 */
	public static SkriptMobSpawner getAsSkriptSpawner(Object object) {
		Spawner spawner = null;
		SpawnerType type = null;

		if (object instanceof Block block && block.getState() instanceof Spawner spawner1) {
			spawner = spawner1;
			type = SpawnerType.CREATURE;
		} else if (object instanceof SpawnerMinecart spawner1) {
			spawner = spawner1;
			type = SpawnerType.MINECART;
		}

		if (spawner == null)
			return null;

		return new SkriptMobSpawner(spawner, type);
	}

	/**
	 * Returns the object as a {@link SkriptTrialSpawner}, OR null if the object is not a trial spawner.
	 * @param object the object
	 * @return the object as a {@link SkriptTrialSpawner}
	 */
	public static SkriptTrialSpawner getAsSkriptTrialSpawner(Object object) {
		if (object instanceof Block block && block.getState() instanceof TrialSpawner trialSpawner)
			return new SkriptTrialSpawner(trialSpawner);
		return null;
	}

	/**
	 * Ensures that the current Minecraft version supports spawner entries by throwing an exception if not.
	 * This is only supported for Minecraft 1.20.3 and later.
	 */
	public static void ensureSupportsSpawnerEntries() {
		if (!SpawnerUtils.IS_RUNNING_1_20_3) {
			throw new UnsupportedOperationException(
				"Spawner entries are only supported for Minecraft 1.20.3 and later"
			);
		}
	}

	/**
	 * Ensures that the current Minecraft version supports entity snapshots by throwing an exception if not.
	 * This is only supported for Minecraft 1.20.3 and later.
	 */
	public static void ensureSupportsEntitySnapshots() {
		if (!SpawnerUtils.IS_RUNNING_1_20_3) {
			throw new UnsupportedOperationException(
				"Entity snapshots are only supported for Minecraft 1.20.3 and later"
			);
		}
	}

	//<editor-fold desc="Legacy spawner handling" defaultstate="collapsed">
	/**
	 * Returns the object as a {@link LegacySkriptMobSpawner}, or null if the object is not a legacy spawner.
	 * @param object the object
	 * @return the object as a {@link LegacySkriptMobSpawner}
	 */
	public static LegacySkriptMobSpawner getAsLegacySkriptSpawner(Object object) {
		if (object instanceof Block block && block.getState() instanceof CreatureSpawner spawner)
			return new LegacySkriptMobSpawner(spawner);
		return null;
	}
	//</editor-fold>

	// todo: discard everything below from here?

	/**
	 * Returns whether the object is an instance of {@link BaseSpawner}. Base spawners are creature spawners,
	 * spawner minecarts and trial spawner configurations. Note that this returns true for {@link TrialSpawnerConfig}.
	 * @param object the object
	 * @return whether the object is a base spawner
	 */
	public static boolean isBaseSpawner(Object object) {
		if (object instanceof BaseSpawner) {
			return true;
		} else if (object instanceof Block block) {
			return block.getState() instanceof BaseSpawner;
		}
		return object instanceof TrialSpawnerConfig;
	}

	/**
	 * Returns whether the object is an instance of Spawner. Spawners are creature spawners and spawner minecarts.
	 * @param object the object
	 * @return whether the object is a Spawner
	 */
	public static boolean isSpawner(Object object) {
		if (object instanceof Block block)
			return block.getState() instanceof Spawner;
		return object instanceof SpawnerMinecart;
	}

	/**
	 * Returns whether the object is an instance of {@link TrialSpawner}. This also returns true for {@link TrialSpawnerConfig}.
	 * @param object the object
	 * @return whether the object is a TrialSpawner
	 */
	public static boolean isTrialSpawner(Object object) {
		if (object instanceof TrialSpawnerConfig) {
			return true;
		} else if (object instanceof Block block) {
			return block.getState() instanceof TrialSpawner;
		}
		return object instanceof TrialSpawner;
	}

	/**
	 * Returns the object as a {@link BaseSpawner}.
	 * @param object the object
	 * @return the object as a base spawner
	 * @see #isBaseSpawner(Object)
	 */
	public static BaseSpawner getAsBaseSpawner(Object object) {
		if (object instanceof Block block) {
			return (BaseSpawner) block.getState();
		} else if (object instanceof SpawnerMinecart spawner) {
			return spawner;
		} else if (object instanceof TrialSpawnerConfig config) {
			return config.config();
		}
		return null;
	}

	/**
	 * Returns the current trial spawner configuration of the spawner. I.e. ominous or normal.
	 * @param spawner the spawner
	 * @return the current trial spawner configuration
	 */
	public static TrialSpawnerConfig getCurrentTrialConfig(TrialSpawner spawner) {
		if (spawner.isOminous())
			return new TrialSpawnerConfig(spawner.getOminousConfiguration(), spawner, true);
		return new TrialSpawnerConfig(spawner.getNormalConfiguration(), spawner, false);
	}

	/**
	 * Updates the state of the spawner. This is used for {@link CreatureSpawner} and {@link TrialSpawner}.
	 * @param state the state
	 */
	public static void updateState(Object state) {
		if (state instanceof CreatureSpawner spawner) {
			spawner.update(true, false);
		} else if (state instanceof TrialSpawnerConfig config) {
			config.state().update(true, false);
		} else if (state instanceof TrialSpawner spawner) {
			spawner.update(true, false);
		}
	}

}

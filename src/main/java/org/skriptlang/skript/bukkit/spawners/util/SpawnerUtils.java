package org.skriptlang.skript.bukkit.spawners.util;

import ch.njol.skript.Skript;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.TrialSpawner;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.spawner.TrialSpawnerConfiguration;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Utility class for spawners.
 */
public class SpawnerUtils {

	public static boolean IS_RUNNING_1_21 = Skript.isRunningMinecraft(1, 21);
	public static boolean IS_RUNNING_1_21_4 = Skript.isRunningMinecraft(1, 21, 4);

	public static final int DEFAULT_ACTIVATION_RANGE = 16;
	public static final int DEFAULT_MAX_NEARBY_ENTITIES = 6;
	public static final int DEFAULT_SPAWN_RANGE = 4;
	public static final int DEFAULT_SPAWN_COUNT = 4;

	public static final Timespan DEFAULT_MAX_SPAWN_DELAY = new Timespan(TimePeriod.TICK, 800);
	public static final Timespan DEFAULT_MIN_SPAWN_DELAY = new Timespan(TimePeriod.TICK, 200);
	public static final Timespan DEFAULT_COOLDOWN_LENGTH = new Timespan(TimePeriod.TICK, 36_000);
	public static final Timespan DEFAULT_TRIAL_SPAWN_DELAY = new Timespan(TimePeriod.TICK, 40);

	public static final int DEFAULT_TRIAL_ACTIVATION_RANGE = 14;
	public static final int DEFAULT_BASE_MOB_AMOUNT = 6;
	public static final int DEFAULT_BASE_PER_PLAYER_INCREMENT = 2;
	public static final int DEFAULT_CONCURRENT_MOB_AMOUNT = 2;
	public static final int DEFAULT_CONCURRENT_PER_PLAYER_INCREMENT = 1;

	public static String spawnerPropertyType;

	static {
		String type = "blocks";
		if (IS_RUNNING_1_21)
			type += "/entities";
		spawnerPropertyType = type;
	}

	/**
	 * Returns the trial spawner configuration for the given trial spawner.
	 *
	 * @param trialSpawner the trial spawner to retrieve the configuration for
	 * @param ominous whether to retrieve the ominous configuration
	 * @return The trial spawner configuration
	 */
	public static TrialSpawnerConfiguration getTrialSpawnerConfiguration(TrialSpawner trialSpawner, boolean ominous) {
		if (ominous)
			return trialSpawner.getOminousConfiguration();
		return trialSpawner.getNormalConfiguration();
	}

	/**
	 * Returns the trial spawner configuration for the given trial spawner.
	 * Automatically determines whether to retrieve the ominous configuration.
	 *
	 * @param trialSpawner the trial spawner to retrieve the configuration for
	 * @return the trial spawner configuration
	 */
	public static TrialSpawnerConfiguration getTrialSpawnerConfiguration(TrialSpawner trialSpawner) {
		return getTrialSpawnerConfiguration(trialSpawner, trialSpawner.isOminous());
	}

	/**
	 * Checks if the given object is a creature spawner.
	 *
	 * @param object the object to check.
	 * @return true if the object is a creature spawner, false otherwise
	 * @see	#getCreatureSpawner(Object)
	 */
	public static boolean isCreatureSpawner(Object object) {
		if (object instanceof Block block)
			return block.getState() instanceof CreatureSpawner;
		return object instanceof CreatureSpawner;
	}

	/**
	 * Retrieves the creature spawner from the given object.
	 *
	 * @param object The object to retrieve the creature spawner from.
	 * @return the creature spawner
	 * @see	#isCreatureSpawner(Object)
	 */
	public static CreatureSpawner getCreatureSpawner(Object object) {
		if (object instanceof Block block)
			return (CreatureSpawner) block.getState();
		return (CreatureSpawner) object;
	}

	/**
	 * Checks if the given object is a spawner minecart.
	 *
	 * @param object the object to check
	 * @return true if the object is a spawner minecart, false otherwise
	 * @see #getSpawnerMinecart(Object)
	 */
	public static boolean isSpawnerMinecart(Object object) {
		return IS_RUNNING_1_21 && object instanceof SpawnerMinecart;
	}

	/**
	 * Retrieves the spawner minecart from the given object.
	 *
	 * @param object the object to retrieve the spawner minecart from.
	 * @return the spawner minecart
	 * @see #isSpawnerMinecart(Object)
	 */
	public static @UnknownNullability SpawnerMinecart getSpawnerMinecart(Object object) {
		if (!IS_RUNNING_1_21)
			return null;
		return (SpawnerMinecart) object;
	}

	/**
	 * Checks if the given object is a trial spawner.
	 *
	 * @param object the object to check
	 * @return true if the object is a trial spawner, false otherwise
	 * @see #getTrialSpawner(Object)
	 */
	public static boolean isTrialSpawner(Object object) {
		if (!IS_RUNNING_1_21)
			return false;
		if (object instanceof Block block)
			return block.getState() instanceof TrialSpawner;
		return object instanceof TrialSpawner;
	}


	/**
	 * Retrieves the trial spawner from the given object.
	 *
	 * @param object the object to retrieve the trial spawner from.
	 * @return the trial spawner
	 * @see #isTrialSpawner(Object)
	 */
	public static @UnknownNullability TrialSpawner getTrialSpawner(Object object) {
		if (!IS_RUNNING_1_21)
			return null;
		if (object instanceof Block block)
			return (TrialSpawner) block.getState();
		return (TrialSpawner) object;
	}

}

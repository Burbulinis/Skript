package org.skriptlang.skript.bukkit.spawners.util.spawnerdata;

import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import ch.njol.yggdrasil.YggdrasilSerializable;
import com.google.common.base.Preconditions;
import org.bukkit.block.TrialSpawner;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the data of a {@link TrialSpawner} and its configuration.
 *
 * @see SkriptMobSpawnerData
 * @see SkriptSpawnerData
 */
@SuppressWarnings("UnstableApiUsage")
public class SkriptTrialSpawnerData extends SkriptSpawnerData implements YggdrasilSerializable {

	private int activationRange = SpawnerUtils.DEFAULT_TRIAL_ACTIVATION_RANGE;
	private int baseMobAmount = SpawnerUtils.DEFAULT_BASE_MOB_AMOUNT;
	private int baseMobAmountIncrement = SpawnerUtils.DEFAULT_BASE_PER_PLAYER_INCREMENT;
	private int concurrentMobAmount = SpawnerUtils.DEFAULT_CONCURRENT_MOB_AMOUNT;
	private int concurrentMobAmountIncrement = SpawnerUtils.DEFAULT_CONCURRENT_PER_PLAYER_INCREMENT;

	private Timespan spawnDelay = SpawnerUtils.DEFAULT_TRIAL_SPAWN_DELAY;
	private @NotNull Map<LootTable, Integer> rewardEntries = new HashMap<>();

	private final boolean ominous;

	/**
	 * Creates a new SkriptTrialSpawnerData instance with default values
	 * @param ominous whether the data should be ominous
	 */
	public SkriptTrialSpawnerData(boolean ominous) {
		this.ominous = ominous;
	}

	/**
	 * Creates a new {@code SkriptTrialSpawnerData} instance from the given Bukkit {@link TrialSpawner}.
	 * <p>
	 * This is by default not ominous, meaning it will create a normal trial spawner data.
	 * @param trialSpawner the Bukkit trial spawner to convert
	 * @return a new {@code SkriptTrialSpawnerData} containing the data from the given {@link TrialSpawner}
	 */
	public static SkriptTrialSpawnerData fromTrialSpawner(@NotNull TrialSpawner trialSpawner) {
		return fromTrialSpawner(trialSpawner, false);
	}

	/**
	 * Creates a new {@code SkriptTrialSpawnerData} instance from the given Bukkit {@link TrialSpawner}.
	 * @param trialSpawner the Bukkit trial spawner to convert
	 * @param ominous whether the trial spawner is ominous
	 * @return a new {@code SkriptTrialSpawnerData} instance containing the data from the Bukkit trial spawner
	 */
	public static SkriptTrialSpawnerData fromTrialSpawner(@NotNull TrialSpawner trialSpawner, boolean ominous) {
		SkriptTrialSpawnerData data = new SkriptTrialSpawnerData(ominous);

		var config = SpawnerUtils.getTrialSpawnerConfiguration(trialSpawner, ominous);
		SkriptSpawnerData.applyToSpawnerData(config, data);
		data.setMaxSpawnDelay(new Timespan(TimePeriod.TICK, config.getDelay()));
		data.setRewardEntries(config.getPossibleRewards());

		data.setBaseMobAmount((int) config.getBaseSpawnsBeforeCooldown());
		data.setBaseMobAmountIncrement((int) config.getAdditionalSpawnsBeforeCooldown());
		data.setConcurrentMobAmount((int) config.getBaseSimultaneousEntities());
		data.setConcurrentMobAmountIncrement((int) config.getAdditionalSimultaneousEntities());

		return data;
	}

	/**
	 * Applies this SkriptTrialSpawnerData to the given Bukkit trial spawners.
	 * @param trialSpawners the array of Bukkit trial spawners to apply the data to
	 */
	public void applyDataToTrialSpawners(@NotNull TrialSpawner[] trialSpawners) {
		Preconditions.checkNotNull(trialSpawners, "trialSpawners cannot be null");
		for (TrialSpawner trialSpawner : trialSpawners) {
			applyDataToTrialSpawner(trialSpawner);
		}
	}

	/**
	 * Applies this SkriptTrialSpawnerData to the given Bukkit trial spawner.
	 * @param trialSpawner the Bukkit trial spawner to apply the data to
	 */
	public void applyDataToTrialSpawner(@NotNull TrialSpawner trialSpawner) {
		Preconditions.checkNotNull(trialSpawner, "trialSpawner cannot be null");

		var config = SpawnerUtils.getTrialSpawnerConfiguration(trialSpawner, ominous);
		super.applyToSpawner(config);

		config.setPossibleRewards(rewardEntries);

		config.setBaseSpawnsBeforeCooldown(getBaseMobAmount());
		config.setAdditionalSpawnsBeforeCooldown(getBaseMobAmountIncrement());
		config.setBaseSimultaneousEntities(getConcurrentMobAmount());
		config.setAdditionalSimultaneousEntities(getConcurrentMobAmountIncrement());
		config.setDelay(Math.clamp(spawnDelay.getAs(TimePeriod.TICK), 0, Integer.MAX_VALUE));

		trialSpawner.update(true, false);
	}

	/**
	 * Returns whether this trial spawner data is for an ominous trial spawner.
	 * @return true if this is an ominous trial spawner data, false otherwise
	 */
	public boolean isOminous() {
		return ominous;
	}

	@Override
	public int getActivationRange() {
		return activationRange;
	}

	@Override
	public void setActivationRange(int activationRange) {
		this.activationRange = activationRange;
	}

	/**
	 * {@inheritDoc}
	 * <br>
	 * <br>
	 * For trial spawners, the minimum and maximum spawn delays are always identical. This results in a fixed delay,
	 * rather than a random range.
	 * <p>
	 * The default value for trial spawners is 2 seconds (40 ticks).
	 */
	@Override
	public @NotNull Timespan getMaxSpawnDelay() {
		return spawnDelay;
	}

	/**
	 * {@inheritDoc}
	 * <br>
	 * <br>
	 * For trial spawners, the minimum and maximum spawn delays are always identical. This results in a fixed delay,
	 * rather than a random range.
	 * <p>
	 * The default value for trial spawners is 2 seconds (40 ticks).
	 */
	@Override
	public void setMaxSpawnDelay(@NotNull Timespan maxSpawnDelay) {
		this.spawnDelay = maxSpawnDelay;
	}

	/**
	 * {@inheritDoc}
	 * <br>
	 * <br>
	 * For trial spawners, the minimum and maximum spawn delays are always identical. This results in a fixed delay,
	 * rather than a random range.
	 * <p>
	 * The default value for trial spawners is 2 seconds (40 ticks).
	 */
	@Override
	public @NotNull Timespan getMinSpawnDelay() {
		return spawnDelay;
	}

	/**
	 * {@inheritDoc}
	 * <br>
	 * <br>
	 * For trial spawners, the minimum and maximum spawn delays are always identical. This results in a fixed delay,
	 * rather than a random range.
	 * <p>
	 * The default value for trial spawners is 2 seconds (40 ticks).
	 */
	@Override
	public void setMinSpawnDelay(@NotNull Timespan minSpawnDelay) {
		this.spawnDelay = minSpawnDelay;
	}

	/**
	 * Returns the reward entries for this trial spawner data.
	 * @return a map of loot tables and their corresponding weights
	 */
	public @NotNull Map<LootTable, Integer> getRewardEntries() {
		return Map.copyOf(rewardEntries);
	}

	/**
	 * Returns the weight of the specified loot table in this trial spawner data.
	 * @param lootTable the loot table to get the weight for
	 * @return the weight of the loot table, or null if it does not exist in the map
	 */
	public Integer getRewardWeight(@NotNull LootTable lootTable) {
		Preconditions.checkNotNull(lootTable, "lootTable cannot be null");
		return rewardEntries.get(lootTable);
	}

	/**
	 * Sets the reward entries for this trial spawner data.
	 * @param rewardEntries a map of loot tables and their corresponding weights
	 */
	public void setRewardEntries(@NotNull Map<LootTable, Integer> rewardEntries) {
		Preconditions.checkNotNull(rewardEntries, "rewardEntries cannot be null");
		this.rewardEntries = new HashMap<>(rewardEntries);
	}

	/**
	 * Adds a reward entry to the map of reward entries.
	 * @param lootTable the loot table
	 * @param weight the weight of the loot table
	 */
	public void setRewardEntry(@NotNull LootTable lootTable, int weight) {
		Preconditions.checkNotNull(lootTable, "lootTable cannot be null");
		this.rewardEntries.put(lootTable, weight);
	}

	/**
	 * Removes a reward entry from the map of reward entries.
	 * @param lootTable the loot table to remove
	 */
	public void removeRewardEntry(@NotNull LootTable lootTable) {
		Preconditions.checkNotNull(lootTable, "lootTable cannot be null");
		this.rewardEntries.remove(lootTable);
	}

	/**
	 * Clears all reward entries from this trial spawner data.
	 */
	public void clearRewardEntries() {
		this.rewardEntries.clear();
	}

	/**
	 * Returns the total number of mobs this spawner will spawn for a single player before going into cooldown.
	 * <p>
	 * The formula for calculating the total mob amount, taking into account multiple players, is:
	 * <pre><code>
	 * totalMobAmount = baseMobAmount + (baseMobAmountIncrement * (numberOfPlayers - 1))
	 * </code></pre>
	 * where {@code numberOfPlayers} is the number of players within range of the spawner.
	 * <p>
	 * The default base mob amount is {@code 6}.
	 *
	 * @return the base mob amount
	 * @see #getBaseMobAmountIncrement()
	 */
	public int getBaseMobAmount() {
		return baseMobAmount;
	}

	/**
	 * Sets the base number of mobs this spawner will spawn for a single player before going into cooldown.
	 * @param mobAmount the base mob amount to set
	 * @see #getBaseMobAmount()
	 * @see #getBaseMobAmountIncrement()
	 */
	public void setBaseMobAmount(int mobAmount) {
		baseMobAmount = mobAmount;
	}

	/**
	 * Returns how many mobs this spawner will add to the total mob spawn amount for each additional player.
	 * <p>
	 * The formula for calculating the total mob amount, taking into account multiple players, is:
	 * <pre><code>
	 * totalMobAmount = baseMobAmount + (baseMobAmountIncrement * (numberOfPlayers - 1))
	 * </code></pre>
	 * where {@code numberOfPlayers} is the number of players within range of the spawner.
	 * <p>
	 * The default value is {@code 2}.
	 *
	 * @return the number of additional mobs spawned per extra player
	 */
	public int getBaseMobAmountIncrement() {
		return baseMobAmountIncrement;
	}

	/**
	 * Sets how many mobs this spawner will add to the total mob spawn amount for each additional player.
	 * @param incrementPerPlayer the number of additional mobs spawned per extra player
	 * @see #getBaseMobAmount()
	 * @see #getBaseMobAmountIncrement()
	 */
	public void setBaseMobAmountIncrement(int incrementPerPlayer) {
		baseMobAmountIncrement = incrementPerPlayer;
	}

	/**
	 * Returns the maximum amount of mobs this spawner allows to exist concurrently for a single player.
	 * <p>
	 * The formula for calculating the total concurrent mob amount, taking into account multiple players, is:
	 * <pre><code>
	 *     totalConcurrentMobAmount = concurrentMobAmount + (concurrentMobAmountIncrement * (numberOfPlayers - 1))
	 * </code></pre>
	 * where {@code numberOfPlayers} is the number of players within range of the spawner.
	 * <p>
	 * The default value is {@code 6}.
	 * @return the maximum amount of mobs that can exist concurrently for a single player
	 * @see #getConcurrentMobAmountIncrement()
	 */
	public int getConcurrentMobAmount() {
		return concurrentMobAmount;
	}

	/**
	 * Sets the maximum amount of mobs this spawner allows to exist concurrently for a single player.
	 * @param mobAmount the maximum amount of mobs that can exist concurrently for a single player
	 * @see #getConcurrentMobAmount()
	 * @see #getConcurrentMobAmountIncrement()
	 */
	public void setConcurrentMobAmount(int mobAmount) {
		concurrentMobAmount = mobAmount;
	}

	/**
	 * Returns how many mobs this spawner will add to the concurrent mob spawn amount for each additional player.
	 * <p>
	 * The formula for calculating the total concurrent mob amount, taking into account multiple players, is:
	 * <pre><code>
	 *     totalConcurrentMobAmount = concurrentMobAmount + (concurrentMobAmountIncrement * (numberOfPlayers - 1))
	 * </code></pre>
	 * where {@code numberOfPlayers} is the number of players within range of the spawner.
	 * <p>
	 * The default value is {@code 2}.
	 * @return the number of additional mobs spawned concurrently per extra player
	 * @see #getConcurrentMobAmount()
	 */
	public int getConcurrentMobAmountIncrement() {
		return concurrentMobAmountIncrement;
	}

	/**
	 * Sets how many mobs this spawner will add to the concurrent mob spawn amount for each additional player.
	 * @param incrementPerPlayer the number of additional mobs spawned concurrently per extra player
	 * @see #getConcurrentMobAmount()
	 * @see #getBaseMobAmountIncrement()
	 */
	public void setConcurrentMobAmountIncrement(int incrementPerPlayer) {
		concurrentMobAmountIncrement = incrementPerPlayer;
	}

}
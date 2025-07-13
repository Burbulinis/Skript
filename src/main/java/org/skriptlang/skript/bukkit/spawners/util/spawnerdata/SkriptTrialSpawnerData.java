package org.skriptlang.skript.bukkit.spawners.util.spawnerdata;

import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import ch.njol.yggdrasil.YggdrasilSerializable;
import com.google.common.base.Preconditions;
import org.bukkit.block.TrialSpawner;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.bukkit.spawners.util.SpawnerUtils;
import org.skriptlang.skript.bukkit.spawners.util.TrialSpawnerRewardEntry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents the data of a {@link TrialSpawner} and its configuration.
 *
 * @see SkriptMobSpawnerData
 * @see SkriptSpawnerData
 */
public class SkriptTrialSpawnerData extends SkriptSpawnerData implements YggdrasilSerializable {

	private int activationRange = SpawnerUtils.DEFAULT_TRIAL_ACTIVATION_RANGE;
	private int baseMobAmount = SpawnerUtils.DEFAULT_BASE_MOB_AMOUNT;
	private int baseMobAmountIncrement = SpawnerUtils.DEFAULT_BASE_PER_PLAYER_INCREMENT;
	private int concurrentMobAmount = SpawnerUtils.DEFAULT_CONCURRENT_MOB_AMOUNT;
	private int concurrentMobAmountIncrement = SpawnerUtils.DEFAULT_CONCURRENT_PER_PLAYER_INCREMENT;

	private Timespan spawnDelay = SpawnerUtils.DEFAULT_TRIAL_SPAWN_DELAY;
	private @NotNull Set<TrialSpawnerRewardEntry> rewardEntries = new HashSet<>();

	private final boolean ominous;

	/**
	 * Creates a new SkriptTrialSpawnerData instance
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

		//data.setCooldownLength(new Timespan(TimePeriod.TICK, trialSpawner.getCooldownLength()));
		var config = SpawnerUtils.getTrialSpawnerConfiguration(trialSpawner, ominous);
		SkriptSpawnerData.applyToSpawnerData(config, data);
		data.setMaxSpawnDelay(new Timespan(TimePeriod.TICK, config.getDelay()));

		Set<TrialSpawnerRewardEntry> rewardEntries = config.getPossibleRewards().entrySet().stream()
			.map(entry -> new TrialSpawnerRewardEntry(entry.getKey(), entry.getValue()))
			.collect(Collectors.toSet());
		data.setRewardEntries(rewardEntries);

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
		//trialSpawner.setCooldownLength(Math.clamp(getCooldownLength().getAs(TimePeriod.TICK), 0 , Integer.MAX_VALUE));

		var config = SpawnerUtils.getTrialSpawnerConfiguration(trialSpawner, ominous);
		super.applyToSpawner(config);

		Map<LootTable, Integer> weightedMap = new HashMap<>();
		for (TrialSpawnerRewardEntry entry : rewardEntries) {
			weightedMap.put(entry.lootTable(), entry.weight());
		}
		config.setPossibleRewards(weightedMap);

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
	 * Returns a set of reward entries this trial spawner can choose during reward ejection.
	 * @return the set of trial spawner reward entries
	 */
	public @NotNull Set<TrialSpawnerRewardEntry> getRewardEntries() {
		return Set.copyOf(rewardEntries);
	}

	/**
	 * Sets the reward entries for this trial spawner.
	 * @param rewardEntries the set of reward entries to set
	 */
	public void setRewardEntries(@NotNull Set<TrialSpawnerRewardEntry> rewardEntries) {
		Preconditions.checkNotNull(rewardEntries, "rewardEntries cannot be null");
		this.rewardEntries = new HashSet<>(rewardEntries);
	}

	/**
	 * Adds multiple reward entries to the set of reward entries.
	 * @param rewardEntries the set of reward entries to add
	 */
	public void addRewardEntries(@NotNull Set<TrialSpawnerRewardEntry> rewardEntries) {
		Preconditions.checkNotNull(rewardEntries, "rewardEntries cannot be null");
		this.rewardEntries.addAll(rewardEntries);
	}

	/**
	 * Adds a specific reward entry to the set of reward entries.
	 * @param rewardEntry the reward entry to add
	 */
	public void addRewardEntry(@NotNull TrialSpawnerRewardEntry rewardEntry) {
		Preconditions.checkNotNull(rewardEntry, "rewardEntry cannot be null");
		this.rewardEntries.add(rewardEntry);
	}

	/**
	 * Removes multiple reward entries from the set of reward entries.
	 * @param rewardEntries the set of reward entries to remove
	 */
	public void removeRewardEntries(@NotNull Set<TrialSpawnerRewardEntry> rewardEntries) {
		Preconditions.checkNotNull(rewardEntries, "rewardEntries cannot be null");
		this.rewardEntries.removeAll(rewardEntries);
	}

	/**
	 * Removes a specific reward entry from the set of reward entries.
	 * @param rewardEntry the reward entry to remove
	 */
	public void removeRewardEntry(@NotNull TrialSpawnerRewardEntry rewardEntry) {
		Preconditions.checkNotNull(rewardEntry, "rewardEntry cannot be null");
		this.rewardEntries.remove(rewardEntry);
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

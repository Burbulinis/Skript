package org.skriptlang.skript.bukkit.spawners.util;

import ch.njol.yggdrasil.YggdrasilSerializable;
import com.google.common.base.Preconditions;
import org.bukkit.block.TrialSpawner;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the data of a {@link TrialSpawner} and its configuration.
 *
 * @see SkriptSpawnerData
 * @see AbstractSpawnerData
 */
public class SkriptTrialSpawnerData extends AbstractSpawnerData implements YggdrasilSerializable {

	private int activationRange = SpawnerUtils.DEFAULT_TRIAL_ACTIVATION_RANGE;
	//private @NotNull Timespan cooldownLength = SpawnerUtils.DEFAULT_COOLDOWN_LENGTH;
	private int baseMobAmount = SpawnerUtils.DEFAULT_BASE_MOB_AMOUNT;
	private int baseMobAmountIncrement = SpawnerUtils.DEFAULT_BASE_PER_PLAYER_INCREMENT;
	private int concurrentMobAmount = SpawnerUtils.DEFAULT_CONCURRENT_MOB_AMOUNT;
	private int concurrentMobAmountIncrement = SpawnerUtils.DEFAULT_CONCURRENT_PER_PLAYER_INCREMENT;

	private @NotNull List<TrialSpawnerRewardEntry> rewardEntries = new ArrayList<>();

	private final boolean ominous;

	/**
	 * Creates a new SkriptTrialSpawnerData instance
	 * @param ominous whether the data
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
	public static SkriptTrialSpawnerData fromBukkitTrialSpawner(@NotNull TrialSpawner trialSpawner) {
		return fromBukkitTrialSpawner(trialSpawner, false);
	}

	/**
	 * Creates a new {@code SkriptTrialSpawnerData} instance from the given Bukkit {@link TrialSpawner}.
	 * @param trialSpawner the Bukkit trial spawner to convert
	 * @param ominous whether the trial spawner is ominous
	 * @return a new {@code SkriptTrialSpawnerData} instance containing the data from the Bukkit trial spawner
	 */
	public static SkriptTrialSpawnerData fromBukkitTrialSpawner(@NotNull TrialSpawner trialSpawner, boolean ominous) {
		SkriptTrialSpawnerData data = new SkriptTrialSpawnerData(ominous);

		//data.setCooldownLength(new Timespan(TimePeriod.TICK, trialSpawner.getCooldownLength()));
		var config = SpawnerUtils.getTrialSpawnerConfiguration(trialSpawner, ominous);
		SpawnerUtils.applySpawnerDataToAbstractData(config, data);

		List<TrialSpawnerRewardEntry> rewardEntries = config.getPossibleRewards().entrySet().stream()
				.map(entry ->
						new TrialSpawnerRewardEntry(entry.getKey(), entry.getValue())
				)
				.toList();
		data.setRewardEntries(rewardEntries);

		data.setBaseMobAmount((int) config.getBaseSpawnsBeforeCooldown());
		data.setBaseMobAmountIncrement((int) config.getAdditionalSpawnsBeforeCooldown());
		data.setConcurrentMobAmount((int) config.getBaseSimultaneousEntities());
		data.setConcurrentMobAmountIncrement((int) config.getAdditionalSimultaneousEntities());

		return data;
	}

	public void applyDataToBukkitTrialSpawners(@NotNull TrialSpawner[] trialSpawners) {
		Preconditions.checkNotNull(trialSpawners, "trialSpawners cannot be null");
		for (TrialSpawner trialSpawner : trialSpawners) {
			applyDataToBukkitTrialSpawner(trialSpawner);
		}
	}

	public void applyDataToBukkitTrialSpawner(@NotNull TrialSpawner trialSpawner) {
		Preconditions.checkNotNull(trialSpawner, "trialSpawner cannot be null");
		//trialSpawner.setCooldownLength(Math.clamp(getCooldownLength().getAs(TimePeriod.TICK), 0 , Integer.MAX_VALUE));

		var config = SpawnerUtils.getTrialSpawnerConfiguration(trialSpawner, ominous);
		SpawnerUtils.applyAbstractDataToSpawner(this, config);

		Map<LootTable, Integer> weightedMap = new HashMap<>();
		for (TrialSpawnerRewardEntry entry : rewardEntries) {
			weightedMap.put(entry.lootTable(), entry.weight());
		}
		config.setPossibleRewards(weightedMap);

		config.setBaseSpawnsBeforeCooldown(getBaseMobAmount());
		config.setAdditionalSpawnsBeforeCooldown(getBaseMobAmountIncrement());
		config.setBaseSimultaneousEntities(getConcurrentMobAmount());
		config.setAdditionalSimultaneousEntities(getConcurrentMobAmountIncrement());

		trialSpawner.update(true, false);
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
	 * Returns whether this trial spawner data is for an ominous trial spawner.
	 * @return true if this is an ominous trial spawner data, false otherwise
	 */
	public boolean isOminous() {
		return ominous;
	}

	//public @NotNull Timespan getCooldownLength() {
	//	return cooldownLength;
	//}

	//public void setCooldownLength(@NotNull Timespan cooldownLength) {
	//	Preconditions.checkNotNull(cooldownLength, "Cooldown length cannot be null");
	//	this.cooldownLength = cooldownLength;
	//}

	/**
	 * Returns a list of reward entries this trial spawner can choose during reward ejection.
	 * @return the list of trial spawner reward entries
	 */
	public @NotNull List<TrialSpawnerRewardEntry> getRewardEntries() {
		return List.copyOf(rewardEntries);
	}

	/**
	 * Sets the reward entries for this trial spawner.
	 * @param rewardEntries the list of reward entries to set
	 */
	public void setRewardEntries(@NotNull List<TrialSpawnerRewardEntry> rewardEntries) {
		Preconditions.checkNotNull(rewardEntries, "rewardEntries cannot be null");
		this.rewardEntries = new ArrayList<>(rewardEntries);
	}

	/**
	 * Adds a specific reward entry to the list of reward entries.
	 * @param rewardEntry the reward entry to add
	 */
	public void addRewardEntry(@NotNull TrialSpawnerRewardEntry rewardEntry) {
		Preconditions.checkNotNull(rewardEntry, "rewardEntry cannot be null");
		this.rewardEntries.add(rewardEntry);
	}

	/**
	 * Removes a specific reward entry from the list of reward entries.
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

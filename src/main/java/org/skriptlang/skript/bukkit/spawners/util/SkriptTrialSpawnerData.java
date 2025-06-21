package org.skriptlang.skript.bukkit.spawners.util;

import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Timespan.TimePeriod;
import ch.njol.yggdrasil.YggdrasilSerializable;
import org.bukkit.block.TrialSpawner;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkriptTrialSpawnerData extends AbstractSpawnerData implements YggdrasilSerializable {

	private int requiredPlayerRange = SpawnerUtils.DEFAULT_TRIAL_REQUIRED_PLAYER_RANGE;
	private @NotNull Timespan cooldownLength = SpawnerUtils.DEFAULT_COOLDOWN_LENGTH;
	private int baseMobAmount = SpawnerUtils.DEFAULT_BASE_MOB_AMOUNT;
	private int baseMobAmountIncrement = SpawnerUtils.DEFAULT_BASE_PER_PLAYER_INCREMENT;
	private int concurrentMobAmount = SpawnerUtils.DEFAULT_CONCURRENT_MOB_AMOUNT;
	private int concurrentMobAmountIncrement = SpawnerUtils.DEFAULT_CONCURRENT_PER_PLAYER_INCREMENT;

	private List<TrialSpawnerRewardEntry> rewardEntries = new ArrayList<>();

	private final boolean ominous;

	public SkriptTrialSpawnerData(boolean ominous) {
		this.ominous = ominous;
	}

	public static SkriptTrialSpawnerData fromBukkitTrialSpawner(@NotNull TrialSpawner trialSpawner) {
		return fromBukkitTrialSpawner(trialSpawner, false);
	}

	public static SkriptTrialSpawnerData fromBukkitTrialSpawner(@NotNull TrialSpawner trialSpawner, boolean ominous) {
		SkriptTrialSpawnerData data = new SkriptTrialSpawnerData(ominous);

		data.setCooldownLength(new Timespan(TimePeriod.TICK, trialSpawner.getCooldownLength()));
		var config = SpawnerUtils.getTrialSpawnerConfiguration(trialSpawner, ominous);
		SpawnerUtils.applySpawnerDataToAbstractData(config, data);
		// this class overrides this method from AbstractSpawnerData
		data.setRequiredPlayerRange(config.getRequiredPlayerRange());

		data.setBaseMobAmount((int) config.getBaseSpawnsBeforeCooldown());

		return data;
	}

	public void applyDataToBukkitTrialSpawner(@NotNull TrialSpawner trialSpawner) {
		trialSpawner.setCooldownLength(Math.clamp(getCooldownLength().getAs(TimePeriod.TICK), 0 , Integer.MAX_VALUE));

		var config = SpawnerUtils.getTrialSpawnerConfiguration(trialSpawner, ominous);
		SpawnerUtils.applyAbstractDataToSpawner(this, config);
		// override the required player range as the default value differs
		trialSpawner.setRequiredPlayerRange(getRequiredPlayerRange());

		//todo: figure out what the difference is between trialSpawner.getRequiredPlayerRange() and config's one

		Map<LootTable, Integer> weightedMap = new HashMap<>();
		for (TrialSpawnerRewardEntry entry : rewardEntries) {
			weightedMap.put(entry.lootTable(), entry.weight());
		}
		config.setPossibleRewards(weightedMap);

		config.setBaseSpawnsBeforeCooldown(getBaseMobAmount());
		config.setAdditionalSpawnsBeforeCooldown(getBaseMobAmountIncrement());
		config.setBaseSimultaneousEntities(getConcurrentMobAmount());
		config.setAdditionalSimultaneousEntities(getConcurrentMobAmountIncrement());


		// todo: update trial spawner
	}

	@Override
	public int getRequiredPlayerRange() {
		return requiredPlayerRange;
	}

	@Override
	public void setRequiredPlayerRange(int requiredPlayerRange) {
		this.requiredPlayerRange = requiredPlayerRange;
	}

	public @NotNull Timespan getCooldownLength() {
		return cooldownLength;
	}

	public void setCooldownLength(@NotNull Timespan cooldownLength) {
		this.cooldownLength = cooldownLength;
	}

	public @NotNull List<TrialSpawnerRewardEntry> getRewardEntries() {
		return List.copyOf(rewardEntries);
	}

	public void setRewardEntries(@NotNull List<TrialSpawnerRewardEntry> rewardEntries) {
		this.rewardEntries = new ArrayList<>(rewardEntries);
	}

	public void addRewardEntry(@NotNull TrialSpawnerRewardEntry rewardEntry) {
		this.rewardEntries.add(rewardEntry);
	}

	public int getBaseMobAmount() {
		return baseMobAmount;
	}

	public void setBaseMobAmount(int mobAmount) {
		baseMobAmount = mobAmount;
	}

	public int getBaseMobAmountIncrement() {
		return baseMobAmountIncrement;
	}

	public void setBaseMobAmountIncrement(int incrementPerPlayer) {
		baseMobAmountIncrement = incrementPerPlayer;
	}

	public int getConcurrentMobAmount() {
		return concurrentMobAmount;
	}

	public void setConcurrentMobAmount(int mobAmount) {
		concurrentMobAmount = mobAmount;
	}

	public int getConcurrentMobAmountIncrement() {
		return concurrentMobAmountIncrement;
	}

	public void setConcurrentMobAmountIncrement(int incrementPerPlayer) {
		concurrentMobAmountIncrement = incrementPerPlayer;
	}

}

package org.skriptlang.skript.bukkit.spawners.util;

import org.bukkit.spawner.Spawner;
import org.jetbrains.annotations.NotNull;

public record SkriptSpawner(@NotNull Spawner spawner, @NotNull SpawnerType type) {}
package org.skriptlang.skript.bukkit.spawners.util;

import org.jetbrains.annotations.NotNull;

public record Spawner(@NotNull org.bukkit.spawner.Spawner spawner, @NotNull SpawnerType type) {}
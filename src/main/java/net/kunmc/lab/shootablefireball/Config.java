package net.kunmc.lab.shootablefireball;

import net.kunmc.lab.configlib.BaseConfig;
import net.kunmc.lab.configlib.value.BooleanValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Config extends BaseConfig {
    public final BooleanValue creativeConsumesItem = new BooleanValue(false);
    public final BooleanValue enableHand = new BooleanValue(true);
    public final BooleanValue enableArrow = new BooleanValue(true);

    public Config(@NotNull Plugin plugin) {
        super(plugin);
    }
}

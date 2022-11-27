package net.kunmc.lab.shootablefireball;

import net.kunmc.lab.configlib.BaseConfig;
import net.kunmc.lab.configlib.value.BooleanValue;
import net.kunmc.lab.configlib.value.IntegerValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Config extends BaseConfig {
    public final BooleanValue creativeConsumesItem = new BooleanValue(false);
    public final IntegerValue coolDown = new IntegerValue(0, 0, Integer.MAX_VALUE);
    public final BooleanValue enableHand = new BooleanValue(true);
    public final BooleanValue enableArrow = new BooleanValue(true);

    public Config(@NotNull Plugin plugin) {
        super(plugin);
    }
}

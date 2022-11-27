package net.kunmc.lab.shootablefireball.command;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.DoubleArgument;
import net.kunmc.lab.commandlib.argument.FloatArgument;
import net.kunmc.lab.commandlib.argument.IntegerArgument;
import net.kunmc.lab.commandlib.argument.PlayersArgument;
import net.kunmc.lab.shootablefireball.ShootableFireballPlugin;
import org.bukkit.entity.Player;

import java.util.List;

public class GiveCommand extends Command {
    public GiveCommand() {
        super("give");


        argument(new PlayersArgument("targets"), new DoubleArgument("speed"), (targets, speed, ctx) -> {
            give(targets, speed, 1.0F, 1, ctx);
        });

        argument(new PlayersArgument("targets"),
                 new DoubleArgument("speed"),
                 new FloatArgument("power"),
                 (targets, speed, power, ctx) -> {
                     give(targets, speed, power, 1, ctx);
                 });

        argument(new PlayersArgument("targets"),
                 new DoubleArgument("speed"),
                 new FloatArgument("power"),
                 new IntegerArgument("amount"),
                 this::give);
    }

    private void give(List<Player> targets, double speed, float power, int amount, CommandContext ctx) {
        ShootableFireballPlugin plugin = ShootableFireballPlugin.getInstance();
        targets.forEach(x -> {
            plugin.giveFireball(x, speed, power, amount);
        });

        ctx.sendSuccess(String.format("%d人のプレイヤーに配布", targets.size()));
    }
}

package net.kunmc.lab.shootablefireball.command;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.ConfigCommand;

public class MainCommand extends Command {
    public MainCommand(ConfigCommand configCommand) {
        super("shootablefireball");
        addChildren(configCommand, new GiveCommand());
    }
}

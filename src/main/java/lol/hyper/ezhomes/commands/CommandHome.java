package lol.hyper.ezhomes.commands;

import lol.hyper.ezhomes.HomeManagement;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class CommandHome implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        try {
            player.teleport(HomeManagement.getHomeLocation(player.getUniqueId(), args[0]));
            player.sendMessage(ChatColor.GREEN + "Whoosh!");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return true;
    }
}

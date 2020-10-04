package lol.hyper.ezhomes.commands;

import io.papermc.lib.PaperLib;
import lol.hyper.ezhomes.EzHomes;
import lol.hyper.ezhomes.HomeManagement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.awt.print.Paper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CommandHome implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "You must specify a home!");
        } else {
            if (args.length == 1){
                try {
                    ArrayList<String> playerHomes = HomeManagement.getPlayerHomes(player.getUniqueId());
                    if (playerHomes != null) {
                        if (HomeManagement.canPlayerTeleport(player)) {
                            if (playerHomes.contains(args[0])) {
                                if (PaperLib.isPaper()) {
                                    PaperLib.teleportAsync(player, HomeManagement.getHomeLocation(player.getUniqueId(), args[0]));
                                } else {
                                    player.teleport(HomeManagement.getHomeLocation(player.getUniqueId(), args[0]));
                                }
                                player.sendMessage(ChatColor.GREEN + "Whoosh!");
                                EzHomes.getInstance().teleportCooldowns.put(player, System.nanoTime());
                            } else {
                                player.sendMessage(ChatColor.RED + "That home does not exist.");
                            }
                        } else {
                            long timeLeft = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - EzHomes.getInstance().teleportCooldowns.get(player));
                            long configTime = EzHomes.getInstance().config.getInt("teleport-cooldown");
                            player.sendMessage(ChatColor.RED + "You must wait " + (configTime - timeLeft) + " seconds to teleport.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have any homes set.");
                    }
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                    sender.sendMessage(ChatColor.RED + "Unable to teleport! This is from a file issue. Please check your console for more information.");
                    Bukkit.getLogger().severe("Error reading home data for player: " + player.getName());
                }
            }
        }
        return true;
    }
}

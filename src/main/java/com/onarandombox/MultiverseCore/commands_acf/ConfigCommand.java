package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@CommandAlias("mv")
@Subcommand("config")
@CommandPermission("multiverse.core.config")
public class ConfigCommand extends MultiverseCommand {

    public ConfigCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("show")
    @Description("Show multiverse config values.")
    public void onShowCommand(@NotNull CommandSender sender) {
        StringBuilder builder = new StringBuilder();
        Map<String, Object> serializedConfig = this.plugin.getMVConfig().serialize();

        for (Map.Entry<String, Object> entry : serializedConfig.entrySet()) {
            builder.append(ChatColor.GREEN)
                    .append(entry.getKey())
                    .append(ChatColor.WHITE).append(" = ").append(ChatColor.GOLD)
                    .append(entry.getValue().toString())
                    .append(ChatColor.WHITE).append(", ");
        }

        String message = builder.toString();
        message = message.substring(0, message.length() - 2);
        sender.sendMessage(message);
    }

    @Subcommand("set")
    @Syntax("<property> <value>")
    @CommandCompletion("@MVConfigs")
    @Description("Set Global MV Variables.")
    public void onSetCommand(@NotNull CommandSender sender,
                             @NotNull String property,
                             @NotNull @Single String value) {

        property = property.toLowerCase();

        if (!this.plugin.getMVConfig().setConfigProperty(property, value)) {
            sender.sendMessage(String.format("%sSetting '%s' to '%s' failed!", ChatColor.RED, property, value));
            return;
        }

        if (!this.plugin.saveMVConfigs()) {
            sender.sendMessage(ChatColor.RED + "FAIL!" + ChatColor.WHITE + " Check your console for details!");
            return;
        }

        // special rule, don't forget to set the world!
        //TODO: Potentially should move to MultiverseCore#loadConfigs method
        if (property.equalsIgnoreCase("firstspawnworld")) {
            this.plugin.getMVWorldManager().setFirstSpawnWorld(value);
        }

        sender.sendMessage(ChatColor.GREEN + "SUCCESS!" + ChatColor.WHITE + " Values were updated successfully!");
        this.plugin.loadConfigs();
    }
}
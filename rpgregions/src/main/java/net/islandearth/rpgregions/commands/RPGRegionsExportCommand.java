package net.islandearth.rpgregions.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.specifier.Greedy;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.effects.RegionEffect;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import net.islandearth.rpgregions.requirements.RegionRequirement;
import net.islandearth.rpgregions.rewards.DiscoveryReward;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.logging.Level;

public class RPGRegionsExportCommand {

    private final RPGRegions plugin;
    private final MinecraftHelp<CommandSender> help;

    public RPGRegionsExportCommand(RPGRegions plugin, PaperCommandManager<CommandSender> manager) {
        this.plugin = plugin;
        this.help = new MinecraftHelp<>(
                "/rpgre help",
                player -> plugin.adventure().sender(player),
                manager
        );
    }

    @CommandDescription("The default RPGRegions export command.")
    @CommandMethod("rpgre")
    public void onDefault(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Use this command to export/import a region template.");
    }

    @CommandMethod("rpgre help [query]")
    public void onHelp(final CommandSender sender, @Argument("query") @Greedy @Nullable String query) {
        help.queryCommands(query == null ? "" : query, sender);
    }

    @CommandDescription("Exports a region to a template file (`plugins/RPGRegions/templates`).")
    @CommandMethod("rpgre export <region>")
    public void onExport(CommandSender sender,
                         @Argument("region") ConfiguredRegion region) {
        File templates = new File(plugin.getDataFolder() + File.separator + "templates");
        File target = new File(templates + File.separator + region.getId() + "_template.json");
        region.save(plugin, target);

        sender.sendMessage(ChatColor.GREEN + "Your region has been saved as a template to " + target + ".");
        sender.sendMessage(ChatColor.YELLOW + "You may use /rpgre import " + region.getId() + " <target> to import the requirements, rewards, etc. of this template to the target region.");
    }

    @CommandDescription("Imports a template region into an existing configured region")
    @CommandMethod("rpgre import <template> <region>")
    public void onImport(CommandSender sender,
                         @Argument(value = "template",
                                 suggestions = "templates",
                                 description = "The name of the template file") String template,
                         @Argument("region") ConfiguredRegion region) {
        File templateFile = new File(plugin.getDataFolder() + File.separator + "templates" + File.separator + template);
        if (!templateFile.exists()) {
            sender.sendMessage(ChatColor.RED + "That template does not exist.");
            return;
        }

        int rewards = 0, requirements = 0, effects = 0;
        sender.sendMessage(ChatColor.GREEN + "Reading template file...");
        try (Reader reader = new FileReader(templateFile)) {
            ConfiguredRegion templateRegion = plugin.getGson().fromJson(reader, ConfiguredRegion.class);
            sender.sendMessage(ChatColor.GREEN + "Converting rewards to " + region.getId() + "...");
            for (DiscoveryReward reward : templateRegion.getRewards()) {
                region.getRewards().add(reward);
                rewards++;
            }

            sender.sendMessage(ChatColor.GREEN + "Converting requirements to " + region.getId() + "...");
            for (RegionRequirement requirement : templateRegion.getRequirements()) {
                region.getRequirements().add(requirement);
                requirements++;
            }

            sender.sendMessage(ChatColor.GREEN + "Converting effects to " + region.getId() + "...");
            for (RegionEffect effect : templateRegion.getEffects()) {
                region.getEffects().add(effect);
                effects++;
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error loading template config " + templateFile.getName() + ".", e);
        }

        sender.sendMessage(ChatColor.GREEN + String.format("Done transferring (%d, %d, %d) rewards, requirements, and effects.", rewards, requirements, effects));
    }
}

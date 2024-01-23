package net.islandearth.rpgregions.commands;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.ParserRegistry;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import io.leangen.geantyref.TypeFactory;
import io.leangen.geantyref.TypeToken;
import net.islandearth.rpgregions.RPGRegions;
import net.islandearth.rpgregions.api.integrations.rpgregions.RPGRegionsIntegration;
import net.islandearth.rpgregions.api.integrations.rpgregions.region.RPGRegionsRegion;
import net.islandearth.rpgregions.commands.caption.RPGRegionsCaptionRegistry;
import net.islandearth.rpgregions.commands.caption.RPGRegionsCaptionRegistryFactory;
import net.islandearth.rpgregions.commands.parser.ConfiguredRegionArgument;
import net.islandearth.rpgregions.commands.parser.FaunaArgument;
import net.islandearth.rpgregions.commands.parser.IntegrationRegionArgument;
import net.islandearth.rpgregions.fauna.FaunaInstance;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Commands {

    public Commands(RPGRegions plugin) {

        // This function maps the command sender type of our choice to the bukkit command sender.
        final Function<CommandSender, CommandSender> mapperFunction = Function.identity();

        final PaperCommandManager<CommandSender> manager;
        try {
            manager = new PaperCommandManager<>(
                    plugin,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    mapperFunction,
                    mapperFunction
            );
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize the command manager");
            e.printStackTrace();
            return;
        }

        // Register Brigadier mappings
        if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            manager.registerBrigadier();
        }

        // Register asynchronous completions
        if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            manager.registerAsynchronousCompletions();
        }

        /*
         * Create the confirmation manager. This allows us to require certain commands to be
         * confirmed before they can be executed
         */
        final CommandConfirmationManager<CommandSender> confirmationManager = new CommandConfirmationManager<>(30L, TimeUnit.SECONDS,
                /* Action when confirmation is required */
                context -> {
                    final String name = context.getCommand().getArguments().get(0).getName();
                    context.getCommandContext().getSender().sendMessage(
                        ChatColor.RED + "Confirmation required. Confirm using " + ChatColor.YELLOW + "/" + name + " confirm" + ChatColor.RED + ".");
                },
                /* Action when no confirmation is pending */ sender -> sender.sendMessage(
                ChatColor.RED + "You don't have any pending commands.")
        );

        // Register the confirmation processor. This will enable confirmations for commands that require it
        confirmationManager.registerConfirmationProcessor(manager);

        // This will allow you to decorate commands with descriptions
        final Function<ParserParameters, CommandMeta> commandMetaFunction = parserParameters ->
                CommandMeta.simple()
                        .with(CommandMeta.DESCRIPTION, parserParameters.get(StandardParameters.DESCRIPTION, "No description"))
                        .build();

        // Override the default exception handlers
        // todo: change some stuff lmao
        new MinecraftExceptionHandler<CommandSender>()
                .withInvalidSyntaxHandler()
                .withInvalidSenderHandler()
                .withNoPermissionHandler()
                .withArgumentParsingHandler()
                .apply(manager, player -> plugin.adventure().sender(player));

        // Register our custom caption registry so we can define exception messages for parsers
        final RPGRegionsCaptionRegistry<CommandSender> captionRegistry = new RPGRegionsCaptionRegistryFactory<CommandSender>().create();
        manager.captionRegistry(captionRegistry);

        // Register custom parsers
        final ParserRegistry<CommandSender> parserRegistry = manager.parserRegistry();
        parserRegistry.registerParserSupplier(TypeToken.get(ConfiguredRegion.class), parserParameters ->
                new ConfiguredRegionArgument.ConfiguredRegionParser<>());
        parserRegistry.registerParserSupplier(TypeToken.get(TypeFactory.parameterizedClass(FaunaInstance.class, TypeFactory.unboundWildcard())), parserParameters ->
                new FaunaArgument.FaunaParser<>());
        parserRegistry.registerParserSupplier(TypeToken.get(RPGRegionsRegion.class), parserParameters ->
                new IntegrationRegionArgument.IntegrationRegionParser<>());

        parserRegistry.registerSuggestionProvider("region-types", (context, arg) -> ImmutableList.of("Cuboid", "Poly"));

        parserRegistry.registerSuggestionProvider("integration-regions", (context, arg) -> {
            if (context.getSender() instanceof Player player) {
                return ImmutableList.copyOf(plugin.getManagers().getIntegrationManager().getAllRegionNames(player.getWorld()));
            }
            return new ArrayList<>();
        });

        parserRegistry.registerSuggestionProvider("templates", (context, arg) -> {
            File templates = new File(plugin.getDataFolder() + File.separator + "templates");
            List<String> files = new ArrayList<>();
            for (File file : templates.listFiles()) {
                files.add(file.getName());
            }
            return files;
        });

        parserRegistry.registerSuggestionProvider("schematics", (context, arg) -> {
            File schematicFolder = new File("plugins/WorldEdit/schematics/");
            List<String> files = new ArrayList<>();
            for (File file : schematicFolder.listFiles()) {
                files.add(file.getName());
            }
            return files;
        });

        parserRegistry.registerSuggestionProvider("async", (context, arg) -> List.of("--async"));

        final AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(manager, CommandSender.class, commandMetaFunction);
        annotationParser.parse(new DiscoveriesCommand(plugin));
        annotationParser.parse(new RPGRegionsCommand(plugin, manager));
        annotationParser.parse(new RPGRegionsDebugCommand(plugin));
        annotationParser.parse(new RPGRegionsExportCommand(plugin, manager));
        if (plugin.getManagers().getIntegrationManager() instanceof RPGRegionsIntegration) {
            annotationParser.parse(new RPGRegionsIntegrationCommand(plugin, manager));
        }
    }
}

package net.islandearth.rpgregions.commands.parser;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import com.google.common.collect.ImmutableList;
import net.islandearth.rpgregions.api.RPGRegionsAPI;
import net.islandearth.rpgregions.api.integrations.rpgregions.RPGRegionsIntegration;
import net.islandearth.rpgregions.api.integrations.rpgregions.region.RPGRegionsRegion;
import net.islandearth.rpgregions.commands.caption.RPGRegionsCaptionKeys;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.function.BiFunction;

public final class IntegrationRegionArgument<C> extends CommandArgument<C, RPGRegionsRegion> {

    public IntegrationRegionArgument(
            final boolean required,
            final @NonNull String name,
            final @NonNull String defaultValue,
            final @Nullable BiFunction<@NonNull CommandContext<C>, @NonNull String, @NonNull List<@NonNull String>> suggestionsProvider) {
        super(required, name, new IntegrationRegionArgument.IntegrationRegionParser<>(), defaultValue, RPGRegionsRegion.class, suggestionsProvider);
    }

    public static <C> IntegrationRegionArgument.Builder<C> newBuilder(final @NonNull String name) {
        return new IntegrationRegionArgument.Builder<>(name);
    }

    public static final class Builder<C> extends CommandArgument.Builder<C, RPGRegionsRegion> {

        private Builder(final @NonNull String name) {
            super(RPGRegionsRegion.class, name);
        }

        @Override
        public @NonNull CommandArgument<C, RPGRegionsRegion> build() {
            return new IntegrationRegionArgument<>(
                    this.isRequired(),
                    this.getName(),
                    this.getDefaultValue(),
                    this.getSuggestionsProvider()
            );
        }
    }

    public static final class IntegrationRegionParser<C> implements ArgumentParser<C, RPGRegionsRegion> {
        @Override
        public @NonNull ArgumentParseResult<RPGRegionsRegion> parse(
                @NonNull CommandContext<@NonNull C> commandContext,
                @NonNull Queue<@NonNull String> inputQueue) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                        IntegrationRegionArgument.class,
                        commandContext
                ));
            }

            if (RPGRegionsAPI.getAPI().getManagers().getIntegrationManager() instanceof RPGRegionsIntegration rpgRegionsIntegration) {
                Optional<RPGRegionsRegion> region = rpgRegionsIntegration.getRegion(input);
                if (region.isPresent()) {
                    inputQueue.remove();
                    return ArgumentParseResult.success(region.get());
                }
            }

            return ArgumentParseResult.failure(new IntegrationRegionParserException(input, commandContext));
        }

        @Override
        public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<C> commandContext, @NonNull String input) {
            if (commandContext.getSender() instanceof Player player) {
                return ImmutableList.copyOf(RPGRegionsAPI.getAPI().getManagers().getIntegrationManager().getAllRegionNames(player.getWorld()));
            }
            return new ArrayList<>();
        }
    }

    public static final class IntegrationRegionParserException extends ParserException {

        public IntegrationRegionParserException(
                final @NonNull String input,
                final @NonNull CommandContext<?> context
        ) {
            super(
                    RPGRegionsRegion.class,
                    context,
                    RPGRegionsCaptionKeys.ARGUMENT_PARSE_FAILURE_REGION_NOT_FOUND,
                    CaptionVariable.of("input", input)
            );
        }
    }
}

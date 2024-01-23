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
import net.islandearth.rpgregions.commands.caption.RPGRegionsCaptionKeys;
import net.islandearth.rpgregions.managers.data.region.ConfiguredRegion;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.function.BiFunction;

public final class ConfiguredRegionArgument<C> extends CommandArgument<C, ConfiguredRegion> {

    public ConfiguredRegionArgument(
            final boolean required,
            final @NonNull String name,
            final @NonNull String defaultValue,
            final @Nullable BiFunction<@NonNull CommandContext<C>, @NonNull String, @NonNull List<@NonNull String>> suggestionsProvider) {
        super(required, name, new ConfiguredRegionArgument.ConfiguredRegionParser<>(), defaultValue, ConfiguredRegion.class, suggestionsProvider);
    }

    public static <C> ConfiguredRegionArgument.Builder<C> newBuilder(final @NonNull String name) {
        return new ConfiguredRegionArgument.Builder<>(name);
    }

    public static final class Builder<C> extends CommandArgument.Builder<C, ConfiguredRegion> {

        private Builder(final @NonNull String name) {
            super(ConfiguredRegion.class, name);
        }

        @Override
        public @NonNull CommandArgument<C, ConfiguredRegion> build() {
            return new ConfiguredRegionArgument<>(
                    this.isRequired(),
                    this.getName(),
                    this.getDefaultValue(),
                    this.getSuggestionsProvider()
            );
        }
    }

    public static final class ConfiguredRegionParser<C> implements ArgumentParser<C, ConfiguredRegion> {
        @Override
        public @NonNull ArgumentParseResult<ConfiguredRegion> parse(
                @NonNull CommandContext<@NonNull C> commandContext,
                @NonNull Queue<@NonNull String> inputQueue) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                        ConfiguredRegionArgument.class,
                        commandContext
                ));
            }

            final Optional<ConfiguredRegion> configuredRegion = RPGRegionsAPI.getAPI().getManagers().getRegionsCache().getConfiguredRegion(input);
            if (configuredRegion.isPresent()) {
                inputQueue.remove();
                return ArgumentParseResult.success(configuredRegion.get());
            }
            return ArgumentParseResult.failure(new ConfiguredRegionParserException(input, commandContext));
        }

        @Override
        public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<C> commandContext, @NonNull String input) {
            return ImmutableList.copyOf(RPGRegionsAPI.getAPI().getManagers().getRegionsCache().getConfiguredRegions().keySet());
        }
    }

    public static final class ConfiguredRegionParserException extends ParserException {

        public ConfiguredRegionParserException(
                final @NonNull String input,
                final @NonNull CommandContext<?> context
        ) {
            super(
                    ConfiguredRegionParser.class,
                    context,
                    RPGRegionsCaptionKeys.ARGUMENT_PARSE_FAILURE_REGION_NOT_FOUND,
                    CaptionVariable.of("input", input)
            );
        }
    }
}

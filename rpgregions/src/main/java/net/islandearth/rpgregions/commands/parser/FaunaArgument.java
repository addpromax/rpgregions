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
import net.islandearth.rpgregions.fauna.FaunaInstance;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public final class FaunaArgument<C> extends CommandArgument<C, FaunaInstance> {

    public FaunaArgument(
            final boolean required,
            final @NonNull String name,
            final @NonNull String defaultValue,
            final @Nullable BiFunction<@NonNull CommandContext<C>, @NonNull String, @NonNull List<@NonNull String>> suggestionsProvider) {
        super(required, name, new FaunaArgument.FaunaParser<>(), defaultValue, FaunaInstance.class, suggestionsProvider);
    }

    public static <C> FaunaArgument.Builder<C> newBuilder(final @NonNull String name) {
        return new FaunaArgument.Builder<>(name);
    }

    public static final class Builder<C> extends CommandArgument.Builder<C, FaunaInstance> {

        private Builder(final @NonNull String name) {
            super(FaunaInstance.class, name);
        }

        @Override
        public @NonNull CommandArgument<C, FaunaInstance> build() {
            return new FaunaArgument<>(
                    this.isRequired(),
                    this.getName(),
                    this.getDefaultValue(),
                    this.getSuggestionsProvider()
            );
        }
    }

    public static final class FaunaParser<C> implements ArgumentParser<C, FaunaInstance> {
        @Override
        public @NonNull ArgumentParseResult<FaunaInstance> parse(
                @NonNull CommandContext<@NonNull C> commandContext,
                @NonNull Queue<@NonNull String> inputQueue) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                        FaunaArgument.class,
                        commandContext
                ));
            }


            final Optional<FaunaInstance<?>> fauna = RPGRegionsAPI.getAPI().getManagers().getFaunaCache().getFauna(input);
            if (fauna.isPresent()) {
                inputQueue.remove();
                return ArgumentParseResult.success(fauna.get());
            }
            return ArgumentParseResult.failure(new FaunaParserException(input, commandContext));
        }

        @Override
        public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<C> commandContext, @NonNull String input) {
            return ImmutableList.copyOf(RPGRegionsAPI.getAPI().getManagers().getFaunaCache().getFauna().stream().map(FaunaInstance::getIdentifier).collect(Collectors.toList()));
        }
    }

    public static final class FaunaParserException extends ParserException {

        public FaunaParserException(
                final @NonNull String input,
                final @NonNull CommandContext<?> context
        ) {
            super(
                    FaunaParser.class,
                    context,
                    RPGRegionsCaptionKeys.ARGUMENT_PARSE_FAILURE_FAUNA_NOT_FOUND,
                    CaptionVariable.of("input", input)
            );
        }
    }
}

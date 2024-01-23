package net.islandearth.rpgregions.commands.caption;

import cloud.commandframework.captions.Caption;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class RPGRegionsCaptionKeys {

    private static final Collection<Caption> RECOGNIZED_CAPTIONS = new LinkedList<>();

    public static final Caption ARGUMENT_PARSE_FAILURE_REGION_NOT_FOUND = of("argument.parse.failure.region_not_found");
    public static final Caption ARGUMENT_PARSE_FAILURE_FAUNA_NOT_FOUND = of("argument.parse.failure.fauna_not_found");

    private RPGRegionsCaptionKeys() { }

    private static @NonNull Caption of(final @NonNull String key) {
        final Caption caption = Caption.of(key);
        RECOGNIZED_CAPTIONS.add(caption);
        return caption;
    }

    /**
     * Get an immutable collection containing all standard caption keys
     *
     * @return Immutable collection of keys
     */
    public static @NonNull Collection<@NonNull Caption> getStandardCaptionKeys() {
        return Collections.unmodifiableCollection(RECOGNIZED_CAPTIONS);
    }
}

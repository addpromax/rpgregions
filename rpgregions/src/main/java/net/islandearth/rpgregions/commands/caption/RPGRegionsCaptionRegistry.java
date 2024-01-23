package net.islandearth.rpgregions.commands.caption;

import cloud.commandframework.bukkit.BukkitCaptionRegistry;

/**
 * Caption registry that uses bi-functions to produce messages
 *
 * @param <C> Command sender type
 */
public class RPGRegionsCaptionRegistry<C> extends BukkitCaptionRegistry<C> {

    /**
     * Default caption for {@link RPGRegionsCaptionKeys#ARGUMENT_PARSE_FAILURE_REGION_NOT_FOUND}.
     */
    public static final String ARGUMENT_PARSE_FAILURE_REGION_NOT_FOUND = "Could not find region '{input}'";

    protected RPGRegionsCaptionRegistry() {
        super();
        this.registerMessageFactory(
                RPGRegionsCaptionKeys.ARGUMENT_PARSE_FAILURE_REGION_NOT_FOUND,
                (caption, sender) -> ARGUMENT_PARSE_FAILURE_REGION_NOT_FOUND
        );
    }
}

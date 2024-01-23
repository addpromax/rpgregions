package net.islandearth.rpgregions.requirements;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import net.islandearth.rpgregions.api.IRPGRegionsAPI;
import net.islandearth.rpgregions.gui.GuiEditable;
import net.islandearth.rpgregions.gui.IGuiEditable;
import net.islandearth.rpgregions.gui.element.ICustomGuiFeedback;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class RegionRequirement implements IGuiEditable, ICustomGuiFeedback {

    private final transient IRPGRegionsAPI api;

    @GuiEditable(value = "Prevent Type", type = GuiEditable.GuiEditableType.CHAT)
    private PreventType preventType;

    @GuiEditable(value = "Prevention Entry Message", type = GuiEditable.GuiEditableType.CHAT)
    private String preventMessage;

    @GuiEditable(value = "Prevention Commands", type = GuiEditable.GuiEditableType.CHAT)
    private List<String> preventCommands;

    public RegionRequirement(IRPGRegionsAPI api) {
        this(api, PreventType.TELEPORT);
    }

    public RegionRequirement(IRPGRegionsAPI api, PreventType preventType) {
        this.api = api;
        this.preventType = preventType;
        this.preventCommands = new ArrayList<>();
    }

    public IRPGRegionsAPI getApi() {
        return api;
    }

    /**
     * Checks whether this player meets the requirements to enter.
     * @param player player to check
     * @return true if requirements are met by this player
     */
    public abstract boolean meetsRequirements(Player player);

    public PreventType getPreventType() {
        return preventType;
    }

    public abstract String getText(Player player);

    public String getPluginRequirement() {
        return null;
    }

    @Nullable
    public String getPreventMessage() {
        return preventMessage;
    }

    public void setPreventMessage(String preventMessage) {
        this.preventMessage = preventMessage;
    }

    public List<String> getPreventCommands() {
        return preventCommands;
    }

    public void setPreventCommands(List<String> preventCommand) {
        this.preventCommands = preventCommand;
    }

    @Override
    public boolean feedback(Player player, String input) {
        Optional<?> value = Enums.getIfPresent(PreventType.class, input.toUpperCase());
        if (value.isPresent()) {
            this.preventType = PreventType.valueOf(input.toUpperCase());
            return true;
        }
        return false;
    }

    @Override
    public String info(String field) {
        if (field.equals("preventType")) {
            String preventTypes = StringUtils.join(PreventType.values(), ", ");
            return "Enter one of the following: " + preventTypes;
        }
        return "";
    }
}

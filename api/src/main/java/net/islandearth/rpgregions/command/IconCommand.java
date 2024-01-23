package net.islandearth.rpgregions.command;

public record IconCommand(String command, CommandClickType clickType, boolean console, int cooldown) {

    public enum CommandClickType {
        DISCOVERED,
        UNDISCOVERED
    }
}

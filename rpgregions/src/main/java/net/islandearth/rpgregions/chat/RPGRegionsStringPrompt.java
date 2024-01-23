package net.islandearth.rpgregions.chat;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RPGRegionsStringPrompt extends StringPrompt {

    private final String promptText;

    public RPGRegionsStringPrompt(String promptText) {
        this.promptText = promptText;
    }

    @NotNull
    @Override
    public String getPromptText(@NotNull ConversationContext context) {
        return promptText;
    }

    @Nullable
    @Override
    public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        context.setSessionData("input", input);
        return Prompt.END_OF_CONVERSATION;
    }
}

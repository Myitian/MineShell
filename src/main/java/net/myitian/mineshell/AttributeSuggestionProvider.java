package net.myitian.mineshell;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.myitian.util.ExecRunner;
import net.myitian.util.ProcManager;

import java.util.concurrent.CompletableFuture;

class AttributeSuggestionProvider {
    public static CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder builder) {
        ExecRunner runner = ProcManager.getRunner();
        if (runner != null) {
            Process proc = runner.getProc();
            if (proc != null) {
                if (proc.isAlive()) {
                    String unsentMsg = runner.getUnsentMessage();
                    if (unsentMsg != null) {
                        for (String msg : unsentMsg.split("\n")) {
                            builder.suggest(msg);
                        }
                    }

                }
            }
        }
        return builder.buildFuture();
    }
}
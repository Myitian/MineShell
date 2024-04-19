package net.myitian.mineshell.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ExtendBoolArgumentType implements ArgumentType<Boolean> {
    private static final SimpleCommandExceptionType UNKNOWN_VALUE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.bool.unknown"));
    public final String falseValue, trueValue;
    public final boolean ignoreCase;
    final String lFalseValue, lTrueValue;
    private final Collection<String> EXAMPLES;

    ExtendBoolArgumentType(String falseValue, String trueValue, boolean ignoreCase) {
        this.falseValue = falseValue;
        this.trueValue = trueValue;
        this.ignoreCase = ignoreCase;
        EXAMPLES = Arrays.asList(falseValue, trueValue);
        lFalseValue = falseValue.toLowerCase();
        lTrueValue = trueValue.toLowerCase();
    }

    public static ExtendBoolArgumentType exBool() {
        return exBool("false", "true");
    }

    public static ExtendBoolArgumentType exBool(String falseValue, String trueValue) {
        return exBool(falseValue, trueValue, true);
    }

    public static ExtendBoolArgumentType exBool(String falseValue, String trueValue, boolean ignoreCase) {
        return new ExtendBoolArgumentType(falseValue, trueValue, ignoreCase);
    }

    public static <S> boolean getBoolean(CommandContext<S> context, String name) {
        return context.getArgument(name, Boolean.class);
    }

    @Override
    public Boolean parse(StringReader reader) throws CommandSyntaxException {
        String s = reader.readUnquotedString();
        if (s.equals(falseValue))
            return false;
        if (s.equals(trueValue))
            return true;
        throw UNKNOWN_VALUE_EXCEPTION.createWithContext(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (ignoreCase) {
            if (lFalseValue.startsWith(builder.getRemainingLowerCase()))
                builder.suggest(falseValue);
            if (lTrueValue.startsWith(builder.getRemainingLowerCase()))
                builder.suggest(trueValue);
        } else {
            if (falseValue.startsWith(builder.getRemaining()))
                builder.suggest(falseValue);
            if (trueValue.startsWith(builder.getRemaining()))
                builder.suggest(trueValue);
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

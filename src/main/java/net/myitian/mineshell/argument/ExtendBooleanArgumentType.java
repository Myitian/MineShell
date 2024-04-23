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

public class ExtendBooleanArgumentType implements ArgumentType<Boolean> {
    private static final SimpleCommandExceptionType UNKNOWN_VALUE_EXCEPTION = new SimpleCommandExceptionType(
            Text.translatableWithFallback("mineshell.argument.bool.unknown", "Unknown boolean value"));
    public final String falseValue, trueValue;
    public final boolean ignoreCase;
    final String lFalseValue, lTrueValue;
    private final Collection<String> EXAMPLES;

    ExtendBooleanArgumentType(String falseValue, String trueValue, boolean ignoreCase) {
        this.falseValue = falseValue;
        this.trueValue = trueValue;
        this.ignoreCase = ignoreCase;
        EXAMPLES = Arrays.asList(falseValue, trueValue);
        lFalseValue = falseValue.toLowerCase();
        lTrueValue = trueValue.toLowerCase();
    }

    public static ExtendBooleanArgumentType exBool() {
        return exBool("false", "true");
    }

    public static ExtendBooleanArgumentType exBool(String falseValue, String trueValue) {
        return exBool(falseValue, trueValue, true);
    }

    public static ExtendBooleanArgumentType exBool(String falseValue, String trueValue, boolean ignoreCase) {
        return new ExtendBooleanArgumentType(falseValue, trueValue, ignoreCase);
    }

    public static <S> boolean getBoolean(CommandContext<S> context, String name) {
        return context.getArgument(name, Boolean.class);
    }

    @Override
    public Boolean parse(StringReader reader) throws CommandSyntaxException {
        if (ignoreCase) {
            String s = reader.readUnquotedString().toLowerCase();
            if (s.equals(lFalseValue))
                return false;
            if (s.equals(lTrueValue))
                return true;
        } else {
            String s = reader.readUnquotedString();
            if (s.equals(falseValue))
                return false;
            if (s.equals(trueValue))
                return true;
        }
        throw UNKNOWN_VALUE_EXCEPTION.createWithContext(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (ignoreCase) {
            String s = builder.getRemainingLowerCase();
            if (lFalseValue.startsWith(s))
                builder.suggest(falseValue);
            if (lTrueValue.startsWith(s))
                builder.suggest(trueValue);
        } else {
            String s = builder.getRemaining();
            if (falseValue.startsWith(s))
                builder.suggest(falseValue);
            if (trueValue.startsWith(s))
                builder.suggest(trueValue);
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

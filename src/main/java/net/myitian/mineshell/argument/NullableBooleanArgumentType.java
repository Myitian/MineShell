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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class NullableBooleanArgumentType implements ArgumentType<Optional<Boolean>> {
    private static final SimpleCommandExceptionType UNKNOWN_VALUE_EXCEPTION = new SimpleCommandExceptionType(
            Text.translatableWithFallback("mineshell.argument.bool3.unknown", "Unknown nullable boolean value"));
    public final String falseValue, trueValue, nullValue;
    public final boolean ignoreCase;
    final String lFalseValue, lTrueValue, lNullValue;
    private final Collection<String> EXAMPLES;

    NullableBooleanArgumentType(String falseValue, String trueValue, String nullValue, boolean ignoreCase) {
        this.falseValue = falseValue;
        this.trueValue = trueValue;
        this.nullValue = nullValue;
        this.ignoreCase = ignoreCase;
        EXAMPLES = Arrays.asList(falseValue, trueValue, nullValue);
        lFalseValue = falseValue.toLowerCase();
        lTrueValue = trueValue.toLowerCase();
        lNullValue = nullValue.toLowerCase();
    }

    public static NullableBooleanArgumentType bool3() {
        return bool3("false", "true", "null");
    }

    public static NullableBooleanArgumentType bool3(String falseValue, String trueValue, String nullValue) {
        return bool3(falseValue, trueValue, nullValue, true);
    }

    public static NullableBooleanArgumentType bool3(String falseValue, String trueValue, String nullValue, boolean ignoreCase) {
        return new NullableBooleanArgumentType(falseValue, trueValue, nullValue, ignoreCase);
    }

    public static <S> Boolean getBoolean(CommandContext<S> context, String name) {
        Optional<?> value = context.getArgument(name, Optional.class);
        if (value.isEmpty())
            return null;
        Class<?> clazz = value.get().getClass();
        if (clazz.equals(Boolean.class))
            return (Boolean) value.get();
        throw new IllegalArgumentException("Argument '" + name + "' is defined as Optional<" + clazz.getSimpleName() + ">, not Optional<Boolean>");
    }

    @Override
    public Optional<Boolean> parse(StringReader reader) throws CommandSyntaxException {
        if (ignoreCase) {
            String s = reader.readUnquotedString().toLowerCase();
            if (s.equals(lFalseValue))
                return Optional.of(false);
            if (s.equals(lTrueValue))
                return Optional.of(true);
            if (s.equals(lNullValue))
                return Optional.empty();
        } else {
            String s = reader.readUnquotedString();
            if (s.equals(falseValue))
                return Optional.of(false);
            if (s.equals(trueValue))
                return Optional.of(true);
            if (s.equals(nullValue))
                return Optional.empty();
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
            if (lNullValue.startsWith(s))
                builder.suggest(nullValue);
        } else {
            String s = builder.getRemaining();
            if (falseValue.startsWith(s))
                builder.suggest(falseValue);
            if (trueValue.startsWith(s))
                builder.suggest(trueValue);
            if (nullValue.startsWith(s))
                builder.suggest(nullValue);
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

package net.myitian.mineshell;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collection;

public class CharArgumentType implements ArgumentType<Character> {
    private static final Collection<String> EXAMPLES = Arrays.asList("'\\''", "'\"'", "\"a\"");

    public static CharArgumentType character() {
        return new CharArgumentType();
    }

    public static <S> char getChar(CommandContext<S> context, String name) {
        return context.getArgument(name, Character.class);
    }

    @Override
    public Character parse(final StringReader reader) throws CommandSyntaxException {
        char c = 0;
        if (reader.canRead()) {
            boolean esc = false;
            final char next = reader.peek();
            if (next == '\'') {
                reader.skip();
                if (reader.canRead()) {
                    switch (reader.peek()) {
                        case '\'' -> throw new SimpleCommandExceptionType(Text.literal("Empty character literal")).createWithContext(reader);
                        case '\\' -> {
                            esc = true;
                            reader.skip();
                        }
                        default -> c = reader.read();
                    }
                } else {
                    throw new SimpleCommandExceptionType(
                            Text.literal("Unclosed character literal")).createWithContext(reader);
                }
                if (reader.canRead()) {
                    if (esc) {
                        switch (reader.peek()) {
                            case '0':
                                return '\0';
                            case 't':
                                return '\t';
                            case 'b':
                                return '\b';
                            case 'n':
                                return '\n';
                            case 'r':
                                return '\r';
                            case 'f':
                                return '\f';
                            case '\\':
                            case '\'':
                            case '\"':
                                return reader.read();
                            case 'u':
                                reader.skip();
                                break;
                            default:
                                throw new SimpleCommandExceptionType(
                                        Text.literal("Illegal escape character in character literal")).createWithContext(reader);
                        }
                    } else if (reader.read() == '\'') {
                        return c;
                    } else {
                        throw new SimpleCommandExceptionType(
                                Text.literal("Too many characters in character literal")).createWithContext(reader);
                    }
                } else {
                    throw new SimpleCommandExceptionType(
                            Text.literal("Unclosed character literal")).createWithContext(reader);
                }
                if (reader.canRead(4)) {
                    StringBuilder sb = new StringBuilder(4);
                    for (int i = 0; i < 4; i++) {
                        if (isHex(reader.peek())) {
                            sb.append(reader.read());
                        } else {
                            throw new SimpleCommandExceptionType(
                                    Text.literal("Illegal escape character in character literal")).createWithContext(reader);
                        }
                    }
                    c = (char) Integer.parseInt(sb.toString(), 16);
                } else {
                    throw new SimpleCommandExceptionType(
                            Text.literal("Unclosed character literal")).createWithContext(reader);
                }
                if (reader.canRead()) {
                    if (reader.read() == '\'') {
                        return c;
                    }
                    throw new SimpleCommandExceptionType(
                            Text.literal("Too many characters in character literal")).createWithContext(reader);
                } else {
                    throw new SimpleCommandExceptionType(
                            Text.literal("Unclosed character literal")).createWithContext(reader);
                }
            } else {
                return (char) reader.readInt();
            }
        }
        return c;
    }

    public boolean isHex(char c) {
        return (0x30 <= c && c <= 0x39) || (0x41 <= c && c <= 0x46) || (0x61 <= c && c <= 0x66);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}

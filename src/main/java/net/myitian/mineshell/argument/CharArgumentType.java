package net.myitian.mineshell.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collection;

public class CharArgumentType implements ArgumentType<Character> {
    private static final SimpleCommandExceptionType EMPTY_CHAR_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("mineshell.argument.char.empty"));
    private static final SimpleCommandExceptionType UNCLOSED_CHAR_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("mineshell.argument.char.unclosed"));
    private static final SimpleCommandExceptionType ILLEGAL_ESCAPE_CHAR_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("mineshell.argument.char.illegal_escape"));
    private static final SimpleCommandExceptionType TOO_MANY_CHAR_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("mineshell.argument.char.too_many"));
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
            boolean unicode = false;
            if (reader.peek() == '\'') {
                reader.skip();
                if (reader.canRead()) {
                    switch (reader.peek()) {
                        case '\'' -> throw EMPTY_CHAR_EXCEPTION.createWithContext(reader);
                        case '\\' -> {
                            esc = true;
                            reader.skip();
                        }
                        default -> c = reader.read();
                    }
                } else {
                    throw UNCLOSED_CHAR_EXCEPTION.createWithContext(reader);
                }
                if (reader.canRead()) {
                    if (esc) {
                        switch (reader.read()) {
                            case '0' -> {
                            }
                            case 't' -> c = '\t';
                            case 'b' -> c = '\b';
                            case 'n' -> c = '\n';
                            case 'r' -> c = '\r';
                            case 'f' -> c = '\f';
                            case '\\' -> c = '\\';
                            case '\'' -> c = '\'';
                            case '\"' -> c = '"';
                            case 'u' -> unicode = true;
                            default -> throw ILLEGAL_ESCAPE_CHAR_EXCEPTION.createWithContext(reader);
                        }
                    } else if (reader.read() == '\'') {
                        return c;
                    } else {
                        reader.setCursor(reader.getCursor() - 1);
                        throw TOO_MANY_CHAR_EXCEPTION.createWithContext(reader);
                    }
                } else {
                    throw UNCLOSED_CHAR_EXCEPTION.createWithContext(reader);
                }
                if (unicode) {
                    if (reader.canRead(4)) {
                        StringBuilder sb = new StringBuilder(4);
                        for (int i = 0; i < 4; i++) {
                            if (isHex(reader.peek())) {
                                sb.append(reader.read());
                            } else {
                                throw ILLEGAL_ESCAPE_CHAR_EXCEPTION.createWithContext(reader);
                            }
                        }
                        c = (char) Integer.parseInt(sb.toString(), 16);
                    } else {
                        throw UNCLOSED_CHAR_EXCEPTION.createWithContext(reader);
                    }
                }
                if (reader.canRead()) {
                    if (reader.read() == '\'') {
                        return c;
                    }
                    reader.setCursor(reader.getCursor() - 1);
                    throw TOO_MANY_CHAR_EXCEPTION.createWithContext(reader);
                } else {
                    throw UNCLOSED_CHAR_EXCEPTION.createWithContext(reader);
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

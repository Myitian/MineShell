package net.myitian.mineshell.util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.myitian.mineshell.MineShellMod;
import net.myitian.mineshell.config.Config;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static net.myitian.mineshell.util.SimpleIntegerParser.parseIntWithReturnLength;

class StreamGobbler implements Runnable {
    public static final char ESC = '\u001B';
    public static final char CR = '\r';
    public static final char LF = '\n';
    public static final char Backspace = '\b';
    public static final char Tab = '\t';
    public static final char IND = '\u0084';
    public static final char NEL = '\u0085';
    public static final char HTS = '\u0088';
    public static final char CCH = '\u0094';
    public static final char CSI = '\u009B';
    public static final Identifier FONT_DEFAULT = new Identifier("minecraft:default");
    public static final Identifier FONT_UNIFORM = new Identifier("minecraft:uniform");
    public static final Identifier FONT_ALT = new Identifier("minecraft:alt");
    public static final Identifier FONT_ILLAGERALT = new Identifier("minecraft:illageralt");
    private static final int[] colorTable256 = {
            0x000000, 0x800000, 0x008000, 0x808000, 0x000080, 0x800080, 0x008080, 0xc0c0c0, 0x808080, 0xff0000, 0x00ff00, 0xffff00, 0x0000ff, 0xff00ff, 0x00ffff, 0xffffff,
            0x000000, 0x00005f, 0x000087, 0x0000af, 0x0000d7, 0x0000ff, 0x005f00, 0x005f5f, 0x005f87, 0x005faf, 0x005fd7, 0x005fff, 0x008700, 0x00875f, 0x008787, 0x0087af,
            0x0087d7, 0x0087ff, 0x00af00, 0x00af5f, 0x00af87, 0x00afaf, 0x00afd7, 0x00afff, 0x00d700, 0x00d75f, 0x00d787, 0x00d7af, 0x00d7d7, 0x00d7ff, 0x00ff00, 0x00ff5f,
            0x00ff87, 0x00ffaf, 0x00ffd7, 0x00ffff, 0x5f0000, 0x5f005f, 0x5f0087, 0x5f00af, 0x5f00d7, 0x5f00ff, 0x5f5f00, 0x5f5f5f, 0x5f5f87, 0x5f5faf, 0x5f5fd7, 0x5f5fff,
            0x5f8700, 0x5f875f, 0x5f8787, 0x5f87af, 0x5f87d7, 0x5f87ff, 0x5faf00, 0x5faf5f, 0x5faf87, 0x5fafaf, 0x5fafd7, 0x5fafff, 0x5fd700, 0x5fd75f, 0x5fd787, 0x5fd7af,
            0x5fd7d7, 0x5fd7ff, 0x5fff00, 0x5fff5f, 0x5fff87, 0x5fffaf, 0x5fffd7, 0x5fffff, 0x870000, 0x87005f, 0x870087, 0x8700af, 0x8700d7, 0x8700ff, 0x875f00, 0x875f5f,
            0x875f87, 0x875faf, 0x875fd7, 0x875fff, 0x878700, 0x87875f, 0x878787, 0x8787af, 0x8787d7, 0x8787ff, 0x87af00, 0x87af5f, 0x87af87, 0x87afaf, 0x87afd7, 0x87afff,
            0x87d700, 0x87d75f, 0x87d787, 0x87d7af, 0x87d7d7, 0x87d7ff, 0x87ff00, 0x87ff5f, 0x87ff87, 0x87ffaf, 0x87ffd7, 0x87ffff, 0xaf0000, 0xaf005f, 0xaf0087, 0xaf00af,
            0xaf00d7, 0xaf00ff, 0xaf5f00, 0xaf5f5f, 0xaf5f87, 0xaf5faf, 0xaf5fd7, 0xaf5fff, 0xaf8700, 0xaf875f, 0xaf8787, 0xaf87af, 0xaf87d7, 0xaf87ff, 0xafaf00, 0xafaf5f,
            0xafaf87, 0xafafaf, 0xafafd7, 0xafafff, 0xafd700, 0xafd75f, 0xafd787, 0xafd7af, 0xafd7d7, 0xafd7ff, 0xafff00, 0xafff5f, 0xafff87, 0xafffaf, 0xafffd7, 0xafffff,
            0xd70000, 0xd7005f, 0xd70087, 0xd700af, 0xd700d7, 0xd700ff, 0xd75f00, 0xd75f5f, 0xd75f87, 0xd75faf, 0xd75fd7, 0xd75fff, 0xd78700, 0xd7875f, 0xd78787, 0xd787af,
            0xd787d7, 0xd787ff, 0xd7af00, 0xd7af5f, 0xd7af87, 0xd7afaf, 0xd7afd7, 0xd7afff, 0xd7d700, 0xd7d75f, 0xd7d787, 0xd7d7af, 0xd7d7d7, 0xd7d7ff, 0xd7ff00, 0xd7ff5f,
            0xd7ff87, 0xd7ffaf, 0xd7ffd7, 0xd7ffff, 0xff0000, 0xff005f, 0xff0087, 0xff00af, 0xff00d7, 0xff00ff, 0xff5f00, 0xff5f5f, 0xff5f87, 0xff5faf, 0xff5fd7, 0xff5fff,
            0xff8700, 0xff875f, 0xff8787, 0xff87af, 0xff87d7, 0xff87ff, 0xffaf00, 0xffaf5f, 0xffaf87, 0xffafaf, 0xffafd7, 0xffafff, 0xffd700, 0xffd75f, 0xffd787, 0xffd7af,
            0xffd7d7, 0xffd7ff, 0xffff00, 0xffff5f, 0xffff87, 0xffffaf, 0xffffd7, 0xffffff, 0x080808, 0x121212, 0x1c1c1c, 0x262626, 0x303030, 0x3a3a3a, 0x444444, 0x4e4e4e,
            0x585858, 0x626262, 0x6c6c6c, 0x767676, 0x808080, 0x8a8a8a, 0x949494, 0x9e9e9e, 0xa8a8a8, 0xb2b2b2, 0xbcbcbc, 0xc6c6c6, 0xd0d0d0, 0xdadada, 0xe4e4e4, 0xeeeeee,
    };
    private final InputStream is;
    private final String charset;
    private final CommandContext<ServerCommandSource> ctx;
    private final Config config;
    private Style style = Style.EMPTY;
    private StringBuilder text;
    private MutableText mcText;
    private int linePos = 0, prev = -2, c = 0;
    private EscapeType escape = EscapeType.NONE;
    private boolean escaped;

    StreamGobbler(InputStream is, CommandContext<ServerCommandSource> ctx, String charset, Config config) {
        this.is = is;
        this.ctx = ctx;
        this.charset = charset;
        this.config = config;
    }

    public static int appendTab(StringBuilder text, int linePos, Config config) {
        if (config.tabWidth <= 0)
            return 0;
        int count = config.tabWidth - linePos % config.tabWidth;
        text.append(String.valueOf(config.tabChar).repeat(Math.max(0, count)));
        return count;
    }

    public static int get8bitColor(int index) {
        if (index < 0 || index > 255)
            return -1;
        return colorTable256[index];
    }

    public void flush() {
        if (text.isEmpty())
            return;
        ctx.getSource().sendMessage(mcText.append(Text.literal(text.toString()).setStyle(style)));
        mcText = Text.empty();
        text.setLength(0);
        linePos = 0;
    }

    private void processSGR(int parameterLength, StringBuilder escapeSeq) {
        mcText.append(Text.literal(text.toString()).setStyle(style));
        text.setLength(0);
        SGRParserState state = SGRParserState.BASIC;
        int r = 0, g = 0, b;
        for (int i = 0; i < parameterLength; i++) {
            var parseResult = parseIntWithReturnLength(escapeSeq, i, parameterLength);
            if (parseResult.getRight() == null)
                if (escapeSeq.charAt(i) == ';')
                    parseResult.setRight(0);
                else
                    break;
            i = parseResult.getLeft();
            int number = parseResult.getRight();
            switch (state) {
                case BASIC -> {
                    switch (number) {
                        case 0 -> // Reset
                                style = Style.EMPTY;
                        case 1 -> // Bold
                                style = style.withBold(true);
                        case 3 -> // Italic
                                style = style.withItalic(true);
                        case 4 -> // Underline
                                style = style.withUnderline(true);
                        case 9 -> // Strikethrough
                                style = style.withStrikethrough(true);
                        case 10 -> // Primary font
                                style = style.withFont(FONT_DEFAULT);
                        case 11 -> // Alternative font 1
                                style = style.withFont(FONT_UNIFORM);
                        case 12 -> // Alternative font 2
                                style = style.withFont(FONT_ALT);
                        case 13 -> // Alternative font 3
                                style = style.withFont(FONT_ILLAGERALT);
                        case 22 -> // Not bold
                                style = style.withBold(false);
                        case 23 -> // Not italic
                                style = style.withItalic(false);
                        case 24 -> // Not underlined
                                style = style.withUnderline(false);
                        case 29 -> // Not strikethrough
                                style = style.withStrikethrough(false);
                        case 30 -> // Set foreground color 0
                                style = style.withColor(Formatting.BLACK);
                        case 31 -> // Set foreground color 1
                                style = style.withColor(Formatting.DARK_RED);
                        case 32 -> // Set foreground color 2
                                style = style.withColor(Formatting.DARK_GREEN);
                        case 33 -> // Set foreground color 3
                                style = style.withColor(Formatting.GOLD);
                        case 34 -> // Set foreground color 4
                                style = style.withColor(Formatting.DARK_BLUE);
                        case 35 -> // Set foreground color 5
                                style = style.withColor(Formatting.DARK_PURPLE);
                        case 36 -> // Set foreground color 6
                                style = style.withColor(Formatting.DARK_AQUA);
                        case 37 -> // Set foreground color 7
                                style = style.withColor(Formatting.GRAY);
                        case 38 -> // Set foreground color
                                state = SGRParserState.COLOR;
                        case 39 -> // Default foreground color
                                style = style.withColor((TextColor) null);
                        case 90 -> // Set bright foreground color 0
                                style = style.withColor(Formatting.DARK_GRAY);
                        case 91 -> // Set bright foreground color 1
                                style = style.withColor(Formatting.RED);
                        case 92 -> // Set bright foreground color 2
                                style = style.withColor(Formatting.GREEN);
                        case 93 -> // Set bright foreground color 3
                                style = style.withColor(Formatting.YELLOW);
                        case 94 -> // Set bright foreground color 4
                                style = style.withColor(Formatting.BLUE);
                        case 95 -> // Set bright foreground color 5
                                style = style.withColor(Formatting.LIGHT_PURPLE);
                        case 96 -> // Set bright foreground color 6
                                style = style.withColor(Formatting.AQUA);
                        case 97 -> // Set bright foreground color 7
                                style = style.withColor(Formatting.WHITE);
                    }
                }
                case COLOR -> state = switch (number) {
                    case 2 -> SGRParserState.COLOR_24BIT_R;
                    case 5 -> SGRParserState.COLOR_8BIT;
                    default -> SGRParserState.BASIC;
                };
                case COLOR_8BIT -> {
                    int color8 = get8bitColor(number);
                    if (color8 != -1)
                        style = style.withColor(color8);
                    state = SGRParserState.BASIC;
                }
                case COLOR_24BIT_R -> {
                    state = SGRParserState.COLOR_24BIT_G;
                    r = number < 0 || number > 255 ? -1 : number;
                }
                case COLOR_24BIT_G -> {
                    state = SGRParserState.COLOR_24BIT_B;
                    g = number < 0 || number > 255 ? -1 : number;
                }
                case COLOR_24BIT_B -> {
                    state = SGRParserState.BASIC;
                    b = number < 0 || number > 255 ? -1 : number;
                    if (r != -1 && g != -1 && b != -1) {
                        int color24 = (r << 16) | (g << 8) | b;
                        style = style.withColor(color24);
                    }
                }
            }
        }
    }

    private BreakType processEscape(StringBuilder escapeSeq, BufferedReader br) throws IOException {
        switch (escape) {
            case CRLF -> {
                escape = EscapeType.NONE;
                if (c == LF)
                    return BreakType.READ_LINE;
                else if (!Boolean.FALSE.equals(config.isCREnabled)) {
                    if (Boolean.TRUE.equals(config.isCREnabled)) {
                        prev = c;
                        return BreakType.READ_LINE;
                    }
                } else {
                    text.append(CR);
                    linePos++;
                }
            }
            case ESC -> {
                escape = EscapeType.NONE;
                if (c < '@' || c > '_') {
                    if (config.hideUnsupportedANSIEscapeSequence)
                        return BreakType.CONTINUE;
                    else {
                        text.append(ESC);
                        linePos++;
                    }
                } else {
                    c += 64;
                    escaped = true;
                }
            }
            case CSI -> {
                escape = EscapeType.NONE;
                int parameterLength = 0;
                escapeSeq.setLength(0);
                while (c >= 0x30 && c <= 0x3F) {
                    escapeSeq.append((char) c);
                    parameterLength++;
                    c = br.read();
                }
                while (c >= 0x20 && c <= 0x2F) {
                    escapeSeq.append((char) c);
                    c = br.read();
                }
                CSIMode csi = CSIMode.createCSIMode((char) c);
                switch (csi) {
                    case CNL -> {
                        if (config.isCSICNLEnabled == null)
                            return BreakType.NORMAL;
                        if (Boolean.TRUE.equals(config.isCSICNLEnabled))
                            return BreakType.READ_LINE;
                    }
                    case SGR -> {
                        if (config.isCSISGREnabled == null)
                            return BreakType.NORMAL;
                        if (Boolean.TRUE.equals(config.isCSISGREnabled)) {
                            processSGR(parameterLength, escapeSeq);
                            return BreakType.CONTINUE;
                        }
                    }
                }
                if (!config.hideUnsupportedANSIEscapeSequence)
                    text.append(escapeSeq);
            }
        }
        return BreakType.NORMAL;
    }

    private BreakType processChar(boolean canEscape) {
        switch (c) {
            case -1 -> {
                return BreakType.READ_LINE;
            }
            case CR -> {
                if (config.isCRLFEnabled) {
                    escape = EscapeType.CRLF;
                    return BreakType.CONTINUE;
                } else if (!Boolean.FALSE.equals(config.isCREnabled)) {
                    if (Boolean.TRUE.equals(config.isCREnabled))
                        return BreakType.READ_LINE;
                    return BreakType.CONTINUE;
                }
            }
            case LF -> {
                if (!Boolean.FALSE.equals(config.isLFEnabled)) {
                    if (Boolean.TRUE.equals(config.isLFEnabled))
                        return BreakType.READ_LINE;
                    return BreakType.CONTINUE;
                }
            }
            case Backspace -> {
                if (!Boolean.FALSE.equals(config.isBackspaceEnabled)) {
                    if (Boolean.TRUE.equals(config.isBackspaceEnabled) && !text.isEmpty())
                        text.setLength(text.length() - 1);
                    return BreakType.CONTINUE;
                }
            }
            case Tab -> {
                if (!Boolean.FALSE.equals(config.isTabEnabled)) {
                    if (Boolean.TRUE.equals(config.isTabEnabled))
                        linePos += appendTab(text, linePos, config);
                    return BreakType.CONTINUE;
                }
            }
            case ESC -> {
                if (config.isANSIEscapeEnabled) {
                    escape = EscapeType.ESC;
                    return BreakType.CONTINUE;
                }
            }
            case IND -> {
                if (canEscape && !Boolean.FALSE.equals(config.isINDEnabled)) {
                    if (Boolean.TRUE.equals(config.isINDEnabled))
                        return BreakType.READ_LINE;
                    return BreakType.CONTINUE;
                }
            }
            case NEL -> {
                if (canEscape && !Boolean.FALSE.equals(config.isNELEnabled)) {
                    if (Boolean.TRUE.equals(config.isNELEnabled))
                        return BreakType.READ_LINE;
                    return BreakType.CONTINUE;
                }
            }
            case HTS -> {
                if (canEscape && !Boolean.FALSE.equals(config.isHTSEnabled)) {
                    if (Boolean.TRUE.equals(config.isHTSEnabled))
                        linePos += appendTab(text, linePos, config);
                    return BreakType.CONTINUE;
                }
            }
            case CCH -> {
                if (canEscape && !Boolean.FALSE.equals(config.isCCHEnabled)) {
                    if (Boolean.TRUE.equals(config.isCCHEnabled) && !text.isEmpty()) {
                        text.setLength(text.length() - 1);
                        linePos--;
                    }
                    return BreakType.CONTINUE;
                }
            }
            case CSI -> {
                if (canEscape && !Boolean.FALSE.equals(config.isCSIEnabled)) {
                    if (Boolean.TRUE.equals(config.isCSIEnabled))
                        escape = EscapeType.CSI;
                    return BreakType.CONTINUE;
                }
            }
        }
        return BreakType.NORMAL;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is, charset);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder escapeSeq = new StringBuilder();
            text = new StringBuilder();
            do {
                linePos = 0;
                mcText = Text.empty();
                text.setLength(0);
                READ_LINE:
                for (; ; ) {
                    if (prev == -2)
                        c = br.read();
                    else
                        prev = -2;
                    escaped = false;
                    switch (processEscape(escapeSeq, br)) {
                        case READ_LINE -> {
                            break READ_LINE;
                        }
                        case CONTINUE -> {
                            continue;
                        }
                    }
                    switch (processChar(escaped || config.isUnicodeC1Enabled)) {
                        case READ_LINE -> {
                            break READ_LINE;
                        }
                        case CONTINUE -> {
                            continue;
                        }
                    }
                    if (escaped && config.hideUnsupportedANSIEscapeSequence)
                        continue;
                    text.append((char) c);
                    linePos++;
                }
                if (linePos > 0 || (config.isEmptyNewLineEnabled && c != -1))
                    ctx.getSource().sendMessage(mcText.append(Text.literal(text.toString()).setStyle(style)));
            } while (c != -1);
            br.close();
            isr.close();
        } catch (Exception e) {
            ctx.getSource().sendError(Text.literal(
                    e.getClass().getSimpleName() + ": " + e.getLocalizedMessage()));
            MineShellMod.LOGGER.error(e.getMessage());
        }
    }

    public @Nullable Text getUnsentMessage() {
        if (mcText == null || mcText.getString().length() + text.length() == 0)
            return null;
        return Text.empty().append(mcText).append(Text.literal(text.toString()).setStyle(style));
    }

    private enum BreakType {
        NORMAL,
        READ_LINE,
        CONTINUE
    }
}

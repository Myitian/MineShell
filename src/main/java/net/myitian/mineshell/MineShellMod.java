package net.myitian.mineshell;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.myitian.mineshell.argument.CharArgumentType;
import net.myitian.mineshell.argument.ExtendBoolArgumentSerializer;
import net.myitian.mineshell.argument.ExtendBoolArgumentType;
import net.myitian.mineshell.util.ProcessManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.myitian.mineshell.argument.ExtendBoolArgumentType.exBool;
import static net.myitian.mineshell.argument.ExtendBoolArgumentType.getBoolean;

public class MineShellMod implements ModInitializer {
    public static final String MODID = "mineshell";
    public static final String CMD = "shell";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final String CMDPREFIX_WINDOWS = "cmd /C ";
    public static final String CMDPREFIX_NONWINDOWS = "/bin/sh -c ";
    public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");
    public static final String CMDPREFIX = IS_WINDOWS ? CMDPREFIX_WINDOWS : CMDPREFIX_NONWINDOWS;

    @Override
    public void onInitialize() {
        ArgumentTypeRegistry.registerArgumentType(new Identifier("mineshell:char"),
                CharArgumentType.class, ConstantArgumentSerializer.of(CharArgumentType::character));
        ArgumentTypeRegistry.registerArgumentType(new Identifier("mineshell:bool"),
                ExtendBoolArgumentType.class, new ExtendBoolArgumentSerializer());
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal(CMD)
                .requires((source) -> source.hasPermissionLevel(4))
                .then(literal("runcmd")
                        .then(argument("cmd", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    try {
                                        String cmd = StringArgumentType.getString(ctx, "cmd");
                                        ProcessManager.execAsync(ctx, CMDPREFIX + cmd);
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                        ctx.getSource().sendError(Text.literal(e.getMessage()));
                                    }
                                    return 1;
                                })))
                .then(literal("run")
                        .then(argument("cmd", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    try {
                                        String cmd = StringArgumentType.getString(ctx, "cmd");
                                        ProcessManager.execAsync(ctx, cmd);
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                        ctx.getSource().sendError(Text.literal(e.getMessage()));
                                    }
                                    return SINGLE_SUCCESS;
                                })))
                .then(literal("kill").executes(ctx -> {
                    ProcessManager.kill(ctx);
                    return SINGLE_SUCCESS;
                }))
                .then(literal("info").executes(ctx -> {
                    ProcessManager.info(ctx);
                    return SINGLE_SUCCESS;
                }))
                .then(literal("flushout").executes(ctx -> {
                    ProcessManager.flushOutput(ctx);
                    return SINGLE_SUCCESS;
                }))
                .then(literal("help").executes(ctx -> {
                    // WIP
                    ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.help.main.title", "===== MineShell Help [WIP] ====="));
                    ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.help.main.line.1", " 1. run"));
                    ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.help.main.line.2", " 2. runcmd"));
                    ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.help.main.line.3", " 3. kill"));
                    ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.help.main.line.4", " 4. info"));
                    ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.help.main.line.5", " 5. flushout"));
                    ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.help.main.line.6", " 6. isalive"));
                    ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.help.main.line.7", " 7. input"));
                    ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.help.main.line.8", " 8. config"));
                    ctx.getSource().sendMessage(
                            Text.translatableWithFallback(
                                            "mineshell.help.main.extra",
                                            "See https://github.com/Myitian/MineShell for more information.")
                                    .setStyle(Style.EMPTY
                                            .withBold(true)
                                            .withUnderline(true)
                                            .withClickEvent(new ClickEvent(
                                                    ClickEvent.Action.OPEN_URL,
                                                    "https://github.com/Myitian/MineShell"))));
                    return SINGLE_SUCCESS;
                }))
                .then(literal("isalive").executes(ctx -> {
                    boolean b = ProcessManager.isAlive(ctx);
                    return b ? 1 : 0;
                }))
                .then(literal("input")
                        .then(literal("char")
                                .then(argument("char", CharArgumentType.character())
                                        .executes(ctx -> {
                                            try {
                                                ProcessManager.inputChar(ctx, CharArgumentType.getChar(ctx, "char"));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                ctx.getSource().sendError(Text.literal(e.getMessage()));
                                            }
                                            return SINGLE_SUCCESS;
                                        })))
                        .then(literal("string")
                                .then(argument("string", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            try {
                                                ProcessManager.inputString(ctx, StringArgumentType.getString(ctx, "string"));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                ctx.getSource().sendError(Text.literal(e.getMessage()));
                                            }
                                            return SINGLE_SUCCESS;
                                        })))
                        .then(literal("line")
                                .then(argument("line", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            try {
                                                ProcessManager.inputLine(ctx, StringArgumentType.getString(ctx, "line"));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                ctx.getSource().sendError(Text.literal(e.getMessage()));
                                            }
                                            return SINGLE_SUCCESS;
                                        })))
                        .then(literal("newline")
                                .executes(ctx -> {
                                    try {
                                        ProcessManager.inputNewLine(ctx);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        ctx.getSource().sendError(Text.literal(e.getMessage()));
                                    }
                                    return SINGLE_SUCCESS;
                                })))
                .then(literal("config")
                        .then(literal("reload").executes(ctx -> {
                            ProcessManager.CONFIG.load();
                            return SINGLE_SUCCESS;
                        }))
                        .then(literal("save").executes(ctx -> {
                            ProcessManager.CONFIG.save();
                            return SINGLE_SUCCESS;
                        }))
                        .then(literal("tab-width")
                                .then(argument("value", integer(0))
                                        .executes(ctx -> {
                                            ProcessManager.CONFIG.tabWidth = getInteger(ctx, "value");
                                            return SINGLE_SUCCESS;
                                        })))
                        .then(literal("input-charset")
                                .then(argument("value", word())
                                        .executes(ctx -> {
                                            ProcessManager.CONFIG.inputCharset = getString(ctx, "value");
                                            return SINGLE_SUCCESS;
                                        })))
                        .then(literal("output-charset")
                                .then(argument("value", word())
                                        .executes(ctx -> {
                                            ProcessManager.CONFIG.outputCharset = getString(ctx, "value");
                                            return SINGLE_SUCCESS;
                                        })))
                        .then(literal("ansi-escape")
                                .then(argument("isEnabled", exBool("disable", "enable"))
                                        .executes(ctx -> {
                                            ProcessManager.CONFIG.isANSIEscapeEnabled = getBoolean(ctx, "isEnabled");
                                            return SINGLE_SUCCESS;
                                        }))))));
    }
}

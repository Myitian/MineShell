package net.myitian.mineshell;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.myitian.mineshell.argument.*;
import net.myitian.mineshell.config.Config;
import net.myitian.mineshell.util.ProcessManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.function.BiFunction;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.myitian.mineshell.argument.ExtendBooleanArgumentType.exBool;
import static net.myitian.mineshell.argument.NullableBooleanArgumentType.bool3;

public class MineShellMod implements ModInitializer, ServerWorldEvents.Load, ServerWorldEvents.Unload {
    public static final String MODID = "mineshell";
    public static final String CMD = "shell";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final String CMDPREFIX_WINDOWS = "cmd /C ";
    public static final String CMDPREFIX_NONWINDOWS = "/bin/sh -c ";
    public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");
    public static final String CMDPREFIX = IS_WINDOWS ? CMDPREFIX_WINDOWS : CMDPREFIX_NONWINDOWS;

    static final Text NEWLINE = Text.literal("\n");

    @Override
    public void onInitialize() {
        ArgumentTypeRegistry.registerArgumentType(new Identifier("mineshell:char"),
                CharArgumentType.class, ConstantArgumentSerializer.of(CharArgumentType::character));
        ArgumentTypeRegistry.registerArgumentType(new Identifier("mineshell:bool"),
                ExtendBooleanArgumentType.class, new ExtendBooleanArgumentSerializer());
        ArgumentTypeRegistry.registerArgumentType(new Identifier("mineshell:bool3"),
                NullableBooleanArgumentType.class, new NullableBooleanArgumentSerializer());
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal(CMD)
                .requires((source) -> source.hasPermissionLevel(4))
                .then(literal("runcmd")
                        .then(argument("cmd", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    try {
                                        String cmd = getString(ctx, "cmd");
                                        ProcessManager.execAsync(ctx, CMDPREFIX + cmd);
                                        return SINGLE_SUCCESS;
                                    } catch (Exception e) {
                                        ctx.getSource().sendError(Text.literal(
                                                e.getClass().getSimpleName() + ": " + e.getLocalizedMessage()));
                                        MineShellMod.LOGGER.error(e.getMessage());
                                        return 0;
                                    }
                                })))
                .then(literal("run")
                        .then(argument("cmd", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    try {
                                        String cmd = getString(ctx, "cmd");
                                        ProcessManager.execAsync(ctx, cmd);
                                        return SINGLE_SUCCESS;
                                    } catch (Exception e) {
                                        ctx.getSource().sendError(Text.literal(
                                                e.getClass().getSimpleName() + ": " + e.getLocalizedMessage()));
                                        MineShellMod.LOGGER.error(e.getMessage());
                                        return 0;
                                    }
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
                    String[] lines = {
                            "run",
                            "runcmd",
                            "kill",
                            "info",
                            "flushout",
                            "isalive",
                            "input",
                            "config"
                    };
                    MutableText text = Text.empty().append(Text.translatableWithFallback(
                            "mineshell.help.main.title",
                            "===== MineShell Help [WIP] ====="));
                    for (int i = 0; i < lines.length; i++)
                        text.append("\n " + i + ". " + lines[i]);
                    text.append(NEWLINE).append(Text.translatableWithFallback(
                                    "mineshell.help.main.github",
                                    "GitHub: %s",
                                    Text.literal("https://github.com/Myitian/MineShell").setStyle(Style.EMPTY
                                            .withBold(true)
                                            .withUnderline(true)
                                            .withClickEvent(new ClickEvent(
                                                    ClickEvent.Action.OPEN_URL,
                                                    "https://github.com/Myitian/MineShell")))))
                            .append(NEWLINE).append(Text.translatableWithFallback(
                                    "mineshell.help.main.mcmod",
                                    "MCMOD: %s",
                                    Text.literal("https://www.mcmod.cn/class/8929.html").setStyle(Style.EMPTY
                                            .withBold(true)
                                            .withUnderline(true)
                                            .withClickEvent(new ClickEvent(
                                                    ClickEvent.Action.OPEN_URL,
                                                    "https://www.mcmod.cn/class/8929.html")))));
                    ctx.getSource().sendMessage(text);
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
                                                return SINGLE_SUCCESS;
                                            } catch (Exception e) {
                                                ctx.getSource().sendError(Text.literal(
                                                        e.getClass().getSimpleName() + ": " + e.getLocalizedMessage()));
                                                MineShellMod.LOGGER.error(e.getMessage());
                                                return 0;
                                            }
                                        })))
                        .then(literal("string")
                                .then(argument("string", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            try {
                                                ProcessManager.inputString(ctx, getString(ctx, "string"));
                                                return SINGLE_SUCCESS;
                                            } catch (Exception e) {
                                                ctx.getSource().sendError(Text.literal(
                                                        e.getClass().getSimpleName() + ": " + e.getLocalizedMessage()));
                                                MineShellMod.LOGGER.error(e.getMessage());
                                                return 0;
                                            }
                                        })))
                        .then(literal("line")
                                .then(argument("line", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            try {
                                                ProcessManager.inputLine(ctx, getString(ctx, "line"));
                                                return SINGLE_SUCCESS;
                                            } catch (Exception e) {
                                                ctx.getSource().sendError(Text.literal(
                                                        e.getClass().getSimpleName() + ": " + e.getLocalizedMessage()));
                                                MineShellMod.LOGGER.error(e.getMessage());
                                                return 0;
                                            }
                                        })))
                        .then(literal("newline")
                                .executes(ctx -> {
                                    try {
                                        ProcessManager.inputNewLine(ctx);
                                        return SINGLE_SUCCESS;
                                    } catch (Exception e) {
                                        ctx.getSource().sendError(Text.literal(
                                                e.getClass().getSimpleName() + ": " + e.getLocalizedMessage()));
                                        MineShellMod.LOGGER.error(e.getMessage());
                                        return 0;
                                    }
                                })))
                .then(literal("config")
                        .then(literal("reload").executes(ctx -> {
                            ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.config.reload", "[MSH] Reloading!"));
                            ProcessManager.CONFIG.load();
                            return SINGLE_SUCCESS;
                        }))
                        .then(literal("save").executes(ctx -> {
                            ProcessManager.CONFIG.save();
                            ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.config.save", "[MSH] Saved!"));
                            return SINGLE_SUCCESS;
                        }))
                        .then(config(
                                "TabWidth",
                                "tabWidth",
                                "value",
                                integer(0),
                                IntegerArgumentType::getInteger))
                        .then(config(
                                "InputCharset",
                                "inputCharset",
                                "value",
                                string(),
                                StringArgumentType::getString))
                        .then(config(
                                "OutputCharset",
                                "outputCharset",
                                "value",
                                string(),
                                StringArgumentType::getString))
                        .then(config(
                                "CarriageReturn",
                                "isCREnabled",
                                "isEnabled",
                                bool3("disable", "enable", "ignored"),
                                NullableBooleanArgumentType::getBoolean))
                        .then(config(
                                "ANSIEscape",
                                "isANSIEscapeEnabled",
                                "isEnabled",
                                exBool("disable", "enable"),
                                ExtendBooleanArgumentType::getBoolean))
                        .then(config(
                                "UnicodeC1",
                                "isUnicodeC1Enabled",
                                "isEnabled",
                                exBool("disable", "enable"),
                                ExtendBooleanArgumentType::getBoolean))
                        .then(config(
                                "EmptyNewLine",
                                "isEmptyNewLineEnabled",
                                "isEnabled",
                                exBool("disable", "enable"),
                                ExtendBooleanArgumentType::getBoolean)))));
    }

    public <T> LiteralArgumentBuilder<ServerCommandSource> config(
            String name,
            String fieldName,
            String argName,
            ArgumentType<?> arg,
            BiFunction<CommandContext<ServerCommandSource>, String, T> getValue) {
        Field field;
        try {
            field = Config.class.getField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return literal(name)
                .then(argument(argName, arg)
                        .executes(ctx -> {
                            try {
                                T value = getValue.apply(ctx, argName);
                                field.set(ProcessManager.CONFIG, value);
                                ctx.getSource().sendMessage(Text.translatableWithFallback(
                                        "mineshell.config.set",
                                        "Set the value of %s to %s",
                                        fieldName,
                                        value));
                                return SINGLE_SUCCESS;
                            } catch (Exception e) {
                                ctx.getSource().sendError(Text.literal(
                                        e.getClass().getSimpleName() + ": " + e.getLocalizedMessage()));
                                MineShellMod.LOGGER.error(e.getMessage());
                                return 0;
                            }
                        }))
                .executes(ctx -> {
                    try {
                        ctx.getSource().sendMessage(Text.translatableWithFallback(
                                "mineshell.config.get",
                                "The value of %s is %s",
                                fieldName,
                                field.get(ProcessManager.CONFIG)));
                        return SINGLE_SUCCESS;
                    } catch (Exception e) {
                        ctx.getSource().sendError(Text.literal(
                                e.getClass().getSimpleName() + ": " + e.getLocalizedMessage()));
                        MineShellMod.LOGGER.error(e.getMessage());
                        return 0;
                    }
                });
    }

    @Override
    public void onWorldLoad(MinecraftServer server, ServerWorld world) {
        ProcessManager.CONFIG.load().save();
    }

    @Override
    public void onWorldUnload(MinecraftServer server, ServerWorld world) {
        ProcessManager.CONFIG.save();
        ProcessManager.kill();
    }
}

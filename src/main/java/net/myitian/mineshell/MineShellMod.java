package net.myitian.mineshell;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.myitian.util.ExecRunner;
import net.myitian.util.ProcManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MineShellMod implements ModInitializer {
    public static final String MODID = "mineshell";
    public static final String CMD = "shell";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final String CMDPREFIX_WINDOWS = "cmd /C ";
    public static final String CMDPREFIX_NONWINDOWS = "/bin/sh -c ";
    public static final String CMDPREFIX = ExecRunner.isWindows ? CMDPREFIX_WINDOWS : CMDPREFIX_NONWINDOWS;

    @Override
    public void onInitialize() {
        ArgumentTypeRegistry.registerArgumentType(new Identifier("mineshell", "char"),
                CharArgumentType.class, ConstantArgumentSerializer.of(CharArgumentType::character));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal(CMD)
                .requires((source) -> source.hasPermissionLevel(4))
                .then(literal("run")
                        .then(argument("cmd", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    try {
                                        String cmd = StringArgumentType.getString(ctx, "cmd");
                                        ProcManager.execAsync(ctx, CMDPREFIX + cmd);
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                        ctx.getSource().sendError(Text.literal(e.getMessage()));
                                    }
                                    return 1;
                                })))
                .then(literal("runexecutable")
                        .then(argument("cmd", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    try {
                                        String cmd = StringArgumentType.getString(ctx, "cmd");
                                        ProcManager.execAsync(ctx, cmd);
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                        ctx.getSource().sendError(Text.literal(e.getMessage()));
                                    }
                                    return 1;
                                })))
                .then(literal("kill").executes(ctx -> {
                    ProcManager.kill(ctx);
                    return 1;
                }))
                .then(literal("info").executes(ctx -> {
                    ProcManager.info(ctx);
                    return 1;
                }))
                .then(literal("help")
                        .executes(ctx -> {
                            // TODO
                            return 1;
                        }))
                .then(literal("isalive").executes(ctx -> {
                    ProcManager.isalive(ctx);
                    return 1;
                }))
                .then(literal("input")
                        .then(literal("char")
                                .then(argument("char", CharArgumentType.character())
                                        //.suggests(suggestionProvider)
                                        .executes(ctx -> {
                                            try {
                                                ProcManager.inputChar(ctx, CharArgumentType.getChar(ctx, "char"));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                ctx.getSource().sendError(Text.literal(e.getMessage()));
                                            }
                                            return 1;
                                        })))
                        .then(literal("string")
                                .then(argument("string", StringArgumentType.greedyString())
                                        //.suggests(suggestionProvider)
                                        .executes(ctx -> {
                                            try {
                                                ProcManager.inputString(ctx, StringArgumentType.getString(ctx, "string"));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                ctx.getSource().sendError(Text.literal(e.getMessage()));
                                            }
                                            return 1;
                                        })))
                        .then(literal("line")
                                .then(argument("line", StringArgumentType.greedyString())
                                        //.suggests(suggestionProvider)
                                        .executes(ctx -> {
                                            try {
                                                ProcManager.inputLine(ctx, StringArgumentType.getString(ctx, "line"));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                ctx.getSource().sendError(Text.literal(e.getMessage()));
                                            }
                                            return 1;
                                        })))
                        .then(literal("newline")
                                .executes(ctx -> {
                                    try {
                                        ProcManager.inputNewLine(ctx);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        ctx.getSource().sendError(Text.literal(e.getMessage()));
                                    }
                                    return 1;
                                })))));
    }
}

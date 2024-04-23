package net.myitian.mineshell.util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.myitian.mineshell.config.Config;

import java.io.IOException;
import java.io.Writer;

public class ProcessManager {
    public static final Config CONFIG = new Config();
    private static ExecRunner runner;

    public static void execAsync(CommandContext<ServerCommandSource> ctx, String arg) {
        if (runner != null) {
            Process process = runner.getProcess();
            if (process != null && process.isAlive()) {
                ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.process_manager.error.process_already_running", "There is already a process running!"));
                return;
            }
        }
        runner = new ExecRunner(ctx, arg, CONFIG);
        runner.start();
    }

    public static void kill() {
        if (runner != null) {
            Process process = runner.getProcess();
            if (process != null && process.isAlive()) {
                kill(process.toHandle());
            }
        }
    }

    public static void kill(CommandContext<ServerCommandSource> ctx) {
        if (runner != null) {
            Process process = runner.getProcess();
            if (process != null && process.isAlive()) {
                kill(process.toHandle());
                ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.kill", "[MSH:Ending the process]"));
                return;
            }
        }
        ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.process_manager.error.no_running_process", "No running process!"));
    }

    public static void kill(ProcessHandle handle) {
        handle.descendants().forEach(ProcessManager::kill);
        handle.destroy();
    }

    public static void info(CommandContext<ServerCommandSource> ctx) {
        if (runner != null) {
            Process process = runner.getProcess();
            if (process != null && process.isAlive()) {
                ProcessHandle.Info info = process.info();
                ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.info.command", "Command: %s", info.command().orElse("")));
                if (info.arguments().isEmpty() || info.arguments().get().length == 0)
                    ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.info.no_arguments", "No arguments"));
                else {
                    var args = info.arguments().get();
                    ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.info.arguments", "Arguments (%s):", args.length));
                    for (String arg : args)
                        ctx.getSource().sendMessage(Text.literal(arg));
                }
                ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.info.pid", "PID: %s", process.pid()));
                return;
            }
        }
        ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.process_manager.error.no_running_process", "No running process!"));
    }

    public static boolean isAlive(CommandContext<ServerCommandSource> ctx) {
        if (runner != null) {
            Process process = runner.getProcess();
            if (process != null && process.isAlive()) {
                ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.process_manager.isalive.alive", "Alive"));
                return true;
            }
        }
        ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.process_manager.isalive.unlive", "Unlive"));
        return false;
    }

    public static void flushOutput(CommandContext<ServerCommandSource> ctx) {
        if (runner != null) {
            Process process = runner.getProcess();
            if (process != null && process.isAlive()) {
                if (runner.outputGobbler != null) {
                    runner.outputGobbler.flush();
                }
                if (runner.errorGobbler != null) {
                    runner.errorGobbler.flush();
                }
            }
        }
        ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.process_manager.error.no_running_process", "No running process!"));
    }

    public static void inputChar(CommandContext<ServerCommandSource> ctx, char c) throws IOException {
        if (runner != null) {
            Writer writer = runner.getWriter();
            if (writer != null) {
                writer.write(c);
                writer.flush();
                return;
            }
        }
        ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.process_manager.error.no_running_process", "No running process!"));
    }

    public static void inputString(CommandContext<ServerCommandSource> ctx, String str) throws IOException {
        if (runner != null) {
            Writer writer = runner.getWriter();
            if (writer != null) {
                writer.write(str);
                writer.flush();
                return;
            }
        }
        ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.process_manager.error.no_running_process", "No running process!"));
    }

    public static void inputLine(CommandContext<ServerCommandSource> ctx, String str) throws IOException {
        if (runner != null) {
            Writer writer = runner.getWriter();
            if (writer != null) {
                writer.write(str);
                writer.write(CONFIG.newLine);
                writer.flush();
                return;
            }
        }
        ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.process_manager.error.no_running_process", "No running process!"));
    }

    public static void inputNewLine(CommandContext<ServerCommandSource> ctx) throws IOException {
        if (runner != null) {
            Writer writer = runner.getWriter();
            if (writer != null) {
                writer.write(CONFIG.newLine);
                writer.flush();
                return;
            }
        }
        ctx.getSource().sendMessage(Text.translatableWithFallback("mineshell.process_manager.error.no_running_process", "No running process!"));
    }

    public static ExecRunner getRunner() {
        return runner;
    }
}

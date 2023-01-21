package net.myitian.util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.io.BufferedWriter;
import java.io.IOException;

public class ProcManager {

    private static ExecRunner runner;

    public static void execAsync(CommandContext<ServerCommandSource> ctx, String arg) {
        if (runner != null) {
            Process proc = runner.getProc();
            if (proc != null) {
                if (proc.isAlive()) {
                    ctx.getSource().sendMessage(Text.translatable("procmanager.error.process_already_running"));
                    return;
                }
            }
        }
        runner = new ExecRunner(ctx, arg);
        runner.start();
    }

    public static void kill(CommandContext<ServerCommandSource> ctx) {
        if (runner != null) {
            Process proc = runner.getProc();
            if (proc != null && proc.isAlive()) {
                kill(proc.toHandle());
                ctx.getSource().sendMessage(Text.translatable("mineshell.kill_process"));
                return;
            }
        }
        ctx.getSource().sendMessage(Text.translatable("procmanager.error.no_running_process"));
    }

    public static void kill(ProcessHandle handle) {
        handle.descendants().forEach(ProcManager::kill);
        handle.destroy();
    }

    public static void help(CommandContext<ServerCommandSource> ctx) {
        // TODO
    }

    public static void info(CommandContext<ServerCommandSource> ctx) {
        if (runner != null) {
            Process proc = runner.getProc();
            if (proc != null && proc.isAlive()) {
                ProcessHandle.Info info = proc.info();
                ctx.getSource().sendMessage(Text.translatable("mineshell.info.pid", proc.pid()));
                ctx.getSource().sendMessage(Text.translatable("mineshell.info.command", info.command()));
                ctx.getSource().sendMessage(Text.translatable("mineshell.info.arguments", info.arguments()));
                return;
            }
        }
        ctx.getSource().sendMessage(Text.translatable("procmanager.error.no_running_process"));
    }

    public static void isalive(CommandContext<ServerCommandSource> ctx) {
        if (runner != null) {
            Process proc = runner.getProc();
            if (proc != null && proc.isAlive()) {
                ctx.getSource().sendMessage(Text.translatable("procmanager.isalive.alive"));
                return;
            }
        }
        ctx.getSource().sendMessage(Text.translatable("procmanager.isalive.unlive"));
    }

    public static void inputChar(CommandContext<ServerCommandSource> ctx, char c) throws IOException {
        if (runner != null) {
            BufferedWriter bw = runner.getBw();
            if (bw != null) {
                bw.write(c);
                bw.flush();
                return;
            }
        }
        ctx.getSource().sendMessage(Text.translatable("procmanager.error.no_running_process"));
    }

    public static void inputString(CommandContext<ServerCommandSource> ctx, String str) throws IOException {
        if (runner != null) {
            BufferedWriter bw = runner.getBw();
            if (bw != null) {
                bw.write(str);
                bw.flush();
                return;
            }
        }
        ctx.getSource().sendMessage(Text.translatable("procmanager.error.no_running_process"));
    }

    public static void inputLine(CommandContext<ServerCommandSource> ctx, String str) throws IOException {
        if (runner != null) {
            BufferedWriter bw = runner.getBw();
            if (bw != null) {
                bw.write(str);
                bw.newLine();
                bw.flush();
                return;
            }
        }
        ctx.getSource().sendMessage(Text.translatable("procmanager.error.no_running_process"));
    }

    public static void inputNewLine(CommandContext<ServerCommandSource> ctx) throws IOException {
        if (runner != null) {
            BufferedWriter bw = runner.getBw();
            if (bw != null) {
                bw.newLine();
                bw.flush();
                return;
            }
        }
        ctx.getSource().sendMessage(Text.translatable("procmanager.error.no_running_process"));
    }

    public static ExecRunner getRunner() {
        return runner;
    }
}

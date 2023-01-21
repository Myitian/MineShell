package net.myitian.util;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
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
                    ctx.getSource().sendMessage(Text.literal("已有进程正在运行！"));
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
                ctx.getSource().sendMessage(Text.literal("[MSH][结束进程]"));
                return;
            }
        }
        ctx.getSource().sendMessage(Text.literal("无正在运行的进程！"));
    }

    public static void kill(ProcessHandle handle) {
        handle.descendants().forEach(ProcManager::kill);
        handle.destroy();
    }

    public static void help(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        //ServerCommandSource scs = ctx.getSource();
        throw new SimpleCommandExceptionType(Text.literal("AAA"+ctx.isForked())).create();
        //scs.sendMessage(Text.literal("AAA"+ctx.isForked()));
    }

    public static void info(CommandContext<ServerCommandSource> ctx) {
        if (runner != null) {
            Process proc = runner.getProc();
            if (proc != null && proc.isAlive()) {
                ProcessHandle.Info info = proc.info();
                ctx.getSource().sendMessage(Text.literal("[PID]：" + proc.pid()));
                ctx.getSource().sendMessage(Text.literal("[命令]：" + info.command()));
                ctx.getSource().sendMessage(Text.literal("[参数]：" + info.arguments().toString()));
                return;
            }
        }
        ctx.getSource().sendMessage(Text.literal("无正在运行的进程！"));
    }

    public static void isalive(CommandContext<ServerCommandSource> ctx) {
        if (runner != null) {
            Process proc = runner.getProc();
            if (proc != null && proc.isAlive()) {
                ctx.getSource().sendMessage(Text.literal("Alive"));
                return;
            }
        }
        ctx.getSource().sendMessage(Text.literal("Unalive"));
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
        ctx.getSource().sendMessage(Text.literal("无正在运行的进程！"));
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
        ctx.getSource().sendMessage(Text.literal("无正在运行的进程！"));
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
        ctx.getSource().sendMessage(Text.literal("无正在运行的进程！"));
    }

    public static ExecRunner getRunner() {
        return runner;
    }
}

package net.myitian.util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public class ExecRunner extends Thread {
    public static final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    public static final String charset = isWindows ? "GB2312" : "UTF-8";
    private final String arg;
    private final CommandContext<ServerCommandSource> ctx;
    private StreamGobbler errorGobbler;
    private StreamGobbler outputGobbler;
    private BufferedWriter bw;
    private Process proc;

    ExecRunner(CommandContext<ServerCommandSource> ctx, String arg) {
        this.ctx = ctx;
        this.arg = arg;
    }

    public void run() {
        try {
            ctx.getSource().sendMessage(Text.literal("[MSH][执行]：" + arg));

            proc = Runtime.getRuntime().exec(arg);

            bw = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
            ctx.getSource().sendMessage(Text.literal("[MSH][PID]：" + proc.pid()));

            new Thread((errorGobbler =
                    new StreamGobbler(proc.getErrorStream(), ctx, "[MSH][错误]：", charset))).start();
            new Thread((outputGobbler =
                    new StreamGobbler(proc.getInputStream(), ctx, "[MSH][输出]：", charset))).start();

            int exitVal = proc.waitFor();
            ctx.getSource().sendMessage(Text.literal("[MSH][返回值]：" + exitVal));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public Process getProc() {
        return proc;
    }

    public BufferedWriter getBw() {
        return bw;
    }

    public String getUnsentMessage() {
        String stderr = null, stdout = null;
        if (errorGobbler != null) {
            stderr = errorGobbler.getUnsentMessage();
        }
        if (outputGobbler != null) {
            stdout = outputGobbler.getUnsentMessage();
        }
        if (stderr == null) {
            return stdout;
        } else {
            if (stdout == null) {
                return stderr;
            } else {
                return stderr + '\n' + stdout;
            }
        }
    }
}

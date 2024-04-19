package net.myitian.mineshell.util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.myitian.mineshell.config.Config;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public class ExecRunner extends Thread {
    private final CommandContext<ServerCommandSource> ctx;
    private final String arg;
    private final Config config;
    StreamGobbler outputGobbler;
    StreamGobbler errorGobbler;
    private BufferedWriter bw;
    private Process process;

    ExecRunner(CommandContext<ServerCommandSource> ctx, String arg, Config config) {
        this.ctx = ctx;
        this.arg = arg;
        this.config = config;
    }

    public void run() {
        try {
            ctx.getSource().sendMessage(Text.translatable("mineshell.exec.start", arg));

            process = Runtime.getRuntime().exec(arg);

            bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            ctx.getSource().sendMessage(Text.translatable("mineshell.exec.pid", process.pid()));

            new Thread((outputGobbler =
                    new StreamGobbler(process.getInputStream(), ctx, config.charset, config))).start();
            new Thread((errorGobbler =
                    new StreamGobbler(process.getErrorStream(), ctx, config.charset, config))).start();

            int exitCode = process.waitFor();
            ctx.getSource().sendMessage(Text.translatable("mineshell.exec.exitcode", exitCode));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public Process getProcess() {
        return process;
    }

    public BufferedWriter getBw() {
        return bw;
    }

    public Text[] getUnsentMessages() {
        Text[] outputs = new Text[2];
        if (errorGobbler != null) {
            outputs[0] = errorGobbler.getUnsentMessage();
        }
        if (outputGobbler != null) {
            outputs[1] = outputGobbler.getUnsentMessage();
        }
        return outputs;
    }
}

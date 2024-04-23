package net.myitian.mineshell.util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.myitian.mineshell.MineShellMod;
import net.myitian.mineshell.config.Config;

import java.io.OutputStreamWriter;
import java.io.Writer;

public class ExecRunner extends Thread {
    private final CommandContext<ServerCommandSource> ctx;
    private final String arg;
    private final Config config;
    StreamGobbler outputGobbler;
    StreamGobbler errorGobbler;
    private Writer writer;
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
            writer = new OutputStreamWriter(process.getOutputStream(), config.inputCharset);
            ctx.getSource().sendMessage(Text.translatable("mineshell.exec.pid", process.pid()));

            new Thread((outputGobbler =
                    new StreamGobbler(process.getInputStream(), ctx, config.outputCharset, config))).start();
            new Thread((errorGobbler =
                    new StreamGobbler(process.getErrorStream(), ctx, config.outputCharset, config))).start();

            int exitCode = process.waitFor();
            ctx.getSource().sendMessage(Text.translatable("mineshell.exec.exit_code", exitCode));
        } catch (Exception e) {
            ctx.getSource().sendError(Text.literal(
                    e.getClass().getSimpleName() + ": " + e.getLocalizedMessage()));
            MineShellMod.LOGGER.error(e.getMessage());
        }
    }

    public Process getProcess() {
        return process;
    }

    public Writer getWriter() {
        return writer;
    }

    public Pair<Text,Text> getUnsentMessages() {
        Pair<Text,Text> outputs=new Pair<>(null,null);
        if (outputGobbler != null) {
            outputs.setLeft(outputGobbler.getUnsentMessage());
        }
        if (errorGobbler != null) {
            outputs.setRight(errorGobbler.getUnsentMessage());
        }
        return outputs;
    }
}

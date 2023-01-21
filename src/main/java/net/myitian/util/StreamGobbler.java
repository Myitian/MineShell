package net.myitian.util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class StreamGobbler implements Runnable {
    private final InputStream is;
    private final String prefix;
    private final String charset;
    private final CommandContext<ServerCommandSource> ctx;
    private StringBuilder line;

    StreamGobbler(InputStream is, CommandContext<ServerCommandSource> ctx, String prefix, String charset) {
        this.is = is;
        this.prefix = prefix;
        this.ctx = ctx;
        this.charset = charset;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is, charset);
            BufferedReader br = new BufferedReader(isr);

            line = new StringBuilder();
            boolean CR = false;
            int c;
            while ((c = br.read()) != -1) {
                switch (c) {
                    case '\r':
                        CR = true;
                        break;
                    case '\n':
                        if (CR) {
                            CR = false;
                            continue;
                        }
                        break;
                    default:
                        line.append((char) c);
                        continue;
                }
                ctx.getSource().sendMessage(Text.translatable(prefix, line.toString()));
                line.delete(0, line.length());
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public String getUnsentMessage() {
        if (line == null || line.isEmpty()) {
            return null;
        }
        return line.toString();
    }
}

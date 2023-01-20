package net.myitian.mineshell;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import static net.minecraft.server.command.CommandManager.*;

public class MineShellMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("mineshell");
    public static final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    public static final String charsetName = isWindows ? "GB2312" : "UTF-8";

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("shell")
                .then(argument("cmd", StringArgumentType.string())
                        .executes(context -> {
                            String cmd = StringArgumentType.getString(context, "cmd");
                            try {
                                // 执行脚本
                                Process ps = Runtime.getRuntime().exec(cmd);
                                int exitValue = ps.waitFor();
                                if (0 != exitValue) {
                                    context.getSource().sendMessage(Text.literal("call shell failed. error code is :" + exitValue));
                                }

                                // 只能接收脚本echo打印的数据，并且是echo打印的最后一次数据
                                BufferedInputStream in = new BufferedInputStream(ps.getInputStream());
                                BufferedReader br = new BufferedReader(new InputStreamReader(in, charsetName));
                                String line;
                                while ((line = br.readLine()) != null) {
                                    context.getSource().sendMessage(Text.literal("[脚本]：" + line));
                                }
                                in.close();
                                br.close();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return 1;
                        }))));
    }
}

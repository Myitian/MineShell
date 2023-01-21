package net.myitian.mineshell.mixin;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.ParsedCommandNode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.command.CommandSource;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.myitian.mineshell.MineShellMod;
import net.myitian.util.ProcManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ChatInputSuggestor.class)
public class ChatInputSuggestorMixin {
    @Shadow
    @Final
    private List<OrderedText> messages;
    @Shadow
    private ParseResults<CommandSource> parse;

    @Inject(method = "showCommandSuggestions()V", at = @At("HEAD"))
    public void onShowCommandSuggestions(CallbackInfo ci) {
        List<ParsedCommandNode<CommandSource>> nodes = parse.getContext().getNodes();
        if (nodes.size() > 2) {
            String n0 = nodes.get(0).getNode().getName();
            String n1 = nodes.get(1).getNode().getName();
            String msg = ProcManager.getRunner().getUnsentMessage();
            if (MineShellMod.CMD.equals(n0) && ("input".equals(n1)) && msg != null) {
                messages.add(Text.literal(msg).asOrderedText());
            }
        }
    }
}

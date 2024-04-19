package net.myitian.mineshell.argument;

import com.google.gson.JsonObject;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class ExtendBoolArgumentSerializer
        implements ArgumentSerializer<ExtendBoolArgumentType, ExtendBoolArgumentSerializer.Properties> {
    @Override
    public void writePacket(Properties prop, PacketByteBuf buf) {
        buf.writeString(prop.falseValue).writeString(prop.trueValue).writeBoolean(prop.ignoreCase);
    }

    @Override
    public Properties fromPacket(PacketByteBuf buf) {
        String falseValue = buf.readString();
        String trueValue = buf.readString();
        boolean ignoreCase = buf.readBoolean();
        return new Properties(falseValue, trueValue, ignoreCase);
    }

    @Override
    public void writeJson(Properties arg, JsonObject jsonObject) {
        jsonObject.addProperty("falseValue", arg.falseValue);
        jsonObject.addProperty("trueValue", arg.trueValue);
        jsonObject.addProperty("ignoreCase", arg.ignoreCase);
    }

    @Override
    public Properties getArgumentTypeProperties(ExtendBoolArgumentType extendBoolArgumentType) {
        return new Properties(
                extendBoolArgumentType.falseValue,
                extendBoolArgumentType.trueValue,
                extendBoolArgumentType.ignoreCase);
    }

    public final class Properties
            implements ArgumentSerializer.ArgumentTypeProperties<ExtendBoolArgumentType> {
        final String falseValue, trueValue;
        final boolean ignoreCase;

        public Properties(String falseValue, String trueValue, boolean ignoreCase) {
            this.falseValue = falseValue;
            this.trueValue = trueValue;
            this.ignoreCase = ignoreCase;
        }

        @Override
        public ExtendBoolArgumentType createType(CommandRegistryAccess arg) {
            return ExtendBoolArgumentType.exBool(falseValue, trueValue, ignoreCase);
        }

        @Override
        public ArgumentSerializer<ExtendBoolArgumentType, ?> getSerializer() {
            return ExtendBoolArgumentSerializer.this;
        }
    }
}

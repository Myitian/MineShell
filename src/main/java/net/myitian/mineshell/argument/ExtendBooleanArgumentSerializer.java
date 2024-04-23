package net.myitian.mineshell.argument;

import com.google.gson.JsonObject;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class ExtendBooleanArgumentSerializer
        implements ArgumentSerializer<ExtendBooleanArgumentType, ExtendBooleanArgumentSerializer.Properties> {
    @Override
    public void writePacket(Properties prop, PacketByteBuf buf) {
        buf
                .writeString(prop.falseValue)
                .writeString(prop.trueValue)
                .writeBoolean(prop.ignoreCase);
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
    public Properties getArgumentTypeProperties(ExtendBooleanArgumentType extendBooleanArgumentType) {
        return new Properties(
                extendBooleanArgumentType.falseValue,
                extendBooleanArgumentType.trueValue,
                extendBooleanArgumentType.ignoreCase);
    }

    public final class Properties
            implements ArgumentSerializer.ArgumentTypeProperties<ExtendBooleanArgumentType> {
        final String falseValue, trueValue;
        final boolean ignoreCase;

        public Properties(String falseValue, String trueValue, boolean ignoreCase) {
            this.falseValue = falseValue;
            this.trueValue = trueValue;
            this.ignoreCase = ignoreCase;
        }

        @Override
        public ExtendBooleanArgumentType createType(CommandRegistryAccess arg) {
            return ExtendBooleanArgumentType.exBool(falseValue, trueValue, ignoreCase);
        }

        @Override
        public ArgumentSerializer<ExtendBooleanArgumentType, ?> getSerializer() {
            return ExtendBooleanArgumentSerializer.this;
        }
    }
}

package net.myitian.mineshell.argument;

import com.google.gson.JsonObject;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class NullableBooleanArgumentSerializer
        implements ArgumentSerializer<NullableBooleanArgumentType, NullableBooleanArgumentSerializer.Properties> {
    @Override
    public void writePacket(Properties prop, PacketByteBuf buf) {
        buf
                .writeString(prop.falseValue)
                .writeString(prop.trueValue)
                .writeString(prop.nullValue)
                .writeBoolean(prop.ignoreCase);
    }

    @Override
    public Properties fromPacket(PacketByteBuf buf) {
        String falseValue = buf.readString();
        String trueValue = buf.readString();
        String nullValue = buf.readString();
        boolean ignoreCase = buf.readBoolean();
        return new Properties(falseValue, trueValue, nullValue, ignoreCase);
    }

    @Override
    public void writeJson(Properties arg, JsonObject jsonObject) {
        jsonObject.addProperty("falseValue", arg.falseValue);
        jsonObject.addProperty("trueValue", arg.trueValue);
        jsonObject.addProperty("nullValue", arg.nullValue);
        jsonObject.addProperty("ignoreCase", arg.ignoreCase);
    }

    @Override
    public Properties getArgumentTypeProperties(NullableBooleanArgumentType nullableBooleanArgumentType) {
        return new Properties(
                nullableBooleanArgumentType.falseValue,
                nullableBooleanArgumentType.trueValue,
                nullableBooleanArgumentType.nullValue,
                nullableBooleanArgumentType.ignoreCase);
    }

    public final class Properties
            implements ArgumentTypeProperties<NullableBooleanArgumentType> {
        final String falseValue, trueValue, nullValue;
        final boolean ignoreCase;

        public Properties(String falseValue, String trueValue, String nullValue, boolean ignoreCase) {
            this.falseValue = falseValue;
            this.trueValue = trueValue;
            this.nullValue = nullValue;
            this.ignoreCase = ignoreCase;
        }

        @Override
        public NullableBooleanArgumentType createType(CommandRegistryAccess arg) {
            return NullableBooleanArgumentType.bool3(falseValue, trueValue, nullValue, ignoreCase);
        }

        @Override
        public ArgumentSerializer<NullableBooleanArgumentType, ?> getSerializer() {
            return NullableBooleanArgumentSerializer.this;
        }
    }
}

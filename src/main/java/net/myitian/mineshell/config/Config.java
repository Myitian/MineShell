package net.myitian.mineshell.config;

import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.myitian.mineshell.MineShellMod;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

public class Config {
    static final TypeToken<Config> TYPE = TypeToken.get(Config.class);
    static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("MineShell.json");
    public boolean isANSIEscapeEnabled = true;
    public boolean isUnicodeC1Enabled = false;
    public boolean isEmptyNewLineEnabled = true;
    public boolean hideUnsupportedANSIEscapeSequence = true;
    public boolean isCRLFEnabled = true;
    public @Nullable Boolean isCSICNLEnabled = true;
    public @Nullable Boolean isCSISGREnabled = true;
    public @Nullable Boolean isCREnabled = true;
    public @Nullable Boolean isLFEnabled = true;
    public @Nullable Boolean isBackspaceEnabled = true;
    public @Nullable Boolean isTabEnabled = true;
    public @Nullable Boolean isINDEnabled = true;
    public @Nullable Boolean isNELEnabled = true;
    public @Nullable Boolean isHTSEnabled = true;
    public @Nullable Boolean isCCHEnabled = true;
    public @Nullable Boolean isCSIEnabled = true;
    public String newLine = "\n";
    public String inputCharset = "UTF-8";
    public String outputCharset = "UTF-8";
    public char tabChar = ' ';
    public int tabWidth = 4;

    public Config load() {
        try (var reader = new FileReader(PATH.toFile())) {
            Config read = new GsonBuilder()
                    .registerTypeAdapter(Config.class, (InstanceCreator<?>) type -> this)
                    .create()
                    .fromJson(reader, TYPE);
            return read == null ? this : read;
        } catch (Exception e) {
            if (e instanceof JsonParseException)
                MineShellMod.LOGGER.error("Malformed JSON!");
            else if (e instanceof FileNotFoundException)
                MineShellMod.LOGGER.warn("Config file is missing!");
            MineShellMod.LOGGER.error(e.getClass().getSimpleName());
            MineShellMod.LOGGER.error(e.getLocalizedMessage());
            MineShellMod.LOGGER.error(e.getMessage());
        }
        return this;
    }

    public Config save() {
        try (var writer = new FileWriter(PATH.toFile())) {
            writer.write(new GsonBuilder()
                    .setPrettyPrinting()
                    .serializeNulls()
                    .create()
                    .toJson(this));
        } catch (Exception e) {
            MineShellMod.LOGGER.error(e.getClass().getSimpleName());
            MineShellMod.LOGGER.error(e.getLocalizedMessage());
            MineShellMod.LOGGER.error(e.getMessage());
        }
        return this;
    }
}

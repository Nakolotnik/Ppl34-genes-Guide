package nakolotnik.guide.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigInitializer {

    private static final String CONFIG_DIR_PATH = "config/ppl34genes";
    private static final String CONFIG_FILE_PATH = CONFIG_DIR_PATH + "/gen_info.json";
    private static final String RESOURCE_PATH = "/assets/ppl34guide/gen_info.json";
    private static final String GENES_CONFIG_FILE_PATH = CONFIG_DIR_PATH + "/genes_config.json";

    public static void initializeConfig() {
        File configDir = new File(CONFIG_DIR_PATH);
        File configFile = new File(CONFIG_FILE_PATH);

        try {
            if (!configDir.exists()) {
                if (!configDir.mkdirs()) {
                    throw new IOException("Failed to create directories: " + CONFIG_DIR_PATH);
                }
            }

            if (!configFile.exists()) {
                try (InputStream inputStream = ConfigInitializer.class.getResourceAsStream(RESOURCE_PATH);
                     OutputStream outputStream = new FileOutputStream(configFile)) {
                    if (inputStream == null) {
                        throw new IOException("Resource not found: " + RESOURCE_PATH);
                    }
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("Configuration file created: " + CONFIG_FILE_PATH);
            } else {
                System.out.println("The configuration file already exists " + CONFIG_FILE_PATH);
            }

            loadConfig();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig() {
        try {
            JsonObject config = new JsonObject();
            config.addProperty("cornerRadius", ModConfig.cornerRadius);
            config.addProperty("buttonWidth", ModConfig.buttonWidth);
            config.addProperty("buttonHeight", ModConfig.buttonHeight);
            config.addProperty("leftMargin", ModConfig.textWrapLeftMargin);
            config.addProperty("iconSize", ModConfig.iconSize);
            config.addProperty("textLineHeight", ModConfig.textLineHeight);
            config.addProperty("textLineSpacing", ModConfig.textLineSpacing);

            try (FileWriter writer = new FileWriter(GENES_CONFIG_FILE_PATH)) {
                new Gson().toJson(config, writer);
            }

            System.out.println("Configuration saved: " + GENES_CONFIG_FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig() {
        File configFile = new File(GENES_CONFIG_FILE_PATH);
        if (configFile.exists()) {
            try {
                String jsonText = new String(Files.readAllBytes(Paths.get(GENES_CONFIG_FILE_PATH)));
                JsonObject config = new Gson().fromJson(jsonText, JsonObject.class);

                ModConfig.cornerRadius = config.get("cornerRadius").getAsInt();
                ModConfig.buttonWidth = config.get("buttonWidth").getAsInt();
                ModConfig.buttonHeight = config.get("buttonHeight").getAsInt();
                ModConfig.textWrapLeftMargin = config.get("leftMargin").getAsInt();
                ModConfig.iconSize = config.get("iconSize").getAsInt();
                ModConfig.textLineHeight = config.get("textLineHeight").getAsInt();
                ModConfig.textLineSpacing = config.get("textLineSpacing").getAsInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            saveConfig();
        }
    }
}

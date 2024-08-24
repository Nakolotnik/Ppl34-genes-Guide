package nakolotnik.guide.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    public static final int[] CORNER_RADIUSES = {8, 10, 12};
    public static final int[] BUTTON_WIDTHS = {100, 150, 200};
    public static final int[] BUTTON_HEIGHTS = {30, 40, 50};
    public static final int[] LEFT_MARGINS = {10, 15, 20};
    public static final int[] ICON_SIZES = {16, 24, 32};
    public static final int[] TEXT_LINE_HEIGHTS = {12, 16, 20};
    public static final int[] TEXT_LINE_SPACINGS = {2, 4, 6};
    public static final int[] TEXT_SIZES = {8, 12, 16};

    public static int cornerRadius = CORNER_RADIUSES[0];
    public static int buttonWidth = BUTTON_WIDTHS[0];
    public static int buttonHeight = BUTTON_HEIGHTS[0];
    public static int textWrapLeftMargin = LEFT_MARGINS[0];
    public static int iconSize = ICON_SIZES[0];
    public static int textLineHeight = TEXT_LINE_HEIGHTS[0];
    public static int textLineSpacing = TEXT_LINE_SPACINGS[0];

    private static final File CONFIG_FILE = new File(MinecraftClient.getInstance().runDirectory, "config/ppl34genes/genes_config.json");

    public static void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

                cornerRadius = jsonObject.has("cornerRadius") ? jsonObject.get("cornerRadius").getAsInt() : cornerRadius;
                buttonWidth = jsonObject.has("buttonWidth") ? jsonObject.get("buttonWidth").getAsInt() : buttonWidth;
                buttonHeight = jsonObject.has("buttonHeight") ? jsonObject.get("buttonHeight").getAsInt() : buttonHeight;
                textWrapLeftMargin = jsonObject.has("textWrapLeftMargin") ? jsonObject.get("textWrapLeftMargin").getAsInt() : textWrapLeftMargin;
                iconSize = jsonObject.has("iconSize") ? jsonObject.get("iconSize").getAsInt() : iconSize;
                textLineHeight = jsonObject.has("textLineHeight") ? jsonObject.get("textLineHeight").getAsInt() : textLineHeight;
                textLineSpacing = jsonObject.has("textLineSpacing") ? jsonObject.get("textLineSpacing").getAsInt() : textLineSpacing;

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Configuration file not found: " + CONFIG_FILE.getAbsolutePath());
            saveDefaultConfig();
        }
    }

    public static void saveDefaultConfig() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cornerRadius", cornerRadius);
        jsonObject.addProperty("buttonWidth", buttonWidth);
        jsonObject.addProperty("buttonHeight", buttonHeight);
        jsonObject.addProperty("textWrapLeftMargin", textWrapLeftMargin);
        jsonObject.addProperty("iconSize", iconSize);
        jsonObject.addProperty("textLineHeight", textLineHeight);
        jsonObject.addProperty("textLineSpacing", textLineSpacing);

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            Gson gson = new Gson();
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cornerRadius", cornerRadius);
        jsonObject.addProperty("buttonWidth", buttonWidth);
        jsonObject.addProperty("buttonHeight", buttonHeight);
        jsonObject.addProperty("textWrapLeftMargin", textWrapLeftMargin);
        jsonObject.addProperty("iconSize", iconSize);
        jsonObject.addProperty("textLineHeight", textLineHeight);
        jsonObject.addProperty("textLineSpacing", textLineSpacing);

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            Gson gson = new Gson();
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

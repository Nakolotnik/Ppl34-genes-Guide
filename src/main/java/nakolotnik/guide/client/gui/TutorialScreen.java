package nakolotnik.guide.client.gui;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nakolotnik.guide.PPL34Guide;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class TutorialScreen extends Screen {
    private static final int SPACING = 10; // Промежуток между кнопками
    private static final int ICON_SIZE = 16; // Размер иконки
    private static final int TEXT_LINE_HEIGHT = 16; // Высота строки текста
    private static final int TEXT_LINE_SPACING = 5; // Отступ между строками текста
    private static final int LEFT_MARGIN = 2; // Отступ от левого края для текста и иконок
    private static final int ICON_MARGIN = 4; // Отступ между иконкой и текстом
    private static final int SCROLL_SPEED = 30; // Скорость прокрутки
    private int textWrapLeftMargin = 20; // Переменная для контроля левого отступа

    private Identifier[] levelIcons = new Identifier[0];
    private int scrollOffset = 0;
    private int contentHeight;
    private String[] infoBoxText = new String[]{};

    private List<ButtonInfo> buttonInfos = new ArrayList<>();
    private ButtonInfo hoveredButtonInfo = null;

    protected TutorialScreen() {
        super(Text.literal("Гайд по генам"));
        loadButtonInfos();
    }

    private void loadButtonInfos() {
        try {
            File configFile = new File(MinecraftClient.getInstance().runDirectory, "config/ppl34genes/gen_info.json");

            if (!configFile.exists()) {
                System.out.println("JSON файл не найден: " + configFile.getAbsolutePath());
                return;
            }

            String jsonText = Files.readString(configFile.toPath(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonText, JsonObject.class);
            JsonArray jsonArray = jsonObject.getAsJsonArray("genes");

            buttonInfos.clear();

            for (JsonElement element : jsonArray) {
                JsonObject obj = element.getAsJsonObject();
                String label = obj.get("label").getAsString();
                String tooltip = obj.get("tooltip").getAsString();

                JsonArray levelsArray = obj.getAsJsonArray("levels");
                String[] levels = new String[levelsArray.size()];
                for (int i = 0; i < levelsArray.size(); i++) {
                    levels[i] = levelsArray.get(i).getAsString();
                }

                Identifier icon = new Identifier(PPL34Guide.MOD_ID, obj.get("icon").getAsString());

                JsonArray iconsArray = obj.getAsJsonArray("levelIcons");
                Identifier[] levelIcons = new Identifier[iconsArray.size()];
                for (int i = 0; i < iconsArray.size(); i++) {
                    levelIcons[i] = new Identifier(PPL34Guide.MOD_ID, iconsArray.get(i).getAsString());
                }

                buttonInfos.add(new ButtonInfo(label, tooltip, levels, icon, levelIcons));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void init() {
        int screenWidth = width;
        int screenHeight = height;

        int buttonWidth = Math.min(screenWidth / 3, 300);
        int buttonHeight = 30;

        contentHeight = buttonInfos.size() * (buttonHeight + SPACING);
    }

    private void fillQuarterCircle(DrawContext context, int centerX, int centerY, int radius, int color, Corner corner) {
        int radiusSquared = radius * radius;
        for (int dx = 0; dx <= radius; dx++) {
            int dy = (int) Math.sqrt(radiusSquared - dx * dx);
            switch (corner) {
                case TOP_LEFT:
                    context.fill(centerX - dx, centerY - dy, centerX - dx + 1, centerY, color);
                    break;
                case TOP_RIGHT:
                    context.fill(centerX + dx, centerY - dy, centerX + dx + 1, centerY, color);
                    break;
                case BOTTOM_LEFT:
                    context.fill(centerX - dx, centerY, centerX - dx + 1, centerY + dy, color);
                    break;
                case BOTTOM_RIGHT:
                    context.fill(centerX + dx, centerY, centerX + dx + 1, centerY + dy, color);
                    break;
            }
        }
    }

    private enum Corner {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        int screenWidth = width;
        int screenHeight = height;

        int buttonWidth = Math.min(screenWidth / 3, 300);
        int buttonHeight = 30;
        int infoBoxWidth = Math.min(screenWidth / 2, 460);
        int infoBoxHeight = screenHeight - 60;

        int startX = 20;
        int startY = 30;
        int cornerRadius = 10;

        hoveredButtonInfo = null;

        for (int i = 0; i < buttonInfos.size(); i++) {
            int x = startX;
            int y = startY + i * (buttonHeight + SPACING) - scrollOffset;

            boolean isHovered = mouseX >= x && mouseX <= x + buttonWidth && mouseY >= y && mouseY <= y + buttonHeight;
            int backgroundColor = isHovered ? 0xAA444444 : 0xAA222222;

            drawRoundedRectangle(context, x, y, buttonWidth, buttonHeight, cornerRadius, backgroundColor);

            ButtonInfo buttonInfo = buttonInfos.get(i);
            context.drawTexture(buttonInfo.getIcon(), x + 5, y + (buttonHeight - ICON_SIZE) / 2, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

            String labelText = buttonInfo.getLabel();
            int textWidth = textRenderer.getWidth(labelText);
            int textX = x + ICON_SIZE + ICON_MARGIN + (buttonWidth - ICON_SIZE - ICON_MARGIN - textWidth) / 2;
            int textY = y + (buttonHeight - textRenderer.fontHeight) / 2;
            context.drawText(textRenderer, Text.literal(labelText), textX, textY, 0xFFFFFFFF, false);

            if (isHovered) {
                hoveredButtonInfo = buttonInfo;
            }
        }

        if (hoveredButtonInfo != null) {
            renderTooltip(context, hoveredButtonInfo.getTooltip(), mouseX, mouseY);
        }

        int infoBoxX = screenWidth - infoBoxWidth - 20;
        int infoBoxY = 30;
        context.fillGradient(infoBoxX - 5, infoBoxY - 5, infoBoxX + infoBoxWidth + 5, infoBoxY + infoBoxHeight + 5, 0xFF444444, 0xFF222222);

        int yOffset = 0;

        for (int i = 0; i < infoBoxText.length; i++) {
            List<String> lines = wrapText(infoBoxText[i], infoBoxWidth);
            for (int j = 0; j < lines.size(); j++) {
                int lineY = infoBoxY + yOffset + i * (TEXT_LINE_HEIGHT + TEXT_LINE_SPACING) + j * (TEXT_LINE_HEIGHT + TEXT_LINE_SPACING);

                if (i >= 2 && i - 2 < levelIcons.length && j == 0) {
                    int iconY = lineY + (TEXT_LINE_HEIGHT - ICON_SIZE) / 2;
                    int textY = lineY + (TEXT_LINE_HEIGHT - textRenderer.fontHeight) / 2;

                    context.drawTexture(levelIcons[i - 2], infoBoxX + LEFT_MARGIN, iconY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
                    int textX = infoBoxX + LEFT_MARGIN + ICON_SIZE + ICON_MARGIN;
                    context.drawText(textRenderer, Text.literal(lines.get(j)), textX, textY, 0xFFFFFF, false);
                } else {
                    context.drawText(textRenderer, Text.literal(lines.get(j)), infoBoxX + LEFT_MARGIN, lineY, 0xFFFFFF, false);
                }

                if (j > 0) {
                    yOffset += TEXT_LINE_SPACING;
                }
            }
        }
    }

    private void drawRoundedRectangle(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        context.fill(x + radius, y, x + width - radius, y + height, color);

        context.fill(x, y + radius, x + radius, y + height - radius, color);
        context.fill(x + width - radius, y + radius, x + width, y + height - radius, color);

        fillQuarterCircle(context, x + radius - 1, y + radius, radius, color, Corner.TOP_LEFT);
        fillQuarterCircle(context, x + width - radius, y + radius, radius, color, Corner.TOP_RIGHT);
        fillQuarterCircle(context, x + radius - 1, y + height - radius, radius, color, Corner.BOTTOM_LEFT);
        fillQuarterCircle(context, x + width - radius, y + height - radius, radius, color, Corner.BOTTOM_RIGHT);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int topY = 30;
        int bottomY = topY + Math.min(contentHeight, height - topY);

        boolean isScrollingArea = mouseY >= topY && mouseY <= bottomY;

        if (isScrollingArea) {
            scrollOffset = Math.max(0, Math.min(scrollOffset + (int) (-verticalAmount * SCROLL_SPEED), contentHeight - (height - topY)));
            return true;
        }
        return false;
    }

    private List<String> wrapText(String text, int maxWidth) {
        List<String> wrappedText = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        int adjustedMaxWidth = maxWidth - textWrapLeftMargin;

        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);

            if (c == '\n') {
                wrappedText.add(currentLine.toString());
                currentLine = new StringBuilder();
                continue;
            }

            currentLine.append(c);
            String testLine = currentLine.toString();

            if (textRenderer.getWidth(testLine) > adjustedMaxWidth) {
                int lastSpaceIndex = testLine.lastIndexOf(' ');
                if (lastSpaceIndex != -1) {
                    wrappedText.add(testLine.substring(0, lastSpaceIndex));
                    currentLine = new StringBuilder(testLine.substring(lastSpaceIndex + 1));
                } else {
                    wrappedText.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
            }
        }

        if (currentLine.length() > 0) {
            wrappedText.add(currentLine.toString());
        }

        return wrappedText;
    }

    private void renderTooltip(DrawContext context, String tooltipText, int mouseX, int mouseY) {
        List<Text> tooltip = new ArrayList<>();
        tooltip.add(Text.literal(tooltipText));
        context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
    }

    private static class ButtonInfo {
        private final String label;
        private final String tooltip;
        private final String[] levels;
        private final Identifier icon;
        private final Identifier[] levelIcons;

        ButtonInfo(String label, String tooltip, String[] levels, Identifier icon, Identifier[] levelIcons) {
            this.label = label;
            this.tooltip = tooltip;
            this.levels = levels;
            this.icon = icon;
            this.levelIcons = levelIcons;
        }

        public String getLabel() {
            return label;
        }

        public String getTooltip() {
            return tooltip;
        }

        public Identifier getIcon() {
            return icon;
        }

        public Identifier[] getLevelIcons() {
            return levelIcons;
        }

        public String[] getInfo() {
            String[] info = new String[levels.length + 2];
            info[0] = "Название гена: " + this.label;
            info[1] = "Уровни:";
            for (int i = 0; i < levels.length; i++) {
                info[i + 2] = (i + 1) + " - " + levels[i];
            }
            return info;
        }
    }
}

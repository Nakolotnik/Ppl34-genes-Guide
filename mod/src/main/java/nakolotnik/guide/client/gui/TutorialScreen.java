package nakolotnik.guide.client.gui;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nakolotnik.guide.PPL34Guide;
import nakolotnik.guide.config.ConfigInitializer;
import nakolotnik.guide.config.ModConfig;
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

    private static final int LEFT_MARGIN = 2; // Отступ от левого края для текста и иконок
    private static final int ICON_MARGIN = 4; // Отступ между иконкой и текстом
    private static final int SCROLL_SPEED = 30; // Скорость прокрутки
    private static final float SCALE_SPEED = 0.1f; // Скорость анимации масштабирования

    private int textWrapLeftMargin = ModConfig.textWrapLeftMargin; // Отступ для оборачивания текста
    private int buttonWidth = ModConfig.buttonWidth; // Ширина кнопок
    private int buttonHeight = ModConfig.buttonHeight; // Высота кнопок
    private int cornerRadius = ModConfig.cornerRadius; // Радиус скругления
    private int iconSize = ModConfig.iconSize; // Размер иконки
    private int textLineHeight = ModConfig.textLineHeight; // Высота строки текста
    private int textLineSpacing = ModConfig.textLineSpacing; // Отступ между строками текста
//    private int textSize = ModConfig.textSize;


    private int textSize = 12;


    private int scrollOffset = 0;
    private int contentHeight;
    private float[] iconScales;

    private List<ButtonInfo> buttonInfos = new ArrayList<>();
    private ButtonInfo selectedButtonInfo = null;
    private String[] infoBoxText = new String[]{};

    protected TutorialScreen() {
        super(Text.literal("Гайд по генам"));
        loadButtonInfos();
        iconScales = new float[buttonInfos.size()];
    }

    private void loadButtonInfos() {
        try {
            File configFile = new File(MinecraftClient.getInstance().runDirectory, "config/ppl34genes/gen_info.json");

            if (!configFile.exists()) {
                System.out.println("JSON файл не найден: " + configFile.getAbsolutePath());
                ConfigInitializer.initializeConfig();
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

        buttonWidth = Math.min(screenWidth / 3, 300);
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

        int infoBoxWidth = Math.min(screenWidth / 2, 460);
        int infoBoxHeight = screenHeight - 60;

        int startX = 20;
        int startY = 30;

        for (int i = 0; i < buttonInfos.size(); i++) {
            int x = startX;
            int y = startY + i * (buttonHeight + SPACING) - scrollOffset;

            boolean isHovered = mouseX >= x && mouseX <= x + buttonWidth && mouseY >= y && mouseY <= y + buttonHeight;
            int backgroundColor = isHovered ? 0xFF666666 : 0xFF333333;

            // Плавное изменение масштаба
            float targetScale = isHovered ? 1.1f : 1.0f;
            iconScales[i] = lerp(iconScales[i], targetScale, SCALE_SPEED);

            drawRoundedRectangle(context, x, y, buttonWidth, buttonHeight, cornerRadius, backgroundColor);

            ButtonInfo buttonInfo = buttonInfos.get(i);
            int iconX = x + 5;
            int iconY = y + (buttonHeight - iconSize) / 2;

            int scaledIconSize = (int) (iconSize * iconScales[i]);

            // Сохранение текущей матрицы
            context.getMatrices().push();

            // Перемещение и масштабирование
            context.getMatrices().translate(iconX + (iconSize - scaledIconSize) / 2.0f, iconY + (iconSize - scaledIconSize) / 2.0f, 0);
            context.getMatrices().scale(iconScales[i], iconScales[i], 1.0f);

            // Отрисовка иконки
            context.drawTexture(buttonInfo.getIcon(), 0, 0, 0, 0, iconSize, iconSize, iconSize, iconSize);

            // Восстановление матрицы
            context.getMatrices().pop();

            String labelText = buttonInfo.getLabel();
            int textWidth = (int) (textRenderer.getWidth(labelText) * textSize / 12.0);
            int textHeight = (int) (textRenderer.fontHeight * textSize / 12.0);

            int textX = x + iconSize + ICON_MARGIN + (buttonWidth - iconSize - ICON_MARGIN - textWidth) / 2;
            int textY = y + (buttonHeight - textHeight) / 2;
            int textColor = isHovered ? 0xFFFFE0B2 : 0xFFFFFFFF;

            context.getMatrices().push();
            context.getMatrices().scale(textSize / 12.0f, textSize / 12.0f, 1.0f);
            context.drawText(textRenderer, Text.literal(labelText), (int)(textX / (textSize / 12.0f)), (int)(textY / (textSize / 12.0f)), textColor, false);
            context.getMatrices().pop();

            if (isHovered) {
                context.drawTooltip(textRenderer, List.of(Text.literal(buttonInfo.getTooltip())), mouseX, mouseY);
            }
        }

        int infoBoxX = screenWidth - infoBoxWidth - 20;
        int infoBoxY = 30;
        context.fillGradient(infoBoxX - 5, infoBoxY - 5, infoBoxX + infoBoxWidth + 5, infoBoxY + infoBoxHeight + 5, 0xFF444444, 0xFF222222);

        int yOffset = 0;

        if (selectedButtonInfo != null) {
            String[] infoText = selectedButtonInfo.getInfo();
            for (int i = 0; i < infoText.length; i++) {
                List<String> lines = wrapText(infoText[i], infoBoxWidth);
                for (int j = 0; j < lines.size(); j++) {
                    int lineY = infoBoxY + yOffset + i * (textLineHeight + textLineSpacing) + j * (textLineHeight + textLineSpacing);

                    if (i >= 2 && i - 2 < selectedButtonInfo.getLevelIcons().length && j == 0) {
                        int iconY = lineY + (textLineHeight - iconSize) / 2;
                        int textY = lineY + (textLineHeight - (int)(textRenderer.fontHeight * textSize / 12.0)) / 2;

                        context.drawTexture(selectedButtonInfo.getLevelIcons()[i - 2], infoBoxX + LEFT_MARGIN, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
                        int textX = infoBoxX + LEFT_MARGIN + iconSize + ICON_MARGIN;
                        context.drawText(textRenderer, Text.literal(lines.get(j)), (int)(textX / (textSize / 12.0)), (int)(textY / (textSize / 12.0)), 0xFFFFFF, false);
                    } else {
                        context.drawText(textRenderer, Text.literal(lines.get(j)), (int)(infoBoxX + LEFT_MARGIN / (textSize / 12.0)), (int)(lineY / (textSize / 12.0)), 0xFFFFFF, false);
                    }

                    if (j > 0) {
                        yOffset += textLineSpacing;
                    }
                }
            }
        }
    }




    private float lerp(float start, float end, float speed) {
        return start + (end - start) * speed;
    }

    private void drawRoundedRectangle(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        // Ограничиваем радиус
        int maxRadius = Math.min(width, height) / 2;
        radius = Math.min(radius, maxRadius);

        // Рисуем центральные области
        context.fill(x + radius, y, x + width - radius, y + height, color);
        context.fill(x, y + radius, x + radius, y + height - radius, color);
        context.fill(x + width - radius, y + radius, x + width, y + height - radius, color);

        // Рисуем закругленные углы
        fillQuarterCircle(context, x + radius - 1, y + radius, radius, color, Corner.TOP_LEFT);
        fillQuarterCircle(context, x + width - radius, y + radius, radius, color, Corner.TOP_RIGHT);
        fillQuarterCircle(context, x + radius - 1, y + height - radius, radius, color, Corner.BOTTOM_LEFT);
        fillQuarterCircle(context, x + width - radius, y + height - radius, radius, color, Corner.BOTTOM_RIGHT);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = 0; i < buttonInfos.size(); i++) {
            int x = 20;
            int y = 30 + i * (buttonHeight + SPACING) - scrollOffset;

            if (mouseX >= x && mouseX <= x + buttonWidth && mouseY >= y && mouseY <= y + buttonHeight) {
                selectedButtonInfo = buttonInfos.get(i);
                infoBoxText = selectedButtonInfo.getInfo();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
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
        int scaledMaxWidth = (int)(adjustedMaxWidth / (textSize / 12.0f));

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

            if (textRenderer.getWidth(testLine) * textSize / 12.0 > scaledMaxWidth) {
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

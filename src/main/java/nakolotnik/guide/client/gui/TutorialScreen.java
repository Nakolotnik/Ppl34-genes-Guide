package nakolotnik.guide.client.gui;

import nakolotnik.guide.PPL34Guide;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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
    private int textWrapLeftMargin = 30; // Переменная для контроля левого отступа


    private Identifier[] levelIcons = new Identifier[0];
    private int scrollOffset = 0;
    private int contentHeight;
    private String[] infoBoxText = new String[]{};

    private final ButtonInfo[] buttonInfos = new ButtonInfo[]{
            new ButtonInfo("Рост", "Влияет на рост игрока", new String[]{
                    "Маленький (0,8 - 1,2)",
                    "Средний (1,2 - 2)",
                    "Большой (2 - 3,5)",
                    "Игрока можно доить ведром, получая молоко, не влияет на рост\n§7Возможно зависит от хаоса§r"
            }, new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/growth.png"), new Identifier[]{
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/growth/1.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/growth/2.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/growth/3.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/growth/4.png")
            }),
            new ButtonInfo("Уровень здоровья", "Влияет на количество хп", new String[]{
                    "Мало",
                    "Средне. Выше регенерация?",
                    "Много. Выше регенерация?",
                    "Игрок может стричь взрослых коров получая кожу и стейки"
            }, new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/hp.png"), new Identifier[]{
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/hp/1.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/hp/2.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/hp/3.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/hp/4.png")
            }),
            new ButtonInfo("Высота прыжка", "Влияет на высоту прыжка", new String[]{
                    "1 блок",
                    "1 - 1,5 блока",
                    "1,5 - 2 блока",
                    "Игрок может прыгать в 2 блока и получать квампы(ПКМ по слизи)\n§7Зависит от роста§r",

            }, new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/jump.png"), new Identifier[]{
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/jump/1.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/jump/2.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/jump/3.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/jump/4.png")
            }),
            new ButtonInfo("Скорость бега", "Влияет на скорость бега", new String[]{
                    "Низкая",
                    "Средняя",
                    "Высокая",
                    "§7Возможно зависит от хаоса§r"
            }, new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/run.png"), new Identifier[]{
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/run/1.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/run/2.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/run/3.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/run/4.png")
            }),
            new ButtonInfo("Дальность копания", "Влияет на дальность копания", new String[]{
                    "3 блока",
                    "5 блоков",
                    "7 блоков",
                    "Игрок может использовать верстак нажимая ПКМ по воздуху"
            }, new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/minelenght.png"), new Identifier[]{
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/minelenght/1.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/minelenght/2.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/minelenght/3.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/minelenght/4.png")
            }),
            new ButtonInfo("Подъем на блок", "Влияет на высоту подъема на блок", new String[]{
                    "Нет. Не всегда?",
                    "0 - 0,7 блока",
                    "0,7 - 1 блок",
                    "Игрок становится вегетарианцем(не может есть мясо, пить молоко и т.д.)\n§7Зависит от роста§r",
            }, new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/blockup.png"), new Identifier[]{
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/blockup/1.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/blockup/2.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/blockup/3.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/blockup/4.png")
            }),
            new ButtonInfo("Урон от падения", "Влияет на уменьшение урона от падения", new String[]{
                    "Точно не известно\n§7(3 блока?)§r",
                    "4 блока",
                    "5 блоков",
                    "Игрок может кататься на криперах и они его не атакуют\n§7Зависит от роста§r"
            }, new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/falling.png"), new Identifier[]{
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/falling/1.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/falling/2.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/falling/3.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/falling/4.png")
            }),
            new ButtonInfo("Защита", "Влияет на пассивную защиту", new String[]{
                    "Низкая",
                    "Средняя",
                    "Высокая",
                    "Игрок варит случайное зелье кликнув ПКМ пустой бутылкой по воздуху\n§7Зависит от роста§r"

            }, new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/def.png"), new Identifier[]{
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/def/1.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/def/2.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/def/3.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/def/4.png")
            }),
            new ButtonInfo("Сила", "Влияет на урон от удара", new String[]{
                    "Низкая",
                    "Средняя",
                    "Высокая",
                    "Игрок может бросаться кирпичами\n(ПКМ по воздуху)"
            }, new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/strength.png"), new Identifier[]{
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/strength/1.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/strength/2.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/strength/3.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/strength/4.png")
            }),
            new ButtonInfo("Скорость атаки", "Влияет на скорость атаки", new String[]{
                    "Низкая",
                    "Средняя",
                    "Высокая",
                    "Пока неизвестно"
            }, new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/attackspeed.png"), new Identifier[]{
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/attackspeed/1.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/attackspeed/2.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/attackspeed/3.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/attackspeed/4.png")
            }),
            new ButtonInfo("Скорость копания", "Влияет на скорость копания", new String[]{
                    "Низкая",
                    "Средняя",
                    "Высокая",
                    "Только игрок с этим геном может бросать око эндера"
            }, new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/haste.png"), new Identifier[]{
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/haste/1.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/haste/2.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/haste/3.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/haste/4.png")
            }),
            new ButtonInfo("Гравитация предметов", "Влияет на гравитацию предметов", new String[]{
                    "Слабая",
                    "Обычная",
                    "Высокая",
                    "Предметы, которые выкинул игрок, летят +- 20 блоков\n(лук странно работает)"
            }, new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/gravity.png"), new Identifier[]{
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/gravity/1.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/gravity/2.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/gravity/3.png"),
                    new Identifier(PPL34Guide.MOD_ID, "textures/gui/icons/gravity/4.png")
            })
    };

    protected TutorialScreen() {
        super(Text.literal("Гайд по генам"));
    }


    @Override
    protected void init() {
        int screenWidth = width;
        int screenHeight = height;

        int buttonWidth = Math.min(screenWidth / 3, 300);
        int buttonHeight = 30;

        contentHeight = buttonInfos.length * (buttonHeight + SPACING);
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

        for (int i = 0; i < buttonInfos.length; i++) {
            int x = startX;
            int y = startY + i * (buttonHeight + SPACING) - scrollOffset;

            boolean isHovered = mouseX >= x && mouseX <= x + buttonWidth && mouseY >= y && mouseY <= y + buttonHeight;
            int backgroundColor = isHovered ? 0xAA444444 : 0xAA222222;

            drawRoundedRectangle(context, x, y, buttonWidth, buttonHeight, cornerRadius, backgroundColor);

            ButtonInfo buttonInfo = buttonInfos[i];
            context.drawTexture(buttonInfo.getIcon(), x + 5, y + (buttonHeight - ICON_SIZE) / 2, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

            String labelText = buttonInfo.getLabel();
            int textWidth = textRenderer.getWidth(labelText);
            int textX = x + ICON_SIZE + ICON_MARGIN + (buttonWidth - ICON_SIZE - ICON_MARGIN - textWidth) / 2;
            int textY = y + (buttonHeight - textRenderer.fontHeight) / 2;
            context.drawText(textRenderer, Text.literal(labelText), textX, textY, 0xFFFFFFFF, false);

            if (isHovered && isMouseClicked) {
                infoBoxText = buttonInfo.getInfo();
                levelIcons = buttonInfo.getLevelIcons();
            }
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
        fillQuarterCircle(context, x + width - radius , y + radius, radius, color, Corner.TOP_RIGHT);
        fillQuarterCircle(context, x + radius - 1, y + height - radius , radius, color, Corner.BOTTOM_LEFT);
        fillQuarterCircle(context, x + width - radius , y + height - radius , radius, color, Corner.BOTTOM_RIGHT);
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


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        isMouseClicked = true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isMouseClicked = false;
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

    private boolean isMouseClicked = false;

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
package nakolotnik.guide.integrations;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import nakolotnik.guide.config.ConfigInitializer;
import nakolotnik.guide.config.ModConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new Screen(Text.literal("Mod Configuration")) {

            @Override
            protected void init() {
                super.init();

                int centerX = this.width / 2 - 100;
                int startY = this.height / 2 - 100;

                this.addDrawableChild(new SliderWidget(centerX, startY, 200, 20, Text.translatable("config.corner_radius", ModConfig.cornerRadius), ModConfig.cornerRadius / 50.0f) {
                    @Override
                    protected void updateMessage() {
                        int radius = (int) (this.value * 50);
                        this.setMessage(Text.translatable("config.corner_radius", radius));
                    }

                    @Override
                    protected void applyValue() {
                        ModConfig.cornerRadius = (int) (this.value * 50);
                        ConfigInitializer.saveConfig();
                    }
                });

                int yOffset = 40;

                // Button Width
                this.addDrawableChild(ButtonWidget.builder(Text.translatable("config.button_width", ModConfig.buttonWidth), button -> {
                    ModConfig.buttonWidth = (ModConfig.buttonWidth == ModConfig.BUTTON_WIDTHS[0]) ? ModConfig.BUTTON_WIDTHS[1] :
                            (ModConfig.buttonWidth == ModConfig.BUTTON_WIDTHS[1]) ? ModConfig.BUTTON_WIDTHS[2] : ModConfig.BUTTON_WIDTHS[0];
                    button.setMessage(Text.translatable("config.button_width", ModConfig.buttonWidth));
                    ConfigInitializer.saveConfig();
                }).dimensions(centerX, startY + yOffset, 200, 20).build());

                // Button Height
                this.addDrawableChild(ButtonWidget.builder(Text.translatable("config.button_height", ModConfig.buttonHeight), button -> {
                    ModConfig.buttonHeight = (ModConfig.buttonHeight == ModConfig.BUTTON_HEIGHTS[0]) ? ModConfig.BUTTON_HEIGHTS[1] :
                            (ModConfig.buttonHeight == ModConfig.BUTTON_HEIGHTS[1]) ? ModConfig.BUTTON_HEIGHTS[2] : ModConfig.BUTTON_HEIGHTS[0];
                    button.setMessage(Text.translatable("config.button_height", ModConfig.buttonHeight));
                    ConfigInitializer.saveConfig();
                }).dimensions(centerX, startY + yOffset + 30, 200, 20).build());

                // Left Margin
                this.addDrawableChild(ButtonWidget.builder(Text.translatable("config.left_margin", ModConfig.textWrapLeftMargin), button -> {
                    ModConfig.textWrapLeftMargin = (ModConfig.textWrapLeftMargin == ModConfig.LEFT_MARGINS[0]) ? ModConfig.LEFT_MARGINS[1] :
                            (ModConfig.textWrapLeftMargin == ModConfig.LEFT_MARGINS[1]) ? ModConfig.LEFT_MARGINS[2] : ModConfig.LEFT_MARGINS[0];
                    button.setMessage(Text.translatable("config.left_margin", ModConfig.textWrapLeftMargin));
                    ConfigInitializer.saveConfig();
                }).dimensions(centerX, startY + yOffset + 60, 200, 20).build());

                // Icon Size
                this.addDrawableChild(ButtonWidget.builder(Text.translatable("config.icon_size", ModConfig.iconSize), button -> {
                    ModConfig.iconSize = (ModConfig.iconSize == ModConfig.ICON_SIZES[0]) ? ModConfig.ICON_SIZES[1] :
                            (ModConfig.iconSize == ModConfig.ICON_SIZES[1]) ? ModConfig.ICON_SIZES[2] : ModConfig.ICON_SIZES[0];
                    button.setMessage(Text.translatable("config.icon_size", ModConfig.iconSize));
                    ConfigInitializer.saveConfig();
                }).dimensions(centerX, startY + yOffset + 90, 200, 20).build());

                // Text Line Height
                this.addDrawableChild(ButtonWidget.builder(Text.translatable("config.text_line_height", ModConfig.textLineHeight), button -> {
                    ModConfig.textLineHeight = (ModConfig.textLineHeight == ModConfig.TEXT_LINE_HEIGHTS[0]) ? ModConfig.TEXT_LINE_HEIGHTS[1] :
                            (ModConfig.textLineHeight == ModConfig.TEXT_LINE_HEIGHTS[1]) ? ModConfig.TEXT_LINE_HEIGHTS[2] : ModConfig.TEXT_LINE_HEIGHTS[0];
                    button.setMessage(Text.translatable("config.text_line_height", ModConfig.textLineHeight));
                    ConfigInitializer.saveConfig();
                }).dimensions(centerX, startY + yOffset + 120, 200, 20).build());

                // Text Line Spacing
                this.addDrawableChild(ButtonWidget.builder(Text.translatable("config.text_line_spacing", ModConfig.textLineSpacing), button -> {
                    ModConfig.textLineSpacing = (ModConfig.textLineSpacing == ModConfig.TEXT_LINE_SPACINGS[0]) ? ModConfig.TEXT_LINE_SPACINGS[1] :
                            (ModConfig.textLineSpacing == ModConfig.TEXT_LINE_SPACINGS[1]) ? ModConfig.TEXT_LINE_SPACINGS[2] : ModConfig.TEXT_LINE_SPACINGS[0];
                    button.setMessage(Text.translatable("config.text_line_spacing", ModConfig.textLineSpacing));
                    ConfigInitializer.saveConfig();
                }).dimensions(centerX, startY + yOffset + 150, 200, 20).build());
            }

            @Override
            public void close() {
                this.client.setScreen(parent);
            }
        };
    }
}

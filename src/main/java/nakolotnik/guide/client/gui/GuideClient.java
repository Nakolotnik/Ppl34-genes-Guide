package nakolotnik.guide.client.gui;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class GuideClient implements ClientModInitializer {
    private static KeyBinding openTutorialScreenKey;

    @Override
    public void onInitializeClient() {
        // Регистрация новой клавиши с возможностью изменения в настройках
        openTutorialScreenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.guide.open_tutorial_screen", // Идентификатор клавиши
                InputUtil.Type.KEYSYM, // Тип ввода (клавиатура)
                GLFW.GLFW_KEY_K, // Код клавиши по умолчанию (в данном случае K)
                "category.guide.keybindings" // Категория клавиши (пользователь видит это в настройках)
        ));

        // Обработчик события нажатия клавиши
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openTutorialScreenKey.wasPressed()) {
                // Проверка, что клиент доступен и можно открыть экран
                if (client != null) {
                    client.setScreen(new TutorialScreen());
                }
            }
        });
    }
}

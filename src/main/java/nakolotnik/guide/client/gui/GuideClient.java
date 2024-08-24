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
        openTutorialScreenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.guide.open_tutorial_screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.guide.keybindings"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openTutorialScreenKey.wasPressed()) {
                if (client != null) {
                    client.setScreen(new TutorialScreen());
                }
            }
        });
    }
}

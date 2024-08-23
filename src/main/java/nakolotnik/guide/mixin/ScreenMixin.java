package nakolotnik.guide.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.minecraft.client.gui.screen.Screen;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Mixin;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Mixin(Screen.class)
public class ScreenMixin {

    /**
     * Метод для загрузки JSON-файла извне.
     * @param filePath путь к JSON-файлу.
     * @return JsonArray объект, представляющий содержимое файла.
     */
    public JsonArray loadJsonFromFile(String filePath) {
        try {
            InputStream inputStream = new FileInputStream(filePath);
            String jsonText = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            return JsonParser.parseString(jsonText).getAsJsonArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonArray();
        }
    }
}

package nakolotnik.guide.client.gui;

import com.google.gson.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.resource.Resource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EditScreen extends Screen {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 400;

    private TextFieldWidget jsonPathField;
    private TextFieldWidget jsonEditorField;
    private String jsonPath;
    private JsonObject jsonObject;

    protected EditScreen() {
        super(Text.literal("JSON Editor"));
        this.jsonPath = "/assets/ppl34guide/gen_info.json";  // Default path
        this.jsonObject = new JsonObject();  // Initialize with empty JsonObject
    }
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Инициализация поля для пути JSON
        this.jsonPathField = new TextFieldWidget(this.textRenderer, centerX - WIDTH / 2 + 10, centerY - HEIGHT / 2 + 10, WIDTH - 20, 20, Text.literal("Path"));
        this.jsonPathField.setText(this.jsonPath);
        this.addDrawableChild(this.jsonPathField);

        // Инициализация поля JSON редактора с поддержкой многострочного ввода
        this.jsonEditorField = new TextFieldWidget(this.textRenderer, centerX - WIDTH / 2 + 10, centerY - HEIGHT / 2 + 40, WIDTH - 20, HEIGHT - 140, Text.literal("JSON Editor"));
        this.jsonEditorField.setMaxLength(Integer.MAX_VALUE);
        this.jsonEditorField.setEditable(true);
        this.addDrawableChild(this.jsonEditorField);

        // Кнопка "Load" в левом нижнем углу
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Load"), button -> loadJson())
                .position(10, this.height - 30)
                .size(80, 20)
                .tooltip(Tooltip.of(Text.literal("Load JSON from file")))
                .build());

        // Кнопка "Save" в правом нижнем углу
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save"), button -> saveJson())
                .position(this.width - 90, this.height - 30)
                .size(80, 20)
                .tooltip(Tooltip.of(Text.literal("Save JSON to file")))
                .build());

        // Кнопка "Edit" в правом верхнем углу
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Edit"), button -> openJsonEditorDialog())
                .position(this.width - 90, 10)
                .size(80, 20)
                .tooltip(Tooltip.of(Text.literal("Edit JSON content")))
                .build());

        // Загружаем JSON по умолчанию в редактор
        loadJson();
    }

    private void loadJson() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            Optional<Resource> resourceOptional = client.getResourceManager().getResource(new Identifier("ppl34guide", "gen_info.json"));

            if (resourceOptional.isPresent()) {
                InputStream inputStream = resourceOptional.get().getInputStream();
                String jsonText = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                Gson gson = new Gson();
                this.jsonObject = gson.fromJson(jsonText, JsonObject.class);
                updateJsonEditorField();
            } else {
                MinecraftClient.getInstance().player.sendMessage(Text.literal("Resource not found."), false);
            }
        } catch (IOException e) {
            e.printStackTrace();
            MinecraftClient.getInstance().player.sendMessage(Text.literal("Error loading JSON file."), false);
        }
    }

    private void saveJson() {
        // Получаем путь из поля ввода и убираем ведущий "/"
        String path = this.jsonPathField.getText().trim();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        // Определяем директорию для сохранения и создаем её, если она не существует
        File saveDir = new File(MinecraftClient.getInstance().runDirectory, "config/ppl34guide");
        if (!saveDir.exists() && !saveDir.mkdirs()) {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("Failed to create save directory."), false);
            return;
        }

        // Определяем файл для сохранения
        File file = new File(saveDir, path);

        // Печатаем путь для отладки
        MinecraftClient.getInstance().player.sendMessage(Text.literal("Saving to: " + file.getAbsolutePath()), false);

        try (FileWriter fileWriter = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(this.jsonObject, fileWriter);
            MinecraftClient.getInstance().player.sendMessage(Text.literal("JSON file saved."), false);
        } catch (IOException e) {
            e.printStackTrace();
            MinecraftClient.getInstance().player.sendMessage(Text.literal("Error saving JSON file: " + e.getMessage()), false);
        }
    }




    private void openJsonEditorDialog() {
        JTextArea textArea = new JTextArea();
        textArea.setText(this.jsonEditorField.getText());
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(textArea);

        int option = JOptionPane.showConfirmDialog(null, scrollPane, "Edit JSON", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            this.jsonObject = JsonParser.parseString(textArea.getText()).getAsJsonObject();
            updateJsonEditorField();
        }
    }

    private void updateJsonEditorField() {
        Gson gson = new Gson();
        String jsonText = gson.toJson(this.jsonObject);
        this.jsonEditorField.setText(jsonText);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        this.jsonPathField.render(context, mouseX, mouseY, delta);
        this.jsonEditorField.render(context, mouseX, mouseY, delta);
    }
}

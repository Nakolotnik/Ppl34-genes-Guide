import sys
import json
import os
from PyQt5.QtWidgets import (
    QApplication, QWidget, QVBoxLayout, QHBoxLayout, QListWidget, QPushButton, QMessageBox, QFileDialog, QInputDialog
)
from PyQt5.QtGui import QFont
from PyQt5.QtCore import Qt

class JsonEditor(QWidget):
    def __init__(self):
        super().__init__()
        self.initUI()
        self.json_data = None

    def initUI(self):
        self.setWindowTitle("Редактор JSON уровней")
        self.setGeometry(100, 100, 700, 780)

        # Main layout
        layout = QVBoxLayout()

        # Button layout for loading JSON files
        button_layout = QHBoxLayout()
        
        self.load_button = QPushButton("Загрузить JSON файл")
        self.load_button.clicked.connect(self.load_json)
        button_layout.addWidget(self.load_button)
        
        layout.addLayout(button_layout)

        # Genes List
        self.genes_list = QListWidget()
        self.genes_list.itemClicked.connect(self.display_levels)
        layout.addWidget(self.genes_list)

        # Levels List
        self.levels_list = QListWidget()
        self.levels_list.itemDoubleClicked.connect(self.edit_level)
        layout.addWidget(self.levels_list)

        # Button layout for marking and clearing
        button_layout = QHBoxLayout()

        self.dom_button = QPushButton("Доминантный")
        self.dom_button.clicked.connect(lambda: self.mark_level("§4", "§r"))
        button_layout.addWidget(self.dom_button)

        self.rec_button = QPushButton("Рецессивный")
        self.rec_button.clicked.connect(lambda: self.mark_level("§2", "§r"))
        button_layout.addWidget(self.rec_button)

        self.chaos_button = QPushButton("Хаос")
        self.chaos_button.clicked.connect(lambda: self.mark_level("§5", "§r"))
        button_layout.addWidget(self.chaos_button)

        self.clear_button = QPushButton("Очистить теги")
        self.clear_button.clicked.connect(self.clear_tags)
        button_layout.addWidget(self.clear_button)

        self.add_block_button = QPushButton("Добавить новый блок")
        self.add_block_button.clicked.connect(self.add_block)
        button_layout.addWidget(self.add_block_button)

        layout.addLayout(button_layout)

        # Save Button
        save_button = QPushButton("Сохранить уровни")
        save_button.clicked.connect(self.save_levels)
        layout.addWidget(save_button)

        # Set layout and apply styles
        self.setLayout(layout)
        self.setStyleSheet("""
            QWidget {
                background-color: #f7f7f7;
            }
            QPushButton {
                font-size: 14px;
                padding: 10px;
                border: none;
                background-color: #ffffff;
                color: #333333;
                border-radius: 5px;
                margin: 5px;
            }
            QPushButton:hover {
                background-color: #e0e0e0;
            }
            QPushButton:pressed {
                background-color: #cccccc;
            }
            QListWidget {
                font-size: 14px;
                background-color: #ffffff;
                border: 1px solid #cccccc;
                padding: 5px;
                border-radius: 5px;
                margin: 5px;
            }
            QScrollBar:vertical {
                border: none;
                background: #f7f7f7;
                width: 12px;
                margin: 5px 0 5px 0;
                border-radius: 6px;
            }
            QScrollBar::handle:vertical {
                background: #cccccc;
                min-height: 20px;
                border-radius: 6px;
            }
            QScrollBar::handle:vertical:hover {
                background: #aaaaaa;
            }
            QScrollBar::add-line:vertical, QScrollBar::sub-line:vertical {
                background: none;
                border: none;
            }
            QScrollBar::add-page:vertical, QScrollBar::sub-page:vertical {
                background: none;
            }
            QInputDialog {
                background-color: #ffffff;
            }
        """)

        # Fonts
        font = QFont("Arial", 12)
        self.setFont(font)

    def load_json(self):
        file_name, _ = QFileDialog.getOpenFileName(self, "Открыть JSON файл", "", "JSON Files (*.json)")
        if not file_name:
            return

        try:
            with open(file_name, "r", encoding="utf-8") as file:
                self.json_data = json.load(file)
            self.populate_genes_list()
        except Exception as e:
            QMessageBox.critical(self, "Ошибка", f"Не удалось загрузить JSON файл: {e}")

    def load_json_from_folder(self):
        folder_path = QFileDialog.getExistingDirectory(self, "Выберите папку с JSON файлами")
        if not folder_path:
            return

        try:
            self.json_data = {"genes": []}
            for file_name in os.listdir(folder_path):
                if file_name.endswith(".json"):
                    file_path = os.path.join(folder_path, file_name)
                    with open(file_path, "r", encoding="utf-8") as file:
                        data = json.load(file)
                        self.json_data["genes"].extend(data.get("genes", []))
            self.populate_genes_list()
        except Exception as e:
            QMessageBox.critical(self, "Ошибка", f"Не удалось загрузить JSON файлы из папки: {e}")

    def populate_genes_list(self):
        self.genes_list.clear()
        for gene in self.json_data.get("genes", []):
            self.genes_list.addItem(gene["label"])

    def display_levels(self, item):
        selected_gene_label = item.text()
        self.levels_list.clear()
        for gene in self.json_data["genes"]:
            if gene["label"] == selected_gene_label:
                for level in gene["levels"]:
                    self.levels_list.addItem(level)
                break

    def mark_level(self, start_tag, end_tag):
        current_item = self.levels_list.currentItem()
        if current_item is not None:
            current_text = current_item.text()
            if not (current_text.startswith(start_tag) and current_text.endswith(end_tag)):
                marked_text = f"{start_tag}{current_text}{end_tag}"
                current_item.setText(marked_text)
            else:
                QMessageBox.warning(self, "Предупреждение", "Эта строка уже помечена!")
        else:
            QMessageBox.warning(self, "Предупреждение", "Не выбрана строка!")

    def clear_tags(self):
        current_item = self.levels_list.currentItem()
        if current_item is not None:
            text = current_item.text()
            # Remove tags but keep text
            text = text.lstrip("§4").lstrip("§2").lstrip("§5").rstrip("§r")
            current_item.setText(text)
        else:
            QMessageBox.warning(self, "Предупреждение", "Не выбрана строка!")

    def edit_level(self, item):
        current_text = item.text()
        new_text, ok = QInputDialog.getText(self, "Редактировать уровень", "Редактировать уровень:", text=current_text)
        if ok and new_text:
            item.setText(new_text)

    def add_block(self):
        new_block_name, ok = QInputDialog.getText(self, "Добавить новый блок", "Введите название нового блока:")
        if ok and new_block_name:
            current_item = self.genes_list.currentItem()
            if current_item is not None:
                selected_gene_label = current_item.text()
                for gene in self.json_data["genes"]:
                    if gene["label"] == selected_gene_label:
                        gene["levels"].append(new_block_name)
                        self.display_levels(current_item)
                        break
            else:
                QMessageBox.warning(self, "Предупреждение", "Не выбран блок для добавления нового уровня!")

    def save_levels(self):
        current_item = self.genes_list.currentItem()
        if current_item is None:
            return

        levels_text = [self.levels_list.item(i).text() for i in range(self.levels_list.count())]
        selected_gene_label = current_item.text()

        for gene in self.json_data["genes"]:
            if gene["label"] == selected_gene_label:
                gene["levels"] = levels_text
                break

        file_name, _ = QFileDialog.getSaveFileName(self, "Сохранить JSON файл", "", "JSON Files (*.json)")
        if not file_name:
            return

        try:
            with open(file_name, "w", encoding="utf-8") as file:
                json.dump(self.json_data, file, ensure_ascii=False, indent=4)
            QMessageBox.information(self, "Успех", "JSON файл успешно сохранен!")
        except Exception as e:
            QMessageBox.critical(self, "Ошибка", f"Не удалось сохранить JSON файл: {e}")

if __name__ == '__main__':
    app = QApplication(sys.argv)
    editor = JsonEditor()
    editor.show()
    sys.exit(app.exec_())

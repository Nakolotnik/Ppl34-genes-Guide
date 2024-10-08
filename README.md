# Ppl34-genes-Guide

## Описание

`Ppl34-genes-Guide` — это гайд по генам из эксперимента 34 на приватном Minecraft сервере Pepeland. В этом моде описаны все доступные на данный момент гены и информация про них.

Для открытия окна по умолчанию используется клавиша `K`. Вы можете изменить эту клавишу в настройках игры. 


## Требования к моду

- **fabric loader** = 0.15.11
- **fabric api** = 0.100.8+1.20.6

## Редактирование JSON файла 
Для редактирования файла необходимо перейти по пути:

```bash
./minecraft/config/ppl34genes/gen_info.json
```

Файл имеет следующую структуру:

```json
{
  "genes": [
    {
      "label": "Пример названия",
      "tooltip": "Пример подсказки",
      "levels": [
        "Пример информации про ген 1",
        "Пример информации про ген 2",
        "Пример информации про ген 3"
      ],
      "icon": "путь к текстуре (его лучше не менять)",
      "levelIcons": [
        "также не стоит менять иконки здесь"
      ]
    }
  ]
}
```
## Описание полей
- label: Название гена, которое будет отображаться в интерфейсе. Убедитесь, что название ясно и точно описывает ген.

- tooltip: Подсказка, которая будет отображаться при наведении курсора на элемент. Подсказка должна содержать краткую и полезную информацию о гене.

- levels: Массив строк, где каждая строка описывает уровень гена. Например, если ген имеет несколько уровней или стадий, каждая строка будет содержать информацию о соответствующем уровне.

- icon: Путь к текстуре, которая будет использоваться для отображения иконки гена. Рекомендуется не изменять этот путь, чтобы избежать проблем с отображением.

- levelIcons: Массив путей к текстурам, которые используются для отображения иконок на различных уровнях гена. Эти иконки не рекомендуется менять, так как это может повлиять на визуализацию в интерфейсе.

## Рекомендации
1. Создавайте резервные копии: Прежде чем вносить изменения в JSON файл, создайте резервную копию оригинала, чтобы можно было восстановить его в случае необходимости.

2. Проверяйте синтаксис: После редактирования файла проверьте его на наличие синтаксических ошибок. Используйте JSON-валидатор для проверки правильности структуры.

3. Тестируйте изменения: После внесения изменений перезапустите приложение и проверьте, что все изменения корректно отобразились и функционируют как ожидалось.

4. Используйте актуальные пути и ресурсы: Убедитесь, что пути к текстурам и иконкам корректны и ресурсы действительно существуют по указанным путям.

5. Если вам нужно внести дополнительные изменения или добавить новые гены, следуйте указанной структуре и рекомендациям для обеспечения правильной работы вашего JSON файла.

## [Если хотите меня поддержать](https://www.donationalerts.com/r/Nakolotnik)

# Утилита фильтрации содержимого файлов.

---
### Stack:
- Java 17 (Azul Zulu 17.0.13). Она должна быть установлена на ПК в переменных среды.
- Maven 3.9.9
- Файлы `in1.txt` и `in2.txt`
---
## Инструкция по запуску

1. Клонируем репозиторий.
2. Открываем проект.
3. Открываем терминал и выполняем команду:
    ````bash
    mvn clean package
    ````
4. Открываем созданную папку `target` и видим собранный jar утилиты - `test_demo-1.0-SNAPSHOT.jar`.
5. Открываем терминал и переходим в директорию `target`.
   ````bash
   cd target
   ````
6. Вводим команду: 
   ````
   java -jar <файл утилиты .jar> -f -s -a -o <путь выходной директории> -p <префикс фильтрованных файлов> <file1.txt file2.txt>
   ````
- Файл утилиты это собранный jar файл в папке ``target``. 
- `-f` - опция вывода полной статистики фильтрации.
- `-s` - опция вывода краткой статистики фильтрации.
- `-a` - опция перезаписи. По умолчанию утилита дописывает результат, но если указать эту опцию, то файлы будут перезаписываться. 
- `-o` - опция выходного пути. По умолчанию утилита сохраняет результат в ту же директорию, где находиться сам файл утилиты.  
Если использовать путь от `/`, то результат будет сохранен в корне диска. Если использовать путь от `./`, то результат будет сохранен в корне директории, где находиться файл утилиты.
- `-p` - опция, позволяющая указать префикс результирующих файлов.
- `file1.txt file2.txt` - путь к файлам. Путь к файлам необходимо указывать абсолютный!

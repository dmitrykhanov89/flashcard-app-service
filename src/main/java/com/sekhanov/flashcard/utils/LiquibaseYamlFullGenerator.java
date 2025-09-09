package com.sekhanov.flashcard.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class LiquibaseYamlFullGenerator {

    public static void main(String[] args) throws IOException {
        List<String> topics = List.of(
                "Sports",
                "Animals",
                "Food",
                "Science",
                "Literature"
        );

        Map<String, List<Map<String, String>>> wordsByTopic = new HashMap<>();

        wordsByTopic.put("Sports", List.of(
                Map.of("term", "soccer", "definition", "футбол"),
                Map.of("term", "basketball", "definition", "баскетбол"),
                Map.of("term", "tennis", "definition", "теннис"),
                Map.of("term", "swimming", "definition", "плавание"),
                Map.of("term", "running", "definition", "бег"),
                Map.of("term", "cycling", "definition", "велоспорт"),
                Map.of("term", "gymnastics", "definition", "гимнастика"),
                Map.of("term", "volleyball", "definition", "волейбол"),
                Map.of("term", "hockey", "definition", "хоккей"),
                Map.of("term", "skiing", "definition", "лыжи"),
                Map.of("term", "boxing", "definition", "бокс"),
                Map.of("term", "wrestling", "definition", "борьба"),
                Map.of("term", "golf", "definition", "гольф"),
                Map.of("term", "fencing", "definition", "фехтование"),
                Map.of("term", "rugby", "definition", "регби"),
                Map.of("term", "badminton", "definition", "бадминтон"),
                Map.of("term", "karate", "definition", "карате"),
                Map.of("term", "archery", "definition", "стрельба из лука"),
                Map.of("term", "ski_jump", "definition", "прыжки на лыжах"),
                Map.of("term", "surfing", "definition", "серфинг")
        ));

        wordsByTopic.put("Animals", List.of(
                Map.of("term", "lion", "definition", "лев"),
                Map.of("term", "tiger", "definition", "тигр"),
                Map.of("term", "elephant", "definition", "слон"),
                Map.of("term", "giraffe", "definition", "жираф"),
                Map.of("term", "zebra", "definition", "зебра"),
                Map.of("term", "bear", "definition", "медведь"),
                Map.of("term", "wolf", "definition", "волк"),
                Map.of("term", "fox", "definition", "лиса"),
                Map.of("term", "deer", "definition", "олень"),
                Map.of("term", "rabbit", "definition", "кролик"),
                Map.of("term", "squirrel", "definition", "белка"),
                Map.of("term", "owl", "definition", "сова"),
                Map.of("term", "eagle", "definition", "орёл"),
                Map.of("term", "dolphin", "definition", "дельфин"),
                Map.of("term", "shark", "definition", "акула"),
                Map.of("term", "whale", "definition", "кит"),
                Map.of("term", "penguin", "definition", "пингвин"),
                Map.of("term", "kangaroo", "definition", "кенгуру"),
                Map.of("term", "panda", "definition", "панда"),
                Map.of("term", "camel", "definition", "верблюд")
        ));

        wordsByTopic.put("Food", List.of(
                Map.of("term", "bread", "definition", "хлеб"),
                Map.of("term", "milk", "definition", "молоко"),
                Map.of("term", "cheese", "definition", "сыр"),
                Map.of("term", "apple", "definition", "яблоко"),
                Map.of("term", "banana", "definition", "банан"),
                Map.of("term", "carrot", "definition", "морковь"),
                Map.of("term", "tomato", "definition", "помидор"),
                Map.of("term", "potato", "definition", "картофель"),
                Map.of("term", "chicken", "definition", "курица"),
                Map.of("term", "beef", "definition", "говядина"),
                Map.of("term", "fish", "definition", "рыба"),
                Map.of("term", "rice", "definition", "рис"),
                Map.of("term", "pasta", "definition", "паста"),
                Map.of("term", "egg", "definition", "яйцо"),
                Map.of("term", "soup", "definition", "суп"),
                Map.of("term", "cake", "definition", "торт"),
                Map.of("term", "cookie", "definition", "печенье"),
                Map.of("term", "honey", "definition", "мёд"),
                Map.of("term", "butter", "definition", "масло"),
                Map.of("term", "yogurt", "definition", "йогурт")
        ));

        wordsByTopic.put("Science", List.of(
                Map.of("term", "physics", "definition", "физика"),
                Map.of("term", "chemistry", "definition", "химия"),
                Map.of("term", "biology", "definition", "биология"),
                Map.of("term", "astronomy", "definition", "астрономия"),
                Map.of("term", "geology", "definition", "геология"),
                Map.of("term", "ecology", "definition", "экология"),
                Map.of("term", "genetics", "definition", "генетика"),
                Map.of("term", "anatomy", "definition", "анатомия"),
                Map.of("term", "botany", "definition", "ботаника"),
                Map.of("term", "zoology", "definition", "зоология"),
                Map.of("term", "meteorology", "definition", "метеорология"),
                Map.of("term", "optics", "definition", "оптика"),
                Map.of("term", "thermodynamics", "definition", "термодинамика"),
                Map.of("term", "quantum", "definition", "квантовая механика"),
                Map.of("term", "robotics", "definition", "робототехника"),
                Map.of("term", "microbiology", "definition", "микробиология"),
                Map.of("term", "neuroscience", "definition", "нейронаука"),
                Map.of("term", "ecology", "definition", "экология"),
                Map.of("term", "genome", "definition", "геном"),
                Map.of("term", "cell", "definition", "клетка")
        ));

        wordsByTopic.put("Literature", List.of(
                Map.of("term", "novel", "definition", "роман"),
                Map.of("term", "poem", "definition", "поэма"),
                Map.of("term", "story", "definition", "рассказ"),
                Map.of("term", "drama", "definition", "драма"),
                Map.of("term", "play", "definition", "пьеса"),
                Map.of("term", "author", "definition", "автор"),
                Map.of("term", "poet", "definition", "поэт"),
                Map.of("term", "character", "definition", "персонаж"),
                Map.of("term", "plot", "definition", "сюжет"),
                Map.of("term", "chapter", "definition", "глава"),
                Map.of("term", "narrative", "definition", "повествование"),
                Map.of("term", "fiction", "definition", "художественная литература"),
                Map.of("term", "novella", "definition", "повесть"),
                Map.of("term", "prologue", "definition", "пролог"),
                Map.of("term", "epilogue", "definition", "эпилог"),
                Map.of("term", "theme", "definition", "тема"),
                Map.of("term", "symbol", "definition", "символ"),
                Map.of("term", "metaphor", "definition", "метафора"),
                Map.of("term", "genre", "definition", "жанр"),
                Map.of("term", "literary_criticism", "definition", "литературная критика")
        ));

        String adminLogin = "user2";

        // Создаём файл и каталоги
        File file = new File("src/main/resources/db/changelog/012-add-flashcard-to-user2.yaml");
        file.getParentFile().mkdirs(); // создаём каталоги, если их нет

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("databaseChangeLog:\n");

            int setId = 1;
            for (String topic : topics) {
                // Генерация changeSet для набора
                writer.write(String.format(
                        "  - changeSet:\n" +
                                "      id: user2_flashcard_set_%d\n" +  // уникальный id для changeSet
                                "      author: dmitry.khanov\n" +
                                "      changes:\n" +
                                "        - insert:\n" +
                                "            tableName: flashcard_set\n" +
                                "            columns:\n" +
                                "              - column: { name: name, value: \"%s\" }\n" +
                                "              - column: { name: owner_id, valueComputed: \"(SELECT id FROM users WHERE login='%s')\" }\n",
                        setId++, topic, adminLogin
                ));

                List<Map<String, String>> words = wordsByTopic.getOrDefault(topic, Collections.emptyList());
                for (Map<String, String> word : words) {
                    writer.write(String.format(
                            "        - insert:\n" +
                                    "            tableName: cards\n" +
                                    "            columns:\n" +
                                    "              - column: { name: flashcard_set_id, valueComputed: \"(SELECT id FROM flashcard_set WHERE name='%s')\" }\n" +
                                    "              - column: { name: term, value: \"%s\" }\n" +
                                    "              - column: { name: definition, value: \"%s\" }\n",
                            topic, word.get("term"), word.get("definition")
                    ));
                }
            }
        }

        System.out.println("Liquibase YAML сгенерирован: " + file.getAbsolutePath());
    }
}


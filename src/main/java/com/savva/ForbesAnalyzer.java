package com.bodya;

import com.bodya.service.CsvLoader;
import com.bodya.service.SqliteRepository;
import com.bodya.service.StatsVisualizer;

import java.io.File;
import java.nio.file.Paths;

/**
 * Главный класс приложения для анализа данных Forbes.
 */
public class ForbesAnalyzer {
    public static void main(String[] args) {
        try {
            System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));
            System.setErr(new java.io.PrintStream(System.err, true, java.nio.charset.StandardCharsets.UTF_8));

            System.out.println(">>> Запуск анализатора данных Forbes...");

            // 1. Чтение данных из файла
            final var sourceFile = Paths.get("Forbes.csv");
            final var richList = CsvLoader.loadData(sourceFile);
            System.out.printf("Успешно прочитано %d записей из CSV%n", richList.size());

            // 2. Подготовка базы данных
            final var dbFile = new File("Forbes.db");
            if (dbFile.exists()) {
                boolean deleted = dbFile.delete();
                if (!deleted) System.err.println("Внимание: не удалось удалить старый файл БД.");
            }

            final var repository = new SqliteRepository();
            repository.initSchema();

            // 3. Сохранение данных
            repository.saveAll(richList);
            System.out.println("Данные успешно мигрированы в SQLite.");

            // 4. Аналитические выборки
            System.out.println("\n--- Результаты анализа ---");

            final var wealthStats = repository.getWealthByCountry();
            repository.findYoungestFrenchBillionaire();
            repository.findTopAmericanEnergyBillionaire();

            // 5. Визуализация
            final var visualizer = new StatsVisualizer();
            visualizer.createBarChart(wealthStats);
        } catch (Exception ex) {
            System.err.println("Критическая ошибка при выполнении программы:");
            ex.printStackTrace();
        }
    }
}
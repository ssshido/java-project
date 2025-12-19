package com.bodya.service;

import com.bodya.model.Billionaire;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
public class SqliteRepository {
    private static final String CONNECTION_STR = "jdbc:sqlite:Forbes.db";

    /**
     * Создание таблицы, если она отсутствует
     */
    public void initSchema() throws SQLException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STR);
             Statement stmt = conn.createStatement()) {

            String sql = """
                        CREATE TABLE IF NOT EXISTS billionaires (
                            id INTEGER PRIMARY KEY,
                            rank INTEGER,
                            full_name TEXT,
                            net_worth REAL,
                            age INTEGER,
                            country TEXT,
                            industry TEXT,
                            source TEXT
                        )
                    """;
            stmt.execute(sql);
        }
    }

    /**
     * Пакетная вставка данных
     */
    public void saveAll(List<Billionaire> list) throws SQLException {
        String insertSql = """
                    INSERT INTO billionaires (id, rank, full_name, net_worth, age, country, industry, source)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DriverManager.getConnection(CONNECTION_STR);
             PreparedStatement ps = conn.prepareStatement(insertSql)) {

            conn.setAutoCommit(false); // Для скорости

            for (int i = 0; i < list.size(); i++) {
                Billionaire t = list.get(i);
                ps.setLong(1, t.getDbId());
                ps.setInt(2, t.getGlobalRank());
                ps.setString(3, t.getFullName());
                ps.setDouble(4, t.getFortune());
                ps.setInt(5, t.getAge());
                ps.setString(6, t.getCitizenship());
                ps.setString(7, t.getSphere());
                ps.setString(8, t.getIncomeSource());

                ps.addBatch();

                if (i % 500 == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
            conn.commit();
        }
    }

    // Запрос 1: Статистика богатства по странам
    public Map<String, Double> getWealthByCountry() throws SQLException {
        Map<String, Double> stats = new LinkedHashMap<>();
        String query = """
                    SELECT country, SUM(net_worth) as total
                    FROM billionaires
                    GROUP BY country
                    ORDER BY total DESC
                """;

        try (Connection conn = DriverManager.getConnection(CONNECTION_STR);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n=== Общий капитал по странам (Топ) ===");
            while (rs.next()) {
                String country = rs.getString("country");
                double sum = rs.getDouble("total");
                stats.put(country, sum);
                System.out.printf("%-20s: $%.2f млрд%n", country, sum);
            }
        }
        return stats;
    }

    // Запрос 2: Молодой французский миллиардер
    public void findYoungestFrenchBillionaire() throws SQLException {
        String query = """
                    SELECT full_name, age, net_worth, source
                    FROM billionaires
                    WHERE country = 'France' AND net_worth > 10.0
                    ORDER BY age ASC
                    LIMIT 1
                """;

        System.out.println("\n=== Самый молодой миллиардер Франции (>$10 млрд) ===");
        try (Connection conn = DriverManager.getConnection(CONNECTION_STR);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                System.out.printf("Имя:       %s%n", rs.getString("full_name"));
                System.out.printf("Возраст:   %d лет%n", rs.getInt("age"));
                System.out.printf("Состояние: $%.2f млрд%n", rs.getDouble("net_worth"));
                System.out.printf("Источник:  %s%n", rs.getString("source"));
            } else {
                System.out.println("Записей не найдено.");
            }
        }
    }

    // Запрос 3: Энергетик из США
    public void findTopAmericanEnergyBillionaire() throws SQLException {
        String query = """
                    SELECT full_name, source, net_worth
                    FROM billionaires
                    WHERE country = 'United States' AND industry = 'Energy'
                    ORDER BY net_worth DESC
                    LIMIT 1
                """;

        System.out.println("\n=== Богатейший представитель энергетики США ===");
        try (Connection conn = DriverManager.getConnection(CONNECTION_STR);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                System.out.printf("Имя:       %s%n", rs.getString("full_name"));
                System.out.printf("Компания:  %s%n", rs.getString("source"));
                System.out.printf("Состояние: $%.2f млрд%n", rs.getDouble("net_worth"));
            } else {
                System.out.println("Записей не найдено.");
            }
        }
    }
}


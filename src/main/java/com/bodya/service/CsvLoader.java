package com.bodya.service;

import com.bodya.model.Billionaire;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvLoader {
    public static List<Billionaire> loadData(Path path) throws IOException {
        List<Billionaire> resultList = new ArrayList<>();

        // Настройка формата CSV
        var csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .build();

        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             CSVParser parser = CSVParser.parse(br, csvFormat)) {
            long counter = 1;

            for (CSVRecord row : parser) {
                try {
                    Billionaire t = new Billionaire();

                    // Парсинг полей с защитой от ошибок
                    t.setDbId(counter++);
                    t.setGlobalRank(parseInteger(row.get("Rank")));
                    t.setFullName(row.get("Name"));
                    t.setFortune(parseDouble(row.get("Networth")));
                    t.setAge(parseInteger(row.get("Age")));
                    t.setCitizenship(row.get("Country"));
                    t.setIncomeSource(row.get("Source"));
                    t.setSphere(row.get("Industry"));

                    resultList.add(t);
                } catch (NumberFormatException e) {
                    System.err.printf("Ошибка в строке %d: некорректный формат числа. Пропуск %n",
                            row.getRecordNumber());
                }
            }
        }
        return resultList;
    }

    private static int parseInteger(String val) {
        return Integer.parseInt(val.trim());
    }

    private static double parseDouble(String val) {
        return Double.parseDouble(val.trim());
    }
}

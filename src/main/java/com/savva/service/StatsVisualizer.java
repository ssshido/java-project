package com.bodya.service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class StatsVisualizer {
    public void createBarChart(Map<String, Double> dataMap) throws IOException {
        var dataset = new DefaultCategoryDataset();

        // Берем только топ-15 для чистоты графика
        dataMap.entrySet().stream()
                .limit(15)
                .forEach(e -> dataset.addValue(e.getValue(), "Состояние", e.getKey()));

        JFreeChart chart = ChartFactory.createBarChart(
                "ТОП-15 Стран по суммарному капиталу миллиардеров", // Заголовок
                "Страна",                                           // Ось X
                "Сумма (млрд $)",                                   // Ось Y
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        File outputFile = new File("wealth_statistics.png");
        ChartUtils.saveChartAsPNG(outputFile, chart, 1200, 900);

        System.out.println("\nГрафик успешно сгенерирован: " + outputFile.getAbsolutePath());
    }
}


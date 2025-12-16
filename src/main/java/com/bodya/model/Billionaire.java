package com.bodya.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Billionaire {
    private Long dbId;           // Внутренний ID базы данных

    private int globalRank;      // Позиция в рейтинге
    private String fullName;     // Имя и фамилия
    private double fortune;      // Состояние (млрд $)
    private int age;             // Возраст

    private String citizenship;  // Страна
    private String incomeSource; // Источник дохода
    private String sphere;       // Индустрия
}
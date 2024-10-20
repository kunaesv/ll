package org.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SignalAnalyzer {
    private static final int MAX_COEFFICIENTS = 1500;  // Максимальное количество коэффициентов
    private List<Double> coefficients;  // Список для хранения коэффициентов

    public SignalAnalyzer() {
        coefficients = new ArrayList<>();  // Инициализация списка коэффициентов
    }

    public void collectInitialCoefficients(List<Double> initialCoefficients) {
        for (double coefficient : initialCoefficients) {
            coefficients.add(coefficient);
        }
    }

    public double predictNextCoefficient() {
        if (coefficients.size() < 2) {
            return coefficients.size() > 0 ? coefficients.get(coefficients.size() - 1) : 0.0;  // Возвращаем последний коэффициент или 0, если нет данных
        }

        // Линейная регрессия: y = a * x + b
        int n = coefficients.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += coefficients.get(i);
            sumXY += i * coefficients.get(i);
            sumX2 += i * i;
        }

        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        // Предсказываем следующий коэффициент
        return slope * n + intercept;
    }

    public void addCoefficient(double coefficient) {
        if (coefficients.size() >= MAX_COEFFICIENTS) {
            System.out.println("Данные переполнены! Начните заново.");
            coefficients.clear();  // Очистка списка коэффициентов
        }
        coefficients.add(coefficient);
    }

    public List<Double> getCoefficients() {
        return coefficients;
    }
}

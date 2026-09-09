package com.qianjh.ryzen.util;

import java.time.LocalDate;

public class AgeUtils {

    public static final int DEFAULT_AGE = 18;

    public static boolean isAdult(LocalDate birthDate) {
        return isAdult(birthDate, DEFAULT_AGE);
    }

    public static boolean isAdult(LocalDate birthDate, int age) {
        if (birthDate == null) {
            return false;
        }

        LocalDate today = LocalDate.now();
        return !birthDate.plusYears(age).isAfter(today);
    }


    public static boolean isMinor(LocalDate birthDate) {
        return isMinor(birthDate, DEFAULT_AGE);
    }

    public static boolean isMinor(LocalDate birthDate, int age) {
        return !isAdult(birthDate, age);
    }
}

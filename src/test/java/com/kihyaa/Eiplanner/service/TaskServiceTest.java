package com.kihyaa.Eiplanner.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    @Test
    void t1(){
        LocalDate endDate = LocalDate.of(2023, 9, 12);
        LocalTime endTime = LocalTime.of(13, 0, 0);

        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();

        System.out.println("endDate = " + endDate);
        System.out.println("endTime = " + endTime);


        System.out.println("localDate = " + localDate);
        System.out.println("localDate = " + localTime);

        long tempDay = ChronoUnit.DAYS.between(localDate, endDate);
        long tempTime = ChronoUnit.HOURS.between(endTime, localTime);

        System.out.println("tempDay = " + tempDay*24);
        System.out.println("tempTime = " + tempTime);

    }

}
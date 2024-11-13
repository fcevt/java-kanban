package com.yandex.tracker.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class TaskTest {
//что экземпляры класса Task равны друг другу, если равен их id;
    @Test
    void tasksAreEqualWithEqualIdTest() {
        Task task = new Task("a","b");
        Task task2 = new Task("с","в");
        Assertions.assertEquals(task, task2, "Таски не равны");
    }
}
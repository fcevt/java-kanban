package com.yandex.tracker.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class EpicTest {


    //тест на то что экземпляры класса Epic равны друг другу, если равен их id;
    @Test
    void epicsAreEqualWithEqualIdTest() {
        Epic epic = new Epic("c", "d");
        Epic epic1 = new Epic("a", "b");
        Assertions.assertEquals(epic1, epic, "эпики не равны");
    }

}
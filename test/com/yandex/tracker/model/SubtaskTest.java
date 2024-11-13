package com.yandex.tracker.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class SubtaskTest {

//тест на равенство субтаск если равны их id
    @Test
    void subtasksAreEqualWithEqualIdTest() {
        Subtask subtask = new Subtask("a", "b");
        Subtask subtask1 = new Subtask("c", "d");
        Assertions.assertEquals(subtask1, subtask);
    }

}
package com.yandex.tracker.servise;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class ManagersTest {

//тест на то что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
    @Test
    void getDefaultTest() {
        Assertions.assertNotNull(Managers.getDefault());
    }

    @Test
    void getDefaultHistoryTest() {
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }
}
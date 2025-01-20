package com.yandex.tracker.servise;

class InMemoryTaskManagerTest extends TaskManagerTest {

    @Override
    TaskManager createTaskManager() {
        return Managers.getDefault();
    }


}
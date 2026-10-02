package com.example.microplanner.data

import com.example.microplanner.domain.model.DurationType
import com.example.microplanner.domain.model.Task
import java.util.UUID

object PredefinedTasks {
    fun getTasks(): List<Task> {
        return listOf(
            // Быстрые дела (до 15 мин)
            Task(UUID.randomUUID().toString(), "Протереть пыль с одной полки", DurationType.SHORT, 0, true, true),
            Task(UUID.randomUUID().toString(), "Вымыть свою кружку и тарелку", DurationType.SHORT, 0, true, true),
            Task(UUID.randomUUID().toString(), "Разобрать папку «Загрузки» (удалить 5 файлов)", DurationType.SHORT, 7, true, true),
            Task(UUID.randomUUID().toString(), "Сделать 10 приседаний или отжиманий", DurationType.SHORT, 0, true, true),
            Task(UUID.randomUUID().toString(), "Полить одно комнатное растение", DurationType.SHORT, 2, true, true),
            Task(UUID.randomUUID().toString(), "Протереть экран телефона или монитора", DurationType.SHORT, 0, true, true),
            
            // Средние дела (до 30 мин)
            Task(UUID.randomUUID().toString(), "Разобрать одну полку в шкафу с одеждой", DurationType.MEDIUM, 14, true, true),
            Task(UUID.randomUUID().toString(), "Почистить почту от спама и старых рассылок", DurationType.MEDIUM, 7, true, true),
            Task(UUID.randomUUID().toString(), "Пропылесосить одну комнату", DurationType.MEDIUM, 3, true, true),
            Task(UUID.randomUUID().toString(), "Составить список покупок на неделю", DurationType.MEDIUM, 7, true, true),
            Task(UUID.randomUUID().toString(), "Сделать легкую растяжку для спины и шеи", DurationType.MEDIUM, 0, true, true),
            Task(UUID.randomUUID().toString(), "Разобрать один ящик стола", DurationType.MEDIUM, 14, true, true)
        )
    }
}
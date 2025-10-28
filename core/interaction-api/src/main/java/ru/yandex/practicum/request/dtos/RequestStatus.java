package ru.yandex.practicum.request.dtos;

public enum RequestStatus {
    /**
     * Запрос ожидает подтверждения
     */
    PENDING,

    /**
     * Запрос подтвержден
     */
    CONFIRMED,

    /**
     * Запрос отклонен
     */
    REJECTED,

    /**
     * Запрос отменен
     */
    CANCELED
}

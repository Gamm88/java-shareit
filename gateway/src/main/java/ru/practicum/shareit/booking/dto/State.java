package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.exceptions.ValidatorExceptions;

public enum State {
	// Все
	ALL,
	// Текущие
	CURRENT,
	// Будущие
	FUTURE,
	// Завершенные
	PAST,
	// Отклоненные
	REJECTED,
	// Ожидающие подтверждения
	WAITING;

	private static final State[] copyOfValues = values();

	public static State getStateOrValidatorExceptions(String state) {
		for (State enumState : copyOfValues) {
			if (enumState.name().equals(state)) {
				return enumState;
			}
		}
		throw new ValidatorExceptions("Unknown state: " + state);
	}
}
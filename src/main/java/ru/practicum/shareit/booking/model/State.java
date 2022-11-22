package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exception.ValidatorExceptions;


public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

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





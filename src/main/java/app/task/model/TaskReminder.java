package app.task.model;

import lombok.Getter;

@Getter
public enum TaskReminder {
    NONE(0),
    THIRTY_MINUTES(30),
    ONE_HOUR(60),
    ONE_DAY(1440),
    TWO_DAYS(2880),
    ONE_WEEK(10080);

    private final int minutes;

    TaskReminder(int minutes) {
        this.minutes = minutes;
    }

}

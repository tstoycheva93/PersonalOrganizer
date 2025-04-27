package app.utils;

import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarDay {
    private LocalDate date;
    private int taskCount;
    private boolean isToday;
    private boolean isSelected;
    private boolean isEmpty;
}

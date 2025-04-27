package app.utils;

import app.task.service.TaskService;
import app.user.model.User;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Component
public class CalendarUtils {
    private final TaskService taskService;

    public CalendarUtils(TaskService taskService) {
        this.taskService = taskService;
    }

    public List<CalendarDay> generateCalendarDays(int year, int month, LocalDate selectedDate, User user) {
        List<CalendarDay> calendarDays = new ArrayList<>();
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = LocalDate.of(year, month, 1);
        DayOfWeek dayOfWeek = firstOfMonth.getDayOfWeek();
        int emptyDays = dayOfWeek.getValue()-1;
        for (int i = 0; i < emptyDays; i++) {
            calendarDays.add(new CalendarDay(null, 0, false, false,true));
        }
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            boolean isToday = date.equals(today);
            boolean isSelected = date.equals(selectedDate);
            int taskCount = getTaskCountForDate(date,user);
            calendarDays.add(new CalendarDay(date, taskCount, isToday, isSelected,true));
        }
        return calendarDays;
    }


    private int getTaskCountForDate(LocalDate date,User user) {
        return taskService.getTaskCountByDateAndUser(date,user);
    }
}

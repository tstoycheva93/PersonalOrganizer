package app.utils;

import app.task.service.TaskService;
import app.user.model.User;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        int emptyDays = dayOfWeek.getValue() - 1;
        Map<LocalDate, Integer> taskCounts = getMonthlyTaskCounts(year, month, user);
        for (int i = 0; i < emptyDays; i++) {
            calendarDays.add(new CalendarDay(null, 0, false, false, true));
        }
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            boolean isToday = date.equals(today);
            boolean isSelected = date.equals(selectedDate);
            int taskCount = taskCounts.getOrDefault(date, 0);
            calendarDays.add(new CalendarDay(date, taskCount, isToday, isSelected, true));
        }

        return calendarDays;
    }

    private Map<LocalDate, Integer> getMonthlyTaskCounts(int year, int month, User user) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();
        return taskService.getTaskCountsBetweenDatesAndUser(start, end, user);
    }

}

package app.utils;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentActivity {
    private LocalDate date;
    private String username;
    private String activity;
    private String description;
}

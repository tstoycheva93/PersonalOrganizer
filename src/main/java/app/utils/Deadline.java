package app.utils;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Deadline {
    private UUID id;
    private String title;
    private String category;
    private String dueDate;
    private String priority;
    private String status;

}

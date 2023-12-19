package org.example;

import lombok.*;
import org.example.enums.ProfileEnum;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
    private Long id;
    private String fullName;
    private ProfileEnum step;
}

package com.course.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
public class Course {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String metadata;
}

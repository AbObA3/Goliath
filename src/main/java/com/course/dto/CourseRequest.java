package com.course.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CourseRequest {

    @JsonProperty
    private String title;

    @JsonProperty
    private String description;

    @JsonProperty
    private String imageUrl;

    @JsonProperty
    private String metadata;
}

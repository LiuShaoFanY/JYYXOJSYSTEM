package com.schall.jyyx.model.entity;

import lombok.Data;

@Data
public class FeedbackAndGuidance {
    private Long questionSubmitId;
    private String personalizedFeedback;
    private String teachingGuidance;
}
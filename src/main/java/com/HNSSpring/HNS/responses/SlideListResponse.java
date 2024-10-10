package com.HNSSpring.HNS.responses;

import com.HNSSpring.HNS.models.Slide;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class SlideListResponse {
    private List<SlideResponse> slides;
    private int totalPages;
}

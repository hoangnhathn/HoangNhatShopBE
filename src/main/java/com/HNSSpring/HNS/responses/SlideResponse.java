package com.HNSSpring.HNS.responses;

import com.HNSSpring.HNS.models.Product;
import com.HNSSpring.HNS.models.Slide;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SlideResponse {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("link")
    private String link;

    public static SlideResponse fromSlide(Slide slide){
        SlideResponse slideResponse = SlideResponse
                .builder()
                .id(slide.getId()) // Cập nhật phương thức để thiết lập id
                .imageUrl(slide.getImageUrl())
                .link(slide.getLink())
                .build();

        return slideResponse;
    }

}

package com.HNSSpring.HNS.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SlideDTO {
    @JsonProperty("image_url")
    private String imageUrl;

    private String link;
}

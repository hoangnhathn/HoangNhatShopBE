package com.HNSSpring.HNS.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageResponse {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("image_url")
    private String imageUrl;
}

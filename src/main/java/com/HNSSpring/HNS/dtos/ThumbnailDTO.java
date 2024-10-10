package com.HNSSpring.HNS.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThumbnailDTO {
    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;
}

package com.HNSSpring.HNS.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "slides")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Slide {
    public static final int MAXIMUM_IMAGES_SLIDE = 1;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "link", length = 255)
    private String link;

}

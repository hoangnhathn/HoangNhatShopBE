package com.HNSSpring.HNS.services.Slide;
import com.HNSSpring.HNS.dtos.SlideDTO;
import com.HNSSpring.HNS.models.Slide;
import com.HNSSpring.HNS.responses.SlideResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ISlideService {
     Page<SlideResponse> getSlides(Pageable pageable);
     Slide getSlideById(Integer id) throws Exception;
     Slide createSlide(SlideDTO slideDTO) throws Exception;
     Slide updateSlide(Slide slide);
     void deleteSlide(Integer id) throws Exception;
}

package com.HNSSpring.HNS.services.Slide;

import com.HNSSpring.HNS.dtos.SlideDTO;
import com.HNSSpring.HNS.exception.DataNotFoundException;
import com.HNSSpring.HNS.models.Slide;
import com.HNSSpring.HNS.repositories.SlideRepository;
import com.HNSSpring.HNS.responses.SlideResponse;
import com.HNSSpring.HNS.services.Slide.ISlideService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class SlideService implements ISlideService {
    private final SlideRepository slideRepository;

    @Value("${upload.dir}/slides")
    private String uploadDir;

    @Override
    public Page<SlideResponse> getSlides(Pageable pageable){
        Page<Slide> slidePage = slideRepository.findAll(pageable);
        return slidePage.map(SlideResponse::fromSlide);
    }


    @Override
    public Slide createSlide(SlideDTO slideDTO) throws Exception {
        Slide slide = Slide.builder()
                .imageUrl(slideDTO.getImageUrl())
                .link(slideDTO.getLink())
                .build();
        return slideRepository.save(slide);
    }

    @Override
    public Slide getSlideById(Integer id) throws Exception {
        return slideRepository.findById(id)
                .orElseThrow(() -> new Exception("Slide not found"));
    }

    @Override
    @Transactional
    public Slide updateSlide(Slide slide) {
        return slideRepository.save(slide);
    }
    @Override
    @Transactional
    public void deleteSlide(Integer id) throws Exception{
        Slide slide = slideRepository.findById(id)
                .orElseThrow(()-> new DataNotFoundException("Cannot find slide image with id: " + id));
        slideRepository.deleteById(id);
        deleteFile(slide.getImageUrl());
    }

    private void deleteFile(String filename) {
        if (filename == null || filename.isEmpty()) {
            // Log lỗi hoặc xử lý theo yêu cầu
            System.err.println("Filename is null or empty. Cannot delete file.");
            return;
        }

        try {
            Path filePath = Paths.get(uploadDir, filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace(); // Xử lý lỗi hoặc ghi log theo yêu cầu
        }
    }
}

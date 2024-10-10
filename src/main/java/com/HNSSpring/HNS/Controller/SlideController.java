package com.HNSSpring.HNS.Controller;


import com.HNSSpring.HNS.dtos.SlideDTO;
import com.HNSSpring.HNS.models.Slide;
import com.HNSSpring.HNS.responses.SlideListResponse;
import com.HNSSpring.HNS.responses.SlideResponse;
import com.HNSSpring.HNS.services.Slide.SlideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j // Thêm annotation này nếu bạn đang dùng Lombok
@RestController
@RequestMapping("${api.prefix}/slides")
@RequiredArgsConstructor
public class SlideController {
    private final SlideService slideService;
    @Value("${upload.dir}/slides")
    private String uploadDir;

    @GetMapping("")
    public ResponseEntity<SlideListResponse> getSlides(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").ascending());
        Page<SlideResponse> slidePage = slideService.getSlides(pageRequest);
        int totalPages = slidePage.getTotalPages();
        List<SlideResponse> slides = slidePage.getContent();
        return ResponseEntity.ok(SlideListResponse
                .builder()
                        .slides(slides)
                        .totalPages(totalPages)
                .build());
    }
    @PostMapping("")
    public ResponseEntity<?> createSlide(
            @Valid @RequestBody SlideDTO slideDTO,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }

        try {
            Slide newSlide = slideService.createSlide(slideDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(newSlide);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Integer slideId,
            @ModelAttribute("files") List<MultipartFile> files
    ) {
        try {
            Slide existingSlide = slideService.getSlideById(slideId);

            if (files == null || files.isEmpty()) {
                return ResponseEntity.badRequest().body("No files uploaded");
            }

            if (files.size() > Slide.MAXIMUM_IMAGES_SLIDE) {
                return ResponseEntity.badRequest().body("You can only upload a maximum of " + Slide.MAXIMUM_IMAGES_SLIDE + " images");
            }

            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }

                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File is too large");
                }

                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image");
                }

                String filename = storeFile(file);
                existingSlide.setImageUrl(filename);
                slideService.updateSlide(existingSlide);
            }

            return ResponseEntity.ok(existingSlide);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid image format");
        }

        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;

        Path uploadDirPath = Paths.get(uploadDir);

        if (!Files.exists(uploadDirPath)) {
            Files.createDirectories(uploadDirPath);
        }

        Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFilename;
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName){
        try{
            Path imagePath = Paths.get("uploads/slides/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());
            if (resource.exists()){
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpg").toUri()));
            }

        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSlide(@PathVariable Integer id) throws Exception {
        log.info("Request to delete slide with ID: {}", id);
        try {
            slideService.deleteSlide(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Xóa thành công");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Error deleting slide with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred");
        }
    }
}

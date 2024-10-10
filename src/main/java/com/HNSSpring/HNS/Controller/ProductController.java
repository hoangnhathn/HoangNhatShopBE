package com.HNSSpring.HNS.Controller;

import com.HNSSpring.HNS.components.LocalizationUtils;
import com.HNSSpring.HNS.dtos.ProductDTO;
import com.HNSSpring.HNS.dtos.ProductImageDTO;
import com.HNSSpring.HNS.dtos.ThumbnailDTO;
import com.HNSSpring.HNS.exception.DataNotFoundException;
import com.HNSSpring.HNS.models.Product;
import com.HNSSpring.HNS.models.ProductImage;
import com.HNSSpring.HNS.responses.ProductListResponse;
import com.HNSSpring.HNS.responses.ProductResponse;
import com.HNSSpring.HNS.services.Product.IProductService;
import com.HNSSpring.HNS.utils.MessageKeys;
import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;
    private final LocalizationUtils localizationUtils;
    @Value("${upload.dir}")
    private String uploadDir;
    @GetMapping("") // localhost:8080/api/v1/products?page=1&limit=10
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sortField, // Thêm trường sắp xếp
            @RequestParam(defaultValue = "asc") String sortDirection // Thêm hướng sắp xếp
    ){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        //tạo pageable từ trang và giới hạn
        PageRequest pageRequest = PageRequest.of(page,limit, sort);
        Page<ProductResponse> productPage = productService.getAllProducts(keyword,categoryId,pageRequest);
        //lấy tổng số trang
        int totalPages = productPage.getTotalPages();
        List<ProductResponse> products = productPage.getContent();

        return ResponseEntity.ok(ProductListResponse
                .builder()
                .products(products)
                .totalPages(totalPages)
                .build());

    }


    @PostMapping(value = "")
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody ProductDTO productDTO,

            //@RequestPart("file") MultipartFile file,
            BindingResult result
            ){
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Product newProduct = productService.createProduct(productDTO);
            return ResponseEntity.ok(newProduct);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "uploads/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Integer productId,
            @ModelAttribute("files")
            List<MultipartFile> files){
        try {
            Product existingProduct = productService.getProductById(productId);
            files = files == null ? new ArrayList<MultipartFile>() : files;
            if (files.size() >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT){
                return ResponseEntity.badRequest().body(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_MAX) + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT );
            }
            List<ProductImage> productImages = new ArrayList<>();
            for(MultipartFile file:files){
                if (file.getSize() == 0){
                    continue;
                }
                //kiểm tra kích thước file và định dạng
                if (file.getSize() > 10 * 1024 * 1024) //kích thước > 10mb
                {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("file is too large");
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image");
                }
                //Lưu file và cập nhật thumbnail trong DTO
                String filename = storeFile(file);
                //lưu vào đối tượng product trong DB
                ProductImage productImage = productService.createProductImage(
                        existingProduct.getId(),
                        ProductImageDTO
                                .builder()
                                .imageUrl(filename)
                                .build()
                );
                productImages.add(productImage);
            }
            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String storeFile(MultipartFile file) throws IOException {
        if (!isImageFile(file) || file.getOriginalFilename()==null){
            throw new IOException("Invalid image format");
        }
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        //Thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String uniqueFilename = UUID.randomUUID().toString()+"_"+filename;
        // Đường dẫn đến thư mục mà bạn lưu file
        Path uploadDirPath = Paths.get(uploadDir);
        //kiểm tra và tạo thư mục nếu nó ko tồn tại
        if (!Files.exists(uploadDirPath)){
            Files.createDirectories(uploadDirPath);
        }
        //đường dẫn đầy đủ đến file
        Path destination = Paths.get(uploadDir.toString(),uniqueFilename);
        //sao chép file vào mục đích
        Files.copy(file.getInputStream(),destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    private boolean isImageFile(MultipartFile file){
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName){
        try{
            Path imagePath = Paths.get(uploadDir+"/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());
            if (resource.exists()){
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get(uploadDir+"/notfound.jpg").toUri()));
            }

        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Integer productId){
        try{
            Product existingProduct = productService.getProductById(productId);
            return ResponseEntity.ok(ProductResponse.formProduct(existingProduct));
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductsByIds(@RequestParam("ids") String ids){
        try{
            List<Integer> productIds = Arrays.stream(ids.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            List<ProductResponse> products = productService.findProductsByIds(productIds);
            return ResponseEntity.ok(products);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id){
        productService.deleteProduct(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", localizationUtils.getLocalizedMessage(MessageKeys.DELETE_ORDER_SUCCESSFULLY));
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/generateFakeProducts")
    public ResponseEntity<String> generateFakeProducts(){
        Faker faker = new Faker();
        for (int i = 0; i<100;i++){
            String productName = faker.commerce().productName();
            if (productService.existsByName(productName))
                continue;
            ProductDTO productDTO = ProductDTO
                    .builder()
                    .name(productName)
                    .price((double) faker.number().numberBetween(10, 90_000_000))
                    .thumbnail("")
                    .description(faker.lorem().sentence())
                    .categoryId(faker.number().numberBetween(1,3))
                    .build();
            try {
                productService.createProduct(productDTO);
            }catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Fake Products created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer id,
            @RequestBody ProductDTO productDTO) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(ProductResponse.formProduct(updatedProduct));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{productId}/upload-thumbnail")
    public ResponseEntity<Product> uploadThumbnail(
            @PathVariable Integer productId,
            @RequestBody ThumbnailDTO thumbnailDTO) throws DataNotFoundException {
        Product updatedProduct = productService.uploadThumbnail(productId, thumbnailDTO.getThumbnailUrl());
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/images/{productImageId}")
    public ResponseEntity<Void> deleteProductImage(@PathVariable Integer productImageId) throws DataNotFoundException {
        productService.deleteProductImage(productImageId);
        return ResponseEntity.noContent().build();
    }
}

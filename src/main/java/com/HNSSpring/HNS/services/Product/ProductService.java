package com.HNSSpring.HNS.services.Product;

import com.HNSSpring.HNS.dtos.ProductDTO;
import com.HNSSpring.HNS.dtos.ProductImageDTO;
import com.HNSSpring.HNS.exception.DataNotFoundException;
import com.HNSSpring.HNS.exception.InvalidParamException;
import com.HNSSpring.HNS.models.Category;
import com.HNSSpring.HNS.models.Product;
import com.HNSSpring.HNS.models.ProductImage;
import com.HNSSpring.HNS.repositories.CategoryRepository;
import com.HNSSpring.HNS.repositories.ProductImageRepository;
import com.HNSSpring.HNS.repositories.ProductRepository;
import com.HNSSpring.HNS.responses.ProductResponse;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    @Value("${upload.dir}")
    private String uploadDir; // Thay đổi đường dẫn theo cấu hình của bạn
    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(()->
                        new DataNotFoundException("Cannot find category with id: "+productDTO.getCategoryId()));
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(Integer productId) throws Exception {
        return productRepository.findById(productId)
                .orElseThrow(()->new DataNotFoundException("Cannot find product with id ="+productId));
    }

    @Override
    public List<ProductResponse> findProductsByIds(List<Integer> productIds) {
        List<Product> products = productRepository.findAllById(productIds);
        return products.stream()
                .map(ProductResponse::formProduct)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductResponse> getAllProducts(
            String keyword,
            Integer categoryId,
            Pageable pageable) {
        Page<Product> productsPage = productRepository.searchProducts(keyword,categoryId, pageable);
        return productsPage.map(ProductResponse::formProduct);
    }

    @Override
    @Transactional
    public Product updateProduct(
            Integer id,
            ProductDTO productDTO
    )
            throws Exception {
        Product existingProduct = getProductById(id);
        if (existingProduct != null) {
            //copy các thuộc tính từ DTO -> Product
            //Có thể sử dụng ModelMapper
            Category existingCategory = categoryRepository
                    .findById(productDTO.getCategoryId())
                    .orElseThrow(()->
                            new DataNotFoundException("Cannot find category with id: "+productDTO.getCategoryId()));
            existingProduct.setName(productDTO.getName());
            existingProduct.setCategory(existingCategory);
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setThumbnail(existingProduct.getThumbnail());
            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteProduct(Integer id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(productRepository::delete);
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    @Transactional
    public ProductImage createProductImage(
            Integer productId,
            ProductImageDTO productImageDTO) throws Exception {
        Product existingProduct = productRepository
                .findById(productId)
                .orElseThrow(()->
                        new DataNotFoundException("Cannot find category with id: "+productImageDTO.getProductId()));
        ProductImage newProductImage = ProductImage
                .builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        //ko cho insert quá 5 ảnh 1 sản phẩm
        int size = productImageRepository.findByProductId(productId).size();
        if (size>ProductImage.MAXIMUM_IMAGES_PER_PRODUCT){
            throw new InvalidParamException(
                    "Number of images must be <= "
                    +ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        return productImageRepository.save(newProductImage);
    }

    @Override
    @Transactional
    public Product uploadThumbnail(Integer productId, String thumbnailUrl) throws DataNotFoundException {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find product with id: " + productId));
        existingProduct.setThumbnail(thumbnailUrl);
        return productRepository.save(existingProduct);
    }

    @Transactional
    @Override
    public void deleteProductImage(Integer productImageId) throws DataNotFoundException {
        ProductImage existingProductImage = productImageRepository.findById(productImageId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find product image with id: " + productImageId));

        productImageRepository.delete(existingProductImage);
        deleteFile(existingProductImage.getImageUrl());
    }
    private void deleteFile(String filename) {
        try {
            Path filePath = Paths.get(uploadDir, filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace(); // Xử lý lỗi hoặc ghi log theo yêu cầu
        }
    }
}

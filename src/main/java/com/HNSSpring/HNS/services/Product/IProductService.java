package com.HNSSpring.HNS.services.Product;

import com.HNSSpring.HNS.dtos.ProductDTO;
import com.HNSSpring.HNS.dtos.ProductImageDTO;
import com.HNSSpring.HNS.dtos.ThumbnailDTO;
import com.HNSSpring.HNS.exception.DataNotFoundException;
import com.HNSSpring.HNS.exception.InvalidParamException;
import com.HNSSpring.HNS.models.Product;
import com.HNSSpring.HNS.models.ProductImage;
import com.HNSSpring.HNS.responses.ProductResponse;
import org.springframework.data.domain.*;

import java.util.List;

public interface IProductService {
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException;
    Product getProductById(Integer id) throws Exception;
    Page<ProductResponse> getAllProducts(String keyword,
                                         Integer categoryId,
                                         Pageable pageable);
    Product updateProduct(Integer id, ProductDTO productDTO) throws Exception;
    void deleteProduct(Integer id);
    boolean existsByName(String name);

    ProductImage createProductImage(
            Integer productId,
            ProductImageDTO productImageDTO) throws Exception;

    List<ProductResponse> findProductsByIds(List<Integer> productIds);


    Product uploadThumbnail(Integer productId, String thumbnailUrl) throws DataNotFoundException;

    void deleteProductImage(Integer productImageId) throws DataNotFoundException;
}

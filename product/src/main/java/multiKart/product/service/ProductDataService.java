package multiKart.product.service;
import multiKart.product.model.ApplicationResponse;
import multiKart.product.model.Product;

import java.util.List;

public interface ProductDataService
{
    ApplicationResponse getCategories();

    ApplicationResponse getProductsByCategory(String category);

    ApplicationResponse<Product> searchProducts(String keyword);

    List<Product> getProductsAll();


    ApplicationResponse getProductById(String id);

    boolean isVariantIdAvailable(String productId, int variantId);


    boolean deleteVariantId(String productId, int variantId);

    ApplicationResponse getProductByVariantId(String productId, int variantId);


}

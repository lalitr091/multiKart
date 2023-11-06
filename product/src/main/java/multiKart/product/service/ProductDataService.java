package multiKart.product.service;
import multiKart.product.model.ApplicationResponse;
import multiKart.product.model.Product;

public interface ProductDataService
{
    ApplicationResponse getCategories();

    ApplicationResponse getProductsByCategory(String category);

    ApplicationResponse<Product> searchProducts(String keyword);

    ApplicationResponse getProductsAll();
}

package multiKart.product.controller;

import multiKart.product.model.ApplicationResponse;
import multiKart.product.service.ProductDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/multikart/product/v1")
public class RestfulServiceRouter {
    @Autowired
    ProductDataService productDataService;

    @Operation(
            summary = "Get all the categories available"
    )
    @Tag(name = "Categories")
    @GetMapping("/categories")
    public final ApplicationResponse GetCategory() {
        return productDataService.getCategories();
    }

    @Operation(
            summary = "Get all the product under category"
    )

    @Tag(name = "Products")
    @GetMapping("/products")
    public final ApplicationResponse getProducts(@RequestParam String category) {
        return productDataService.getProductsByCategory(category);
    }

    @Operation(
            summary = "Get all the products available"
    )
    @Tag(name = "Products")
    @GetMapping("/productsAll")
    public final ApplicationResponse GetProductsAll() {
        return productDataService.getProductsAll();
    }

    @Operation(
            summary = "Search the products by keyword in columns title,description,brand "
    )
    @Tag(name = "Products")
    @GetMapping("/search")
    public final ApplicationResponse searchProductsByName(@RequestParam String keyword) {
        return productDataService.searchProducts(keyword);
    }

}

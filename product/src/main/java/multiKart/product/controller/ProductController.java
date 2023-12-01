package multiKart.product.controller;
import multiKart.product.model.ApplicationResponse;
import multiKart.product.model.Product;
import multiKart.product.service.ProductDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/multikart/v1/product")
public class ProductController {
    @Autowired
    ProductDataService productDataService;

    @Operation(summary = "Get all the categories available")
    @Tag(name = "Categories")
    @GetMapping("/allcategories")
    public final ApplicationResponse GetCategory() {
        return productDataService.getCategories();
    }

    @Operation(summary = "Get all the product under category")
    @Tag(name = "Products")
    @GetMapping("/bycategories")
    public final ApplicationResponse getProducts(@RequestParam String category) {
        return productDataService.getProductsByCategory(category);
    }

    @Operation(summary = "Get all the products available")
    @Tag(name = "Products")
    @GetMapping("/all")
    public final List<Product> GetProductsAll() {
        return productDataService.getProductsAll();
    }

    @Operation(summary = "Search the products by keyword in columns title,description,brand ")
    @Tag(name = "Products")
    @GetMapping("/search")
    public final ApplicationResponse searchProductsByName(@RequestParam String keyword)
    {
        return productDataService.searchProducts(keyword);

    }
    @Operation(summary = "Get the products by it's id")
    @GetMapping("/byid")
    public final ApplicationResponse getProductsById(@RequestParam String id)
    {
        return productDataService.getProductById(id);
    }


    @GetMapping("/isVariantAvailable") //user in order MS
    public final boolean isVariantIdAvailable(
            @RequestParam String productId,
            @RequestParam int variantId) {
        return productDataService.isVariantIdAvailable(productId, variantId);
    }
    @Operation(summary = "Delete a variant ID mapping for a product") //used in order MS
    @Tag(name = "Products")
    @DeleteMapping("/deleteVariant")
    public final boolean deleteVariantId(
            @RequestParam String productId,
            @RequestParam int variantId) {
        return productDataService.deleteVariantId(productId, variantId);
    }
    @Operation(summary = "Get the products by product's id and varient's id") // create for cart MS to give custom response
    @GetMapping("/byvarientid")
    public final ApplicationResponse getProductsByVariantId( @RequestParam String productId,
                                                          @RequestParam int variantId)
    {
        return productDataService.getProductByVariantId(productId, variantId);
    }

}

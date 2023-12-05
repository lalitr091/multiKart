package multiKart.product.service;
import lombok.extern.slf4j.Slf4j;
import multiKart.product.Repository.CategoryRepo;
import multiKart.product.Repository.ProductRepo;
import multiKart.product.common.Constants;
import multiKart.product.model.ApplicationResponse;
import multiKart.product.model.Category;
import multiKart.product.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductDataServiceImpl implements ProductDataService {
    @Autowired
    CategoryRepo categoryRepo;
    @Autowired
    ProductRepo productRepo;

    @Override
    public ApplicationResponse getCategories() {
        ApplicationResponse result;
        try {
            List<Category> categoryData = categoryRepo.findAll();
            ApplicationResponse applicationResponse = new ApplicationResponse();
            applicationResponse.setStatus(Constants.OK);
            applicationResponse.setMessage(Constants.OK_MESSAGE);
            applicationResponse.setData(categoryData);
            result = applicationResponse;
        } catch (Exception e) {
            log.error("An error occurred while fetching categories", e);

            ApplicationResponse<Category> errorResponse = new ApplicationResponse<>();
            errorResponse.setStatus(Constants.INTERNAL_SERVER_ERROR);
            errorResponse.setMessage("An error occurred while fetching categories");
            errorResponse.setData(null);

            result = errorResponse;
        }
        return result;
    }


    @Override
    public ApplicationResponse<Product> getProductsByCategory(String category) {
        ApplicationResponse applicationResponse = new ApplicationResponse();

        try {
            List<Product> subCategoryData = productRepo.findByCategoryIgnoreCase(category);

            if (subCategoryData == null || subCategoryData.isEmpty()) {
                applicationResponse.setStatus(Constants.NO_CONTENT);
                applicationResponse.setMessage(Constants.NO_CONTENT_MESSAGE);
            } else {
                applicationResponse.setStatus(Constants.OK);
                applicationResponse.setMessage(Constants.OK_MESSAGE);
            }
            applicationResponse.setData(subCategoryData);
            return applicationResponse;
        } catch (Exception e) {
            log.error("An error occurred while fetching products by category", e);

            //ApplicationResponse errorResponse = new ApplicationResponse();
            applicationResponse.setStatus(Constants.INTERNAL_SERVER_ERROR);
            applicationResponse.setMessage("An error occurred while fetching products by category");
            applicationResponse.setData(null);
            return applicationResponse;
        }
    }

    @Override
    public List<Product> getProductsAll() {
        try {
            return productRepo.findAll();
        } catch (Exception e) {
            log.error("An error occurred while fetching all products", e);
            return Collections.emptyList();
        }
    }


    @Override
    public ApplicationResponse<Product> getProductById(String id) {
        try {
            List<Product> products = productRepo.findAllById(Collections.singleton(id));
            ApplicationResponse<Product> applicationResponse = new ApplicationResponse<>();
            applicationResponse.setStatus(Constants.OK);
            applicationResponse.setMessage(Constants.OK_MESSAGE);
            applicationResponse.setData(products);
            return applicationResponse;
        } catch (Exception e) {
            log.error("An error occurred while fetching product by ID", e);

            ApplicationResponse<Product> errorResponse = new ApplicationResponse<>();
            errorResponse.setStatus(Constants.INTERNAL_SERVER_ERROR);
            errorResponse.setMessage("An error occurred while fetching product by ID");
            errorResponse.setData(null);

            return errorResponse;
        }

    }

    @Override
    public void saveProduct(Product product) {
        productRepo.save(product);
    }

    @Override
    public ApplicationResponse<Product> searchProducts(String keyword) {
        ApplicationResponse applicationResponse = new ApplicationResponse();
        try {
            List<Product> searchResult = productRepo.findByTitleContaining(keyword);
            if (searchResult == null || searchResult.isEmpty()) {
                log.info("No products found for keyword: {}", keyword);

            }

            assert searchResult != null;
            log.info("{} Search Result found for keyword -> {}", searchResult.size(), keyword);
            applicationResponse.setStatus(Constants.OK);
            applicationResponse.setMessage(Constants.OK_MESSAGE);
            applicationResponse.setData(searchResult);

            return applicationResponse;
        } catch (Exception e) {
            log.error("An error occurred while searching for products", e);
            applicationResponse.setStatus(Constants.INTERNAL_SERVER_ERROR);
            applicationResponse.setMessage("An error occurred while searching for products");
            applicationResponse.setData(null);
            return applicationResponse;
        }
    }

    @Override
    public boolean isVariantIdAvailable(String productId, int variantId, int qty) {
        try {
            Product product = productRepo.findById(productId).orElse(null);

            if (product != null && product.getVariants() != null) {

                return product.getVariants().stream()
                        .filter(variant -> variant.getVariant_id() == variantId)
                        .anyMatch(variant -> variant.getVariant_stock_qty() >= qty);
            } else {
                if (product == null) {
                    log.warn("Product not found for productId: {}", productId);
                } else {
                    log.warn("Variants not found for productId: {}", productId);
                }
                return false;
            }
        } catch (Exception e) {
            log.error("An error occurred while checking variantId availability", e);
            return false;
        }
    }

    @Override
    public ApplicationResponse updateVariant(String productId, int variantId, int requestQty) {
        ApplicationResponse response = new ApplicationResponse();
        try {
            Product product = productRepo.findById(productId).orElse(null);

            if (product != null && product.getVariants() != null) {
                List<Product.Variant> variants = product.getVariants();


                for (Product.Variant variant : variants) {
                    if (variant.getVariant_id() == variantId) {
                        int currentQty = variant.getVariant_stock_qty();

                        if (requestQty > currentQty) // Check if the requested quantity is greater than the available stock
                        {
                            log.warn("Cannot update {} items for variantId {}. Available stock is only {}", requestQty, variantId, currentQty);

                            response.setStatus(Constants.OK);
                            response.setMessage("Cannot update variant quantity. Requested quantity exceeds available stock.");
                            response.setData(null);

                            return response;
                        }

                        variant.setVariant_stock_qty(currentQty - requestQty); // Update the variant stock quantity
                        product.setStock(product.getStock() - requestQty);  // Update the product stock
                        productRepo.save(product);
                        response.setStatus(Constants.OK);
                        response.setMessage(Constants.OK_MESSAGE);
                        response.setData(null);
                        return response;
                    }
                }

                log.warn("Variant not found for variantId: {} in productId: {}", variantId, productId);
                response.setStatus(Constants.NO_CONTENT);
                response.setMessage("Variant not found for given productId and variantId.");

            } else {
                log.warn("Product not found for productId: {}", productId);

                response.setStatus(Constants.NO_CONTENT);
                response.setMessage("Product not found for given productId.");

            }
            response.setData(null);
            return response;
        } catch (Exception e) {
            log.error("An error occurred while updating variant stock quantity", e);

            response.setStatus(Constants.INTERNAL_SERVER_ERROR);
            response.setMessage("An error occurred while updating variant stock quantity.");
            response.setData(null);

            return response;
        }
    }


    @Override
    public ApplicationResponse<Product> getProductByVariantId(String productId, int variantId) {
        try {
            Product product = productRepo.findById(productId).orElse(null);

            if (product != null && product.getVariants() != null) {
                List<Product.Variant> matchingVariants = product.getVariants().stream()
                        .filter(variant -> variant.getVariant_id() == variantId)
                        .collect(Collectors.toList());

                if (!matchingVariants.isEmpty()) {
                    product.setVariants(matchingVariants);
                    ApplicationResponse<Product> applicationResponse = new ApplicationResponse<>();
                    applicationResponse.setStatus(Constants.OK);
                    applicationResponse.setMessage(Constants.OK_MESSAGE);
                    // applicationResponse.setData((List<Product>) product);
                    applicationResponse.setData(Collections.singletonList(product));

                    return applicationResponse;
                } else {
                    log.warn("Variant not found for productId: {} and variantId: {}", productId, variantId);
                }
            } else {
                log.warn("Product or variants not found for productId: {}", productId);
            }

            // If no matching variants are found or product is not found, return an empty response
            ApplicationResponse<Product> emptyResponse = new ApplicationResponse<>();
            emptyResponse.setStatus(Constants.NO_CONTENT);
            emptyResponse.setMessage(Constants.NO_CONTENT_MESSAGE);
            emptyResponse.setData(null);
            return emptyResponse;

        } catch (Exception e) {
            log.error("An error occurred while fetching product by  variant ID", e);
            ApplicationResponse<Product> errorResponse = new ApplicationResponse<>();
            errorResponse.setStatus(Constants.INTERNAL_SERVER_ERROR);
            errorResponse.setMessage("An error occurred while fetching product by product ID and variant ID");
            errorResponse.setData(null);
            return errorResponse;
        }
    }


}







package multiKart.product.service;
import multiKart.product.Repository.CategoryRepo;
import multiKart.product.common.Constants;
import multiKart.product.model.ApplicationResponse;
import multiKart.product.model.Category;
import multiKart.product.model.Product;
import multiKart.product.Repository.ProductRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class  ProductDataServiceImpl implements ProductDataService
{
    @Autowired
    CategoryRepo categoryRepo;
    @Autowired
    ProductRepo productRepo;

    @Override
     public ApplicationResponse getCategories() {
        try {
            List<Category> categoryData = (ArrayList<Category>) categoryRepo.findAll();
            ApplicationResponse<Category> applicationResponse = new ApplicationResponse<>();
            applicationResponse.setStatus(Constants.OK);
            applicationResponse.setMessage(Constants.OK_MESSAGE);
            applicationResponse.setData(categoryData);
            return applicationResponse;
        } catch (Exception e) {
            log.error("An error occurred while fetching categories", e);

            ApplicationResponse<Category> errorResponse = new ApplicationResponse<>();
            errorResponse.setStatus(Constants.INTERNAL_SERVER_ERROR);
            errorResponse.setMessage("An error occurred while fetching categories");
            errorResponse.setData(null);

            return errorResponse;
        }
    }


    @Override
    public ApplicationResponse<Product> getProductsByCategory(String category) {
        try {
            List<Product> subCategoryData = (ArrayList<Product>) productRepo.findByCategoryIgnoreCase(category);
            ApplicationResponse<Product> applicationResponse = new ApplicationResponse<>();
            if (subCategoryData == null || subCategoryData.size() == 0) {
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

            ApplicationResponse<Product> errorResponse = new ApplicationResponse<>();
            errorResponse.setStatus(Constants.INTERNAL_SERVER_ERROR);
            errorResponse.setMessage("An error occurred while fetching products by category");
            errorResponse.setData(null);

            return errorResponse;
        }
    }

    @Override
    public List<Product> getProductsAll() {
        try {
            List<Product> products = (ArrayList<Product>) productRepo.findAll();
            return products;
        } catch (Exception e) {
            log.error("An error occurred while fetching all products", e);
            return Collections.emptyList();
        }
    }


    @Override
    public ApplicationResponse<Product> getProductById(String id) {
        try {
            List<Product> products = (ArrayList<Product>) productRepo.findAllById(Collections.singleton(id));
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
        public ApplicationResponse<Product> searchProducts(String keyword) {
            try {
                List<Product> searchResult = (ArrayList<Product>) productRepo.findByTitleContaining(keyword);
                ApplicationResponse<Product> applicationResponse = new ApplicationResponse();

                if (searchResult == null || searchResult.isEmpty())
                {
                    log.info("No products found for keyword: {}", keyword);

                }

                log.info("{} Search Result found for keyword -> {}", searchResult.size(), keyword);
                applicationResponse.setStatus(Constants.OK);
                applicationResponse.setMessage(Constants.OK_MESSAGE);
                applicationResponse.setData(searchResult);

                return applicationResponse;
            } catch (Exception e) {
                log.error("An error occurred while searching for products", e);

                ApplicationResponse<Product> errorResponse = new ApplicationResponse<>();
                errorResponse.setStatus(Constants.INTERNAL_SERVER_ERROR);
                errorResponse.setMessage("An error occurred while searching for products");
                errorResponse.setData(null);

                return errorResponse;
            }
        }


    }






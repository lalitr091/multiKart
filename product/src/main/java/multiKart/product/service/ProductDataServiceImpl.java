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
import java.util.List;

@Slf4j
@Service
public class ProductDataServiceImpl implements ProductDataService {
    @Autowired
    CategoryRepo categoryRepo;
    @Autowired
    ProductRepo productRepo;

    @Override
    public ApplicationResponse getCategories() {
        List<Category> categoryData = (ArrayList<Category>) categoryRepo.findAll();
        ApplicationResponse<Category> applicationResponse = new ApplicationResponse();
        applicationResponse.setStatus(Constants.OK);
        applicationResponse.setMessage(Constants.OK_MESSAGE);
        applicationResponse.setData(categoryData);
        return applicationResponse;
    }

    @Override
    public ApplicationResponse getProductsByCategory(String category) {
        //List<Product> subCategoryData = (ArrayList<Product>) productRepo.findByCategory(category);
        List<Product> subCategoryData = (ArrayList<Product>) productRepo.findByCategoryIgnoreCase(category);
        ApplicationResponse<Product> applicationResponse = new ApplicationResponse();
        // log.info("Products Data -> %s", subCategoryData);
        if (subCategoryData == null || subCategoryData.size() == 0) {
            applicationResponse.setStatus(Constants.NO_CONTENT);
            applicationResponse.setMessage(Constants.NO_CONTENT_MESSAGE);
        } else {
            applicationResponse.setStatus(Constants.OK);
            applicationResponse.setMessage(Constants.OK_MESSAGE);
        }
        applicationResponse.setData(subCategoryData);
        return applicationResponse;
    }





    @Override
    public ApplicationResponse getProductsAll() {
        List<Product> products = (ArrayList<Product>) productRepo.findAll();
        ApplicationResponse<Product> applicationResponse = new ApplicationResponse();
        applicationResponse.setStatus(Constants.OK);
        applicationResponse.setMessage(Constants.OK_MESSAGE);
        applicationResponse.setData(products);
        return applicationResponse;
    }





    @Override
    public ApplicationResponse<Product> searchProducts(String keyword) {

        List<Product> searchResult = (ArrayList<Product>) productRepo.findByTitleContaining(keyword); //    findByTitleContainingIgnoreCase(title);
        ApplicationResponse<Product> applicationResponse = new ApplicationResponse();
        //log.info("Search Result -> %s", searchResult);
        log.info(" {} Search Result found for keyword ->{} ", searchResult.size(), keyword);

        if (searchResult == null || searchResult.size() == 0) {
            applicationResponse.setStatus(Constants.NO_CONTENT);
            applicationResponse.setMessage(Constants.NO_CONTENT_MESSAGE);
        } else {
            applicationResponse.setStatus(Constants.OK);
            applicationResponse.setMessage(Constants.OK_MESSAGE);
        }

        applicationResponse.setData(searchResult);
        return applicationResponse;
    }




}

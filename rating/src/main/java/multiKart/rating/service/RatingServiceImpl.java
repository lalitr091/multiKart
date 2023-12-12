package multiKart.rating.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import multiKart.rating.Repository.RatingRepo;
import multiKart.rating.common.Constants;
import multiKart.rating.model.ApplicationResponse;
import multiKart.rating.model.Rating;

import java.util.List;

@Slf4j
@Service
public class RatingServiceImpl implements RatingDataService{
    @Autowired
    private RatingRepo ratingRepository;
    @Override
    public ApplicationResponse create(Rating rating) {
        try {
            List<Rating> ratings = ratingRepository.findByUserId(rating.getUserId());
            boolean ratingExists = false;
            for(Rating rating1 : ratings){
                if(rating1.getProductId().equals(rating.getProductId()) && rating1.getVariantId().equals(rating.getVariantId())){
                    ratingExists = true;
                    rating1.setComment(rating.getComment());
                    rating1.setRating(rating.getRating());
                    ratingRepository.save(rating1);
                    break;
                }
            }
            if(ratingExists==false)
                ratingRepository.save(rating);

            ApplicationResponse applicationResponse = new ApplicationResponse();
            applicationResponse.setStatus(Constants.OK);
            applicationResponse.setMessage("Rating of product is added successfully!");
            return applicationResponse;
        }catch (Exception e) {
            log.error("An error occurred while saving rating of product", e.getMessage());

            ApplicationResponse error = new ApplicationResponse();
            error.setStatus(Constants.INTERNAL_SERVER_ERROR);
            error.setMessage("An error occurred while adding saving rating");
            return error;
        }
    }

    @Override
    public ApplicationResponse getRating() {
        try {
            List<Rating> ratings = ratingRepository.findAll();
            ApplicationResponse response = new ApplicationResponse();
            response.setData(ratings);
            response.setMessage("List of all ratings");
            response.setStatus(Constants.OK);
            return response;
        }catch (Exception e) {
            log.error("An error occurred while getting all ratings", e.getMessage());

            ApplicationResponse error = new ApplicationResponse();
            error.setStatus(Constants.INTERNAL_SERVER_ERROR);
            error.setMessage("An error occurred while getting all ratings");
            return error;
        }
    }

    @Override
    public ApplicationResponse getRatingByUserId(String userId) {
        try {
            List<Rating> ratings  = ratingRepository.findByUserId(userId);
            ApplicationResponse response = new ApplicationResponse();
            response.setData(ratings);
            response.setMessage("List of all ratings by userid");
            response.setStatus(Constants.OK);
            return response;

        }catch (Exception e) {
            log.error("An error occurred while getting all ratings of products by userid", e.getMessage());

            ApplicationResponse error = new ApplicationResponse();
            error.setStatus(Constants.INTERNAL_SERVER_ERROR);
            error.setMessage("An error occurred while getting all ratings of products by userid");
            return error;
        }
    }

    @Override
    public ApplicationResponse getRatingByProductId(String productId, String variantId) {
        try {
            List<Rating> ratings = ratingRepository.findByProductIdAndVariantId(productId, variantId);
            ApplicationResponse response = new ApplicationResponse();
            response.setData(ratings);
            response.setMessage("List of all ratings by productid and varaintid");
            response.setStatus(Constants.OK);
            return response;

        }catch (Exception e) {
            log.error("An error occurred while getting all ratings of products by productid and variantid", e.getMessage());

            ApplicationResponse error = new ApplicationResponse();
            error.setStatus(Constants.INTERNAL_SERVER_ERROR);
            error.setMessage("An error occurred while getting all ratings of products by productid and variantid");
            return error;
        }

    }

    @Override
    public ApplicationResponse removeRating(String userId, String productId, String variantId) {
        try {
            List<Rating> ratings = ratingRepository.findByUserId(userId);
            ApplicationResponse applicationResponse = new ApplicationResponse();

            try {
                for(Rating rating : ratings) {
                    if(rating.getProductId().equals(productId) && rating.getVariantId().equals(variantId)) {
                        ratingRepository.delete(rating);
                    }
                }
            } catch (NumberFormatException e) {
                log.error("Error deleting rating of product variant: {}", variantId, e);
            }

            applicationResponse.setStatus(Constants.OK);
            applicationResponse.setMessage(Constants.OK_MESSAGE);
            return applicationResponse;


        } catch (Exception e) {
            log.error("An error occurred while removing rating of product", e);
            ApplicationResponse errorResponse = new ApplicationResponse();
            errorResponse.setStatus(Constants.INTERNAL_SERVER_ERROR);
            errorResponse.setMessage("An error occurred while removing rating of product: " + e.getMessage());
            return errorResponse;
        }
    }
}

package multiKart.product.Repository;
import multiKart.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends MongoRepository<Product, String>
{



    List<Product> findByCategoryIgnoreCase(String category);

    @Query("{$or:[{'title': { $regex: ?0, $options: 'i' }}," +
            " {'description': { $regex: ?0, $options: 'i' }}," +
            " {'brand': { $regex: ?0, $options: 'i' }}]}")
            List<Product> findByTitleContaining(String keyword);


    @Query("{'$or': [" +
            "{'category': { '$in': [ ?0 ] }}," +
            "{'brand': { '$in': [ ?1 ] }}," +
            "{'variants': { '$elemMatch': { 'color': { '$in': [ ?2 ] }, 'size': { '$in': [ ?3 ] } } }}," +
            "]}")
    List<Product> findByCategoryOrBrandOrVariantsColorOrVariantsSize(
            String category,
            List<String> brands,
            List<String> colors,
            List<String> sizes,
            Double minPrice,
            Double maxPrice
    );

}





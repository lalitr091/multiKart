package com.multikart.cart.Repository;

import com.multikart.cart.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepo extends MongoRepository<Cart,String>
{
   Cart findCartByUserid(String userid);

}

package com.multikart.cart.service;

import com.multikart.cart.Repository.CartRepo;
import com.multikart.cart.common.Constants;
import com.multikart.cart.model.ApplicationResponse;
import com.multikart.cart.model.Cart;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class CartDataServiceImpl implements CartDataService {

    @Autowired
    CartRepo cartRepo;

    @Override
    public ApplicationResponse removeFromCart(String variant_id, String user_id, String deleteType, String product_id) {
        try {
            Cart existingCart = cartRepo.findCartByUserid(user_id);
            List<Cart.CartItem> existUserItems = existingCart.getCartItems();

            if ("remove_all".equals(deleteType)) {
                removeItemFromCart(existUserItems, variant_id);
            } else if ("increase_1".equals(deleteType)) {
                increaseQuantity(existUserItems, variant_id);
            } else if ("decrease_1".equals(deleteType)) {
                decreaseQuantity(existUserItems, variant_id);
            } else {
                log.error("Invalid deleteType: {}", deleteType);
            }

            existingCart.setCartItems(existUserItems);
            cartRepo.save(existingCart);
            ApplicationResponse applicationResponse = new ApplicationResponse();
            applicationResponse.setStatus(Constants.OK);
            applicationResponse.setMessage(Constants.OK_MESSAGE);
            return applicationResponse;

        } catch (Exception e)
        {
            log.error("An error occurred while removing variant from cart", e);
            ApplicationResponse errorResponse = new ApplicationResponse();
            errorResponse.setStatus(Constants.INTERNAL_SERVER_ERROR);
            errorResponse.setMessage("An error occurred while removing variant from cart: " + e.getMessage());
            return errorResponse;
        }
    }

    private void removeItemFromCart(List<Cart.CartItem> existUserItems, String variant_id) {
        existUserItems.removeIf(cartItem -> {
            try {
                return cartItem.getVariantid() == Integer.parseInt(variant_id);
            } catch (Exception e) {
                log.error("Error while decreasing quantity with variant_id {}", variant_id, e);
                return false;
            }
        });
    }

    private void increaseQuantity(List<Cart.CartItem> existUserItems, String variant_id) {
        for (Cart.CartItem cartItem : existUserItems) {
            try {
                if (cartItem.getVariantid() == Integer.parseInt(variant_id)) {
                    cartItem.setVariantid_qty(cartItem.getVariantid_qty() + 1);
                    break;
                }
            } catch (Exception e) {
                log.error("Error while decreasing quantity with variant_id {}", variant_id, e);
            }
        }
    }

    private void decreaseQuantity(List<Cart.CartItem> existUserItems, String variant_id)
    {
        existUserItems.removeIf(cartItem -> {
            try {
                if (cartItem.getVariantid() == Integer.parseInt(variant_id)) {
                    if (cartItem.getVariantid_qty() > 1) {
                        cartItem.setVariantid_qty(cartItem.getVariantid_qty() - 1);
                        return false;
                    } else {
                        return true;
                    }
                }
            } catch (Exception e) {
                log.error("Error while decreasing quantity with variant_id {}", variant_id, e);
            }
            return false;
        });
    }


    @Override
    public ApplicationResponse addToCart(Cart cart) {
        try {
            Cart existingCart = cartRepo.findCartByUserid(cart.getUserid());

            if (existingCart == null) {
                existingCart = new Cart();
                existingCart.setUserid(cart.getUserid());
                existingCart.setCartItems(new ArrayList<>());
            }

         //   Cart.CartItem userItem = (Cart.CartItem) cart.getCartItems();
            List<Cart.CartItem> userItems = cart.getCartItems();
            boolean itemExists = false;

            //for (Cart.CartItem item : existingCart.getCartItems()) {

            for (Cart.CartItem userItem : userItems) {
                for (Cart.CartItem item : existingCart.getCartItems()) {
                    if (item.getVariantid() == userItem.getVariantid() && item.getProductid().equals(userItem.getProductid()))
                    {
                        item.setVariantid_qty(item.getVariantid_qty() + userItem.getVariantid_qty());
                        itemExists = true;
                        break;
                    }
                }

                if (!itemExists) {
                    Cart.CartItem newItem = new Cart.CartItem();
                    newItem.setVariantid_qty(1);
                    newItem.setVariantid(userItem.getVariantid());
                    newItem.setProductid(userItem.getProductid());
                    existingCart.getCartItems().add(newItem);
                }

            }
            cartRepo.save(existingCart);

            ApplicationResponse applicationResponse = new ApplicationResponse();
            applicationResponse.setStatus(Constants.OK);
            applicationResponse.setMessage(Constants.OK_MESSAGE);
            return applicationResponse;

        } catch (Exception e) {
            log.error("An error occurred while adding product to cart", e.getMessage());

            ApplicationResponse errorAddingProductToCart = new ApplicationResponse();
            errorAddingProductToCart.setStatus(Constants.INTERNAL_SERVER_ERROR);
            errorAddingProductToCart.setMessage("An error occurred while adding product to cart");
            return errorAddingProductToCart;
        }
    }}





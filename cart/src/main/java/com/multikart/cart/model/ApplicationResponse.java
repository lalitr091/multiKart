package com.multikart.cart.model;

import lombok.Data;


@Data
public class ApplicationResponse<T> {
   private Integer Status;
    private String Message;

}

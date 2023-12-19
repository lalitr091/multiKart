import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Observable, Subject } from 'rxjs';
import { ProductService } from './product.service';

const state = {
  checkoutItems: JSON.parse(localStorage['checkoutItems'] || '[]')
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private cartUpdateSubject = new Subject<void>();
  cartUpdate$ = this.cartUpdateSubject.asObservable();

  constructor(private router: Router,
    private http: HttpClient,
    private toastrService: ToastrService,
    private productService: ProductService) { }

  // Get Checkout Items
  public get checkoutItems(): Observable<any> {
    const itemsStream = new Observable(observer => {
      observer.next(state.checkoutItems);
      observer.complete();
    });
    return <Observable<any>>itemsStream;
  }

  // Create order
  public createOrder(product: any, details: any, orderId: any, amount: any) {
    var item = {
        shippingDetails: details,
        product: product,
        orderId: orderId,
        totalAmount: amount
    };
    state.checkoutItems = item;
    localStorage.setItem("checkoutItems", JSON.stringify(item));
    localStorage.removeItem("cartItems");
    this.router.navigate(['/shop/checkout/success', orderId]);
  }

  //placeOrder 08/12/2023
  public placeOrder(products, totalAmount, checkoutForm){    
    const productArray = [];
    for (let i = 0; i < products.length; i++) {
      const product = products[i];
      const variants = product.variants;
      for (let j = 0; j < variants.length; j++) {
        const variant = variants[j];
        productArray.push({
          product_id: product.product_id,
          variant_id: variant.variant_id,
          qty: variant.variantid_qty,
          price: product.price
        });
      }
    }

    const request = {
      modeOfPayment: "COD",
      userId: 1234,
      totalAmount: totalAmount,
      orderStatus: "Pending",
      paymentStatus: "pending",
      billingDetails: {
        firstName: checkoutForm.value.firstname,
        lastName: checkoutForm.value.lastname,
        phoneNumber: checkoutForm.value.phone,
        email: checkoutForm.value.email,
        country: checkoutForm.value.country,
        address: checkoutForm.value.address,
        city: checkoutForm.value.town,
        state: checkoutForm.value.state,
        postalCode: checkoutForm.value.postalcode
      },
      products: productArray
    }
  this.http.post('http://localhost:8086/multikart/v1/order/create', request)
  .subscribe(
    (response: any) => {
      if (response) {
        this.cartUpdateSubject.next();
        this.productService.getCartItems(1234);
        this.toastrService.success(response.message);
        this.router.navigate(['/shop/cart']);
        return response;
      }
    },
    (error) => {
    }
  );
  }
}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

const state = {
  checkoutItems: JSON.parse(localStorage['checkoutItems'] || '[]')
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private orderUrl = environment.orderUrl;

  constructor(private router: Router, private http: HttpClient) { }

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

    // // Update order status
    // updateOrderStatus(orderId: string, orderStatus: string): Observable<any> {
    //   const url = `${this.orderUrl}/order/updateOrderStatus`;
    //   const params = { orderId, orderStatus };
    //   return this.http.put(url, params);
    // }

    getAllOrders(userId: string): Observable<any[]> {
      const url = `${this.orderUrl}/order/order?userId=${userId}`;
      return this.http.get<any[]>(url);
    }
  
}

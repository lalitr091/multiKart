import { Component, OnInit, Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Observable, Subscription } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { ProductService } from "../../services/product.service";
import { Product } from "../../classes/product";

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {

  public products: Product[] = [];
  public search: boolean = false;
  public userId = 1234;

  public languages = [{
    name: 'English',
    code: 'en'
  }];

  public currencies = [{
    name: 'Euro',
    currency: 'EUR',
    price: 0.90 // price of euro
  }, {
    name: 'Rupees',
    currency: 'INR',
    price: 70.93 // price of inr
  }, {
    name: 'Pound',
    currency: 'GBP',
    price: 0.78 // price of euro
  }, {
    name: 'Dollar',
    currency: 'USD',
    price: 1 // price of usd
  }]
  totalAmountSubscription: Subscription;
  totalAmount: number;
  private cartUpdateSubscription: Subscription;

  constructor(@Inject(PLATFORM_ID) private platformId: Object,
    private translate: TranslateService,
    public productService: ProductService) {
  }

  ngOnInit(): void {
    this.getCartData();
    this.totalAmountSubscription = this.productService.cartTotalAmount().subscribe((total: number) => {
      this.totalAmount = total;
    });
    this.cartUpdateSubscription = this.productService.cartUpdate$.subscribe(() => {
      this.getCartData();
    });
  }

  ngOnDestroy() {
    this.cartUpdateSubscription.unsubscribe();
  }

  getCartData() {
    this.productService.getCartItems(this.userId).subscribe(response => {
      this.products = response;
      this.products.forEach(element => {
        element['selectedImages'] = [];
        element.images.forEach(ele => {
          if (ele.image_id === element.variants[0].image_id) {
            // this.images.push({src : ele.src, alt : ele.alt});
            element['selectedImages'].push({ src: ele.src, alt: ele.alt })
          }
        });
      });
    });
  }

  searchToggle() {
    this.search = !this.search;
  }

  changeLanguage(code) {
    if (isPlatformBrowser(this.platformId)) {
      this.translate.use(code)
    }
  }

  get getTotal(): Observable<number> {
    return this.productService.cartTotalAmount();
  }

  public removeItem(product: any, deleteType = 'remove_all') {
    this.productService.removeCartItem(product, deleteType, this.userId);
  }

  changeCurrency(currency: any) {
    this.productService.Currency = currency
  }

}

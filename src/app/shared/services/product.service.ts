import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { map, startWith, delay, catchError } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';
import { Product } from '../classes/product';
import { environment } from 'src/environments/environment';

const state = {
  products: JSON.parse(localStorage['products'] || '[]'),
  wishlist: JSON.parse(localStorage['wishlistItems'] || '[]'),
  compare: JSON.parse(localStorage['compareItems'] || '[]'),
  cart: JSON.parse(localStorage['cartItems'] || '[]')
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  public Currency = { name: 'Dollar', currency: 'USD', price: 1 } // Default Currency
  public OpenCart: boolean = false;
  public Products: any;
  public productRecords = new BehaviorSubject<any[]>([]);
  private apiUrl = environment.apiUrl;
  private cartUrl = environment.cartUrl;
  variantid: any;
  private cartUpdateSubject = new Subject<void>();
  cartUpdate$ = this.cartUpdateSubject.asObservable();

  constructor(private http: HttpClient,
    private toastrService: ToastrService) {
  }

  /*
    ---------------------------------------------
    ---------------  Product  -------------------
    ---------------------------------------------
  */

      /**
* @method searchProducts
* @description Search products by Brand, Title, Description
*/
    searchProducts(searchTerm: string): any {
      const url = `http://localhost:8085/multikart/v1/product/search?keyword=${searchTerm}`;
      return this.http.get(url);
    }

  /**
* @method getCategory
*/
  public getCategory() {
    const url = this.apiUrl + '/product/allcategories';
    return this.http.get(url).pipe(
      map((resp: any) => {
        return resp.data;
      }),
      catchError((err, caught) => {
        throw err;
      })
    );
  }

  // // Product
  // private get products(): Observable<Product[]> {
  //   this.Products = this.http.get<Product[]>('assets/data/products.json').pipe(map(data => data));
  //   this.Products.subscribe(next => { localStorage['products'] = JSON.stringify(next) });
  //   return this.Products = this.Products.pipe(startWith(JSON.parse(localStorage['products'] || '[]')));
  // }

  // Product
  private get products(): Observable<Product[]> {
    this.Products = this.http.get<Product[]>(this.apiUrl + '/product/all').pipe(map((response: any) => {
      this.productRecords.next(response?.data);
      return response;
    }));

    return this.Products;
  }

  // Get Products
  public get getProducts(): Observable<Product[]> {
    return this.products;
  }

  //  Get All Products
  public get getAllProducts(): Observable<any> {
    return this.http.get<any>(this.apiUrl + '/product/all');
  }

  // Get Products by category
  public getProductsByCategory(category: string): Observable<any> {
    return this.http.get<any>(this.apiUrl + '/product/bycategories?category=' + category);
    // return this.http.get<any>(this.apiUrl + '/product/all');
  }

  // Get Products By Slug
  public getProductBySlug(slug: string): Observable<Product> {
    return this.getAllProducts.pipe(map(items => {
      return items.find((item: any) => {
        return item.title.replace(' ', '-') === slug;
      });
    }));
  }


  /*
    ---------------------------------------------
    ---------------  Wish List  -----------------
    ---------------------------------------------
  */

  // Get Wishlist Items
  public get wishlistItems(): Observable<Product[]> {
    const itemsStream = new Observable(observer => {
      observer.next(state.wishlist);
      observer.complete();
    });
    return <Observable<Product[]>>itemsStream;
  }

  // Add to Wishlist
  public addToWishlist(product): any {
    const wishlistItem = state.wishlist.find((item: { id: any; }) => item.id === product.id)
    if (!wishlistItem) {
      state.wishlist.push({
        ...product
      })
    }
    this.toastrService.success('Product has been added in wishlist.');
    localStorage.setItem("wishlistItems", JSON.stringify(state.wishlist));
    return true
  }

  // Remove Wishlist items
  public removeWishlistItem(product: Product): any {
    const index = state.wishlist.indexOf(product);
    state.wishlist.splice(index, 1);
    localStorage.setItem("wishlistItems", JSON.stringify(state.wishlist));
    return true
  }

  /*
    ---------------------------------------------
    -------------  Compare Product  -------------
    ---------------------------------------------
  */

  // Get Compare Items
  public get compareItems(): Observable<Product[]> {
    const itemsStream = new Observable(observer => {
      observer.next(state.compare);
      observer.complete();
    });
    return <Observable<Product[]>>itemsStream;
  }

  // Add to Compare
  public addToCompare(product): any {
    const compareItem = state.compare.find(item => item.id === product.id)
    if (!compareItem) {
      state.compare.push({
        ...product
      })
    }
    this.toastrService.success('Product has been added in compare.');
    localStorage.setItem("compareItems", JSON.stringify(state.compare));
    return true
  }

  // Remove Compare items
  public removeCompareItem(product: Product): any {
    const index = state.compare.indexOf(product);
    state.compare.splice(index, 1);
    localStorage.setItem("compareItems", JSON.stringify(state.compare));
    return true
  }

  /*
    ---------------------------------------------
    -----------------  Cart  --------------------
    ---------------------------------------------
  */


  // Get Cart Items
  public getCartItems(userId): Observable<Product[]> {
    return this.http.get<Product[]>(this.cartUrl + '/cart/byuserid?userId=' + userId).pipe(
      map((response: any) => {
        return response?.data[0]
      })
    );
  }

  // Get Cart Items
  public get cartItems(): Observable<Product[]> {
    const itemsStream = new Observable(observer => {
      observer.next(state.cart);
      observer.complete();
    });
    return <Observable<Product[]>>itemsStream;
  }

  // Add to Cart
  public addToCart(product, selectedColor?, selectedSize?): any {
    const cartItem = state.cart.find(item => item.id === product.id);
    const qty = product.quantity ? product.quantity : 1;
    const items = cartItem ? cartItem : product;
    const stock = this.calculateStockCounts(items, qty);
    product?.variants.forEach(element => {
      if (selectedColor === element.color && selectedSize === element.size) {
        this.variantid = element.variant_id;
      }
    });
    const request =
    {
      "cartItems": [
        {
          "variantid_qty": product.quantity,
          "variantid": this.variantid,
          "productid": product.product_id
        }
      ],
      "userid": "1234"
    }
    if (!stock) return false

    if (cartItem == !undefined) {
      cartItem.quantity += qty
    } else {
      this.http.post(this.cartUrl + '/cart/add', request)
        .subscribe(
          (response) => {
            if (response) {
              this.cartUpdateSubject.next();
              return response;
            }
          },
          (error) => {
          }
        );
    }
    this.OpenCart = true; // If we use cart variation modal
    return true;
  }

  // Update Cart Quantity
  public updateCartQuantity(product: Product, quantity: number, deleteType, userId) {
    const variant_id = product.variants[0].variant_id;
    const params = new HttpParams()
      .set('variant_id', variant_id)
      .set('product_id', product.product_id)
      .set('user_id', '1234')
      .set('deleteType', deleteType);

    // Make the DELETE request with query parameters
    this.http.delete(this.cartUrl + '/cart/remove', { params })
      .subscribe(
        (response) => {
          if (response) {
            this.cartUpdateSubject.next();
          }
        },
        (error) => {
        },
        () => {
        }
      );
  }

  // Calculate Stock Counts
  public calculateStockCounts(product, quantity) {
    const qty = product.quantity + quantity
    const stock = product.stock
    if (stock < qty || stock == 0) {
      this.toastrService.error('You can not add more items than available. In stock ' + stock + ' items.');
      return false
    }
    return true
  }

  // Remove Cart items
  public removeCartItem(product: Product, deleteType?, userId?): any {
    // const index = state.cart.indexOf(product);
    // state.cart.splice(index, 1);
    const variant_id = product.variants[0].variant_id

    // Define the query parameters
    const params = new HttpParams()
      .set('variant_id', variant_id)
      .set('product_id', product.product_id)
      .set('user_id', '1234')
      .set('deleteType', deleteType);

    // Make the DELETE request with query parameters
    this.http.delete(this.cartUrl + '/cart/remove', { params })
      .subscribe(
        (response) => {
          if (response) {
            this.cartUpdateSubject.next();
          }
        },
        (error) => {
        }
      );
  }

  // Total amount 
  public cartTotalAmount(): Observable<number> {
    return this.getCartItems(1234).pipe(
      map((products) => {
        const totalAmount = products.reduce((total, product: Product) => {
          const price = product.discount ? product.price * (1 - product.discount / 100) : product.price;
          const subtotal = price * product.variants[0].variantid_qty;
          return total + subtotal * this.Currency.price;
        }, 0);
        return totalAmount;
      })
    );
  }  

  /*
    ---------------------------------------------
    ------------  Filter Product  ---------------
    ---------------------------------------------
  */

  // Get Product Filter
  public filterProducts(filter: any): Observable<Product[]> {
    this.products;
    return this.productRecords?.pipe(map(product =>
      product?.filter((item: Product) => {
        if (!filter.length) return true
        const Tags = filter.some((prev) => { // Match Tags
          if (item.tags) {
            if (item.tags.includes(prev)) {
              return prev
            }
          }
        })
        return Tags
      })
    ));
  }

/**
 * @method filterByColor
 */
public filterByColor(products: Product[], payload: string[]): any {
  if (!payload || payload.length === 0) {
    return products;
  } else {
    let uniqueProductIds = new Set<string>();
    let filterProducts = [];

    products.filter((data: any) => {
      if (data?.variants?.length > 0) {
        data.variants.filter((variant: any) => {
          if (payload.includes(variant.color) && !uniqueProductIds.has(data.product_id)) {
            uniqueProductIds.add(data.product_id);
            filterProducts.push(data);
          }
        });
      }
    });

    return filterProducts;
  }
}


/**
 * @method filterBySize
 */
public filterBySize(products: Product[], payload: string[]): any {
  if (!payload || payload.length === 0) {
    return products;
  } else {
    let uniqueProductIds = new Set<string>();
    return products.filter((data: any) => {
      if (data?.variants?.length > 0) {
        let filteredVariants = data.variants.filter((variant: any) => {
          // return payload.includes(variant.size);
          return payload == variant.size;
        });

        if (filteredVariants.length > 0 && !uniqueProductIds.has(data.product_id)) {
          uniqueProductIds.add(data.product_id);
          return true;
        }
      }
      return false;
    });
  }
}


  
/**
 * @method filterByBrand
 */
public filterByBrand(products: Product[], payload: string[]): any {
  if (!payload || payload.length === 0) {
    return products;
  } else {
    let uniqueProductIds = new Set<string>();
    let filterProducts = [];

    products.filter((data: any) => {
      if (payload.includes(data.brand) && !uniqueProductIds.has(data.product_id)) {
        uniqueProductIds.add(data.product_id);
        filterProducts.push(data);
      }
    });

    return filterProducts;
  }
}


/**
 * @method filterByPriceRange
 */
public filterByPriceRange(products: Product[], minPrice: number, maxPrice: number): any {
  if (minPrice === null && maxPrice === null) {
    return products;
  } else {
    return products.filter((product: Product) => {
      const productPrice = product.price || 0;
      return (minPrice === null || productPrice >= minPrice) && (maxPrice === null || productPrice <= maxPrice);
    });
  }
}


  // Sorting Filter
  public sortProducts(products: Product[], payload: string): any {
    if (!payload) { return products; }
    if (payload === 'ascending') {
      return products.sort((a, b) => {
        if (a.id < b.id) {
          return -1;
        } else if (a.id > b.id) {
          return 1;
        }
        return 0;
      })
    } else if (payload === 'a-z') {
      return products.sort((a, b) => {
        if (a.title < b.title) {
          return -1;
        } else if (a.title > b.title) {
          return 1;
        }
        return 0;
      })
    } else if (payload === 'z-a') {
      return products.sort((a, b) => {
        if (a.title > b.title) {
          return -1;
        } else if (a.title < b.title) {
          return 1;
        }
        return 0;
      })
    } else if (payload === 'low') {
      return products.sort((a, b) => {
        if (a.price < b.price) {
          return -1;
        } else if (a.price > b.price) {
          return 1;
        }
        return 0;
      })
    } else if (payload === 'high') {
      return products.sort((a, b) => {
        if (a.price > b.price) {
          return -1;
        } else if (a.price < b.price) {
          return 1;
        }
        return 0;
      })
    }
  }

  /*
    ---------------------------------------------
    ------------- Product Pagination  -----------
    ---------------------------------------------
  */
  public getPager(totalItems: number, currentPage: number = 1, pageSize: number = 16) {
    // calculate total pages
    let totalPages = Math.ceil(totalItems / pageSize);

    // Paginate Range
    let paginateRange = 3;

    // ensure current page isn't out of range
    if (currentPage < 1) {
      currentPage = 1;
    } else if (currentPage > totalPages) {
      currentPage = totalPages;
    }

    let startPage: number, endPage: number;
    if (totalPages <= 5) {
      startPage = 1;
      endPage = totalPages;
    } else if (currentPage < paginateRange - 1) {
      startPage = 1;
      endPage = startPage + paginateRange - 1;
    } else {
      startPage = currentPage - 1;
      endPage = currentPage + 1;
    }

    // calculate start and end item indexes
    let startIndex = (currentPage - 1) * pageSize;
    let endIndex = Math.min(startIndex + pageSize - 1, totalItems - 1);

    // create an array of pages to ng-repeat in the pager control
    let pages = Array.from(Array((endPage + 1) - startPage).keys()).map(i => startPage + i);

    // return object with all pager properties required by the view
    return {
      totalItems: totalItems,
      currentPage: currentPage,
      pageSize: pageSize,
      totalPages: totalPages,
      startPage: startPage,
      endPage: endPage,
      startIndex: startIndex,
      endIndex: endIndex,
      pages: pages
    };
  }

}

import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductDetailsMainSlider, ProductDetailsThumbSlider } from '../../../../shared/data/slider';
import { Product } from '../../../../shared/classes/product';
import { ProductService } from '../../../../shared/services/product.service';
import { SizeModalComponent } from "../../../../shared/components/modal/size-modal/size-modal.component";

@Component({
  selector: 'app-product-left-sidebar',
  templateUrl: './product-left-sidebar.component.html',
  styleUrls: ['./product-left-sidebar.component.scss']
})
export class ProductLeftSidebarComponent implements OnInit {

  public product: Product = {};
  public counter: number = 1;
  public activeSlide: any = 0;
  public selectedSize: any;
  public mobileSidebar: boolean = false;
  public active = 1;

  @ViewChild("sizeChart") SizeChart: SizeModalComponent;

  public ProductDetailsMainSliderConfig: any = ProductDetailsMainSlider;
  public ProductDetailsThumbConfig: any = ProductDetailsThumbSlider;
  productName: any;
  selectedColor: any;

  constructor(private route: ActivatedRoute, private router: Router,
    public productService: ProductService) {
    // this.route.data.subscribe(response => this.product = response.data);
  }

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      // Access the 'id' parameter from the route
      this.productName = params['slug'];
      this.productService.getProductBySlug(this.productName).subscribe(
       response => this.product = response
      )
    });
  }

  //On selecting different Colors 
  selectColor(index: number) {
    this.activeSlide = index.toString();
    this.selectedColor = this.Color(this.product?.variants)[index];
  }

  // Get Product Color
  Color(variants) {
    const uniqColor = []
    if (variants) {
      for (let i = 0; i < Object?.keys(variants).length; i++) {
        if (uniqColor.indexOf(variants[i].color) === -1 && variants[i].color) {
          uniqColor.push(variants[i].color)
        }
        if (i === this.activeSlide) {
          this.selectedColor = variants[i].color;
        }
      }
    }
    return uniqColor
  }

  // Get Product Size
  Size(variants) {
    const uniqSize = []
    if (variants) {
      for (let i = 0; i < Object.keys(variants).length; i++) {
        if (uniqSize.indexOf(variants[i].size) === -1 && variants[i].size) {
          uniqSize.push(variants[i].size)
        }
      } 
    }
    return uniqSize
  }

  selectSize(size) {
    this.selectedSize = size;
  }

  // Increament
  increment() {
    this.counter++;
  }

  // Decrement
  decrement() {
    if (this.counter > 1) this.counter--;
  }

  // Add to cart
  async addToCart(product: any, selectedColor, selectedSize) {
    product?.variants.forEach(element => {
      if ((element.color === selectedColor) && (element.size === selectedSize)) {
        const selectedVariant: string = 'selectedVariant'; // Replace 'someKey' with the actual key you want to use
        product[selectedVariant] = product[selectedVariant] || [];
        product[selectedVariant].push(element);
      }
    });
    product.quantity = this.counter || 1;
    const status = await this.productService.addToCart(product, selectedColor, selectedSize);
    if (status)
      this.router.navigate(['/shop/cart']);
  }

  // Buy Now
  async buyNow(product: any, selectedColor, selectedSize) {
    product?.variants.forEach(element => {
      if ((element.color === selectedColor) && (element.size === selectedSize)) {
        const selectedVariant: string = 'selectedVariant'; // Replace 'someKey' with the actual key you want to use
        product[selectedVariant] = product[selectedVariant] || [];
        product[selectedVariant].push(element);
      }
    });
    product.quantity = this.counter || 1;
    const status = await this.productService.addToCart(product, selectedColor, selectedSize);
    if (status)
      this.router.navigate(['/shop/checkout']);
  }

  // Add to Wishlist
  addToWishlist(product: any) {
    this.productService.addToWishlist(product);
  }

  // Toggle Mobile Sidebar
  toggleMobileSidebar() {
    this.mobileSidebar = !this.mobileSidebar;
  }

}

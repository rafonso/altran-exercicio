import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Cart} from '../cart';
import {ActivatedRoute, Router} from '@angular/router';
import {CartsService} from '../carts.service';
import {Utils} from '../utils';
import {CartItem} from '../cart-item';

@Component({
  selector: 'app-cart-item-edit',
  templateUrl: './cart-item-edit.component.html',
  styleUrls: ['./cart-item-edit.component.css']
})
export class CartItemEditComponent implements OnInit {

  error: string;
  angForm: FormGroup;
  cart: Cart = new Cart();
  cartItem: CartItem = new CartItem();

  constructor(private route: ActivatedRoute, private router: Router, private fb: FormBuilder, private cartService: CartsService) {
    this.createForm();
  }

  createForm() {
    this.error = null;
    this.angForm = this.fb.group({
      quantity: ['', Validators.compose([Validators.required, Validators.min(1)])]
    });
  }

  private loadCartItem(cartId, cartItemId) {
    this.cartService.editCart(cartId).subscribe(
      res => {
        this.cart = res;
        console.log(this.cart);
        // tslint:disable-next-line:triple-equals
        this.cartItem = this.cart.cartItems.filter(ci => ci.id == cartItemId)[0];
        console.log(this.cartItem);
      },
      err => {
        this.error = Utils.handleError(err);
      });
  }

  updateCartItem(quantity) {
    this.cartItem.quantity = quantity;
    this.cartService.updateCart(this.cart).subscribe(
      () => {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate([`cart/edit/${this.cart.id}`], {state: {data: 'Item Updated with success'}});
      },
      err => {
        this.error = Utils.handleError(err);
      }
    );
  }

  ngOnInit() {
    this.error = null;
    this.route.params.subscribe(params => {
      this.loadCartItem(params.cartId, params.itemCartId);
    });
  }

}

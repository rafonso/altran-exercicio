import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {CartsService} from '../carts.service';
import {ItemsService} from '../items.service';
import {Cart} from '../cart';
import {CartItem} from '../cart-item';

@Component({
  selector: 'app-cart-edit',
  templateUrl: './cart-edit.component.html',
  styleUrls: ['./cart-edit.component.css']
})
export class CartEditComponent implements OnInit {

  error: string;
  message: string;
  angForm: FormGroup;
  cart: Cart = new Cart();
  cartItems: CartItem[] = [];

  constructor(private route: ActivatedRoute, private router: Router, private cartService: CartsService, private fb: FormBuilder,
              private itemsService: ItemsService) {
    const navigation = this.router.getCurrentNavigation();
    if (!navigation.extras.state) {
      return;
    }
    const state = navigation.extras.state as { data: string };
    this.message = state.data;
  }

  private loadCart(id) {
    this.cartService.editCart(id).subscribe(
      res => {
        this.cart = res;
        // this.cartItems = this.cart.cartItems;
        // console.log(this.cart);
      },
      err => {
        console.error(err);
        this.error = err.message;
      }
    );
  }

  closeCart(id) {
    this.cartService.closeCart(id).subscribe(res => {
        this.message = 'Cart Closed with success';
        this.loadCart(id);
      },
      err => {
        console.error(err);
        this.error = err.message;
      }
    );
  }

  ngOnInit() {
    this.error = null;
    this.route.params.subscribe(params => {
      this.loadCart(params.id);
    });
  }

}

import {Component, OnInit} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {CartsService} from '../carts.service';
import {Cart} from '../cart';
import {Utils} from '../utils';

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

  constructor(private route: ActivatedRoute, private router: Router, private cartService: CartsService) {
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
      },
      err => {
        this.error = Utils.handleError(err);
      }
    );
  }

  deleteItemCart(itemCartId) {
    // tslint:disable-next-line:triple-equals
    this.cart.cartItems = this.cart.cartItems.filter(ci => ci.id != itemCartId);
    this.cartService.updateCart(this.cart).subscribe(
      res => {
        this.loadCart(this.cart.id);
        this.message = 'Item removed with Success';
      },
      err => {
        this.error = Utils.handleError(err);
      }
    );
  }

  closeCart(id) {
    this.cartService.closeCart(id).subscribe(() => {
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

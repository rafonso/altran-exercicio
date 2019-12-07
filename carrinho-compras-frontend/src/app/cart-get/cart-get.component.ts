import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Cart} from '../cart';
import {CartsService} from '../carts.service';

@Component({
  selector: 'app-cart-get',
  templateUrl: './cart-get.component.html',
  styleUrls: ['./cart-get.component.css']
})
export class CartGetComponent implements OnInit {

  message: string;
  error: string;
  carts: Cart[];

  constructor(private router: Router, private cartsService: CartsService) {
    const navigation = this.router.getCurrentNavigation();
    if (!navigation.extras.state) {
      return;
    }
    const state = navigation.extras.state as { data: string };
    this.message = state.data;
  }

  private fillCarts() {
    this.cartsService.getCarts().subscribe(
      (data: Cart[]) => {
        this.carts = data;
      },
      err => {
        console.error(err);
        this.error = err.message;
      }
    );
  }

  deleteCart(id) {
    this.cartsService.deleteCart(id)
      .subscribe(
        () => {
          this.fillCarts();
          this.message = 'Cart Removed with success';
        },
        err => {
          console.error(err);
          this.error = err.error.message;
        }
      );
  }


  ngOnInit() {
    this.fillCarts();
  }

}

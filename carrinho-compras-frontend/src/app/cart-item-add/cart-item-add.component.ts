import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {CartsService} from '../carts.service';
import {ItemsService} from '../items.service';
import {Item} from '../item';
import {Utils} from '../utils';
import {Cart} from '../cart';
import {CartItem} from '../cart-item';

@Component({
  selector: 'app-cart-item-add',
  templateUrl: './cart-item-add.component.html',
  styleUrls: ['./cart-item-add.component.css']
})
export class CartItemAddComponent implements OnInit {

  error: string;
  angForm: FormGroup;
  cart: Cart = new Cart();
  items: Item[] = [];
  cartId = 0;

  constructor(private route: ActivatedRoute, private router: Router, private fb: FormBuilder, private cartService: CartsService,
              private itemsService: ItemsService) {
    this.createForm();
  }

  createForm() {
    this.error = null;
    this.angForm = this.fb.group({
      itemId: ['', Validators.compose([Validators.required])],
      quantity: ['', Validators.compose([Validators.required, Validators.min(1)])]
    });
  }


  private loadCartItem(cartId) {
    this.cartId = cartId;
    this.cartService.editCart(cartId).subscribe(
      res => {
        this.cart = res;
        console.log(this.cart);
        this.itemsService.getItems().subscribe(
          resItems => {
            console.log('resItems', resItems);
            const idsCartItems = this.cart.cartItems.map(ci => ci.item.id);
            console.log('cartItems', idsCartItems);
            if (idsCartItems.length > 0) {
              this.items = (resItems as Array<Item>).filter(ri => !idsCartItems.includes(ri.id));
            } else {
              this.items = (resItems as Array<Item>);
            }
            console.log('items', this.items);
          },
          err => {
            this.error = Utils.handleError(err);
          }
        );
      },
      err => {
        this.error = Utils.handleError(err);
      });
  }

  addCartItem(itemId, quantity) {
    // tslint:disable-next-line:triple-equals
    const selectedItem = this.items.filter(it => it.id == itemId);
    console.log('selectedItem', selectedItem, 'quantity', quantity);
    if (selectedItem.length === 1) {
      const cartItem = new CartItem();
      cartItem.item = selectedItem[0];
      cartItem.quantity = quantity;
      this.cart.cartItems.push(cartItem);
      console.log(this.cartService);
      this.cartService.updateCart(this.cart).subscribe(
        () => {
          this.router.navigate([`cart/edit/${this.cart.id}`], {state: {data: 'Item Added with success'}});
        },
        err => {
          this.error = Utils.handleError(err);
        }
      );
    } else {
      console.error(selectedItem);
    }
  }

  ngOnInit() {
    this.error = null;
    this.route.params.subscribe(params => {
      this.loadCartItem(params.cartId);
    });
  }

}

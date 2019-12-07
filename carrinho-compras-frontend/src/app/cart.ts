import {User} from './user';
import {CartItem} from './cart-item';
import {CartStatus} from './cart-status.enum';

export class Cart {
  id: number;
  user: User;
  cartItems: CartItem[];
  status: CartStatus;
  cartValue: number;
}

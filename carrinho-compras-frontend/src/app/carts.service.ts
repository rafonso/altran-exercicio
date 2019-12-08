import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from './user';
import {Cart} from './cart';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CartsService {

  uri = 'http://localhost:8080/shopping/cart/';

  constructor(private http: HttpClient) {
  }

  addCart(user: User) {
    const obj = {
      user
    };
    return this.http.post<Cart>(`${this.uri}`, obj);
  }

  getCarts() {
    return this.http.get<Cart[]>(this.uri);
  }

  editCart(id): Observable<Cart> {
    return this.http.get<Cart>(`${this.uri}${id}`);
  }

  closeCart(id) {
    return this.http.put<Cart>(`${this.uri}close/${id}`, {});
  }

  updateCart(cart) {
    return this.http.put<Cart>(`${this.uri}${cart.id}`, cart);
  }

  deleteCart(id) {
    return this.http.delete(`${this.uri}${id}`);
  }

}

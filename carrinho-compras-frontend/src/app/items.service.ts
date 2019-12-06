import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ItemsService {

  uri = 'http://localhost:8080/shopping/item/';

  constructor(private http: HttpClient) {
  }

  addItem(itemName, itemValue) {
    const obj = {
      name: itemName, value: itemValue
    };
    return this.http.post(`${this.uri}`, obj);
  }

  getItems() {
    return this.http.get(this.uri);
  }

  editItem(id) {
    return this.http.get(`${this.uri}${id}`);
  }

  updateItem(itemName, itemValue, id) {
    const obj = {
      name: itemName, value: itemValue
    };
    return this.http.put(`${this.uri}${id}`, obj);
  }

  deleteItem(id) {
    return this.http.delete(`${this.uri}${id}`);
  }

}

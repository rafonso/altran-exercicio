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
    console.log(obj);
    this.http.post(`${this.uri}`, obj)
      .subscribe(res => console.log(`Added Item: ${res.toString()}`));
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
    this.http.put(`${this.uri}${id}`, obj)
      .subscribe(res => console.log(`Updated Item: ${res.toString()}`));
  }

  deleteItem(id) {
    return this.http.delete(`${this.uri}${id}`);
  }

}

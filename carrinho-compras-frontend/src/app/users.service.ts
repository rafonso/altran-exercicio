import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  uri = 'http://localhost:8080/shopping/user/';

  constructor(private http: HttpClient) {
  }

  addUser(userEmail, userName) {
    const obj = {
      email: userEmail, name: userName
    };
    console.log(obj);
    this.http.post(`${this.uri}`, obj)
      .subscribe(res => console.log(`Added User: ${res.toString()}`));
  }

  getUsers() {
    return this.http.get(this.uri);
  }

  editUser(id) {
    return this.http.get(`${this.uri}${id}`);
  }

  updateUser(userEmail, userName, id) {
    const obj = {
      email: userEmail, name: userName
    };
    this.http.put(`${this.uri}${id}`, obj)
      .subscribe(res => console.log(`Updated User: ${res.toString()}`));
  }

  deleteUser(id) {
    return this.http.delete(`${this.uri}${id}`);
  }

}

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from './user';

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
    return this.http.post(`${this.uri}`, obj)
      // .subscribe(res => console.log('User Added'))
    ;
  }

  getUsers() {
    return this.http.get<User[]>(this.uri);
  }

  editUser(id) {
    return this.http.get(`${this.uri}${id}`);
  }

  updateUser(userEmail, userName, id) {
    const obj = {
      email: userEmail, name: userName
    };
    return this.http.put(`${this.uri}${id}`, obj);
  }

  deleteUser(id) {
    return this.http.delete(`${this.uri}${id}`);
  }

}

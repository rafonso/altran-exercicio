import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {UsersService} from '../users.service';
import {CartsService} from '../carts.service';
import {User} from '../user';

@Component({
  selector: 'app-cart-add',
  templateUrl: './cart-add.component.html',
  styleUrls: ['./cart-add.component.css']
})
export class CartAddComponent implements OnInit {

  error: string;
  angForm: FormGroup;
  users: User[];

  constructor(private router: Router, private fb: FormBuilder, private cartService: CartsService, private usersService: UsersService) {
    this.createForm();
  }

  createForm() {
    this.usersService.getUsers().subscribe(
      res => {
        console.log(res);
        this.users = res;
      },
      err => {
        console.error(err);
        this.error = err.toString();
      }
    );
    this.error = null;
    this.angForm = this.fb.group({
      userId: ['', Validators.compose([Validators.required])]
    });
  }

  addCart(userId) {
    const selectedUsers = this.users.filter(u => u.id === parseInt(userId, 10));
    if (selectedUsers.length !== 1) {
      console.error(selectedUsers);
    } else {
      const selectedUser = selectedUsers[0];
      console.log(selectedUser);
      this.cartService.addCart(selectedUser).subscribe(
        res => {
          console.log(res);
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate([`/cart/edit/${res.id}`], {state: {data: 'Cart Added with success. Now add the Items'}});
        },
        err => {
          console.error(err);
          this.error = err.message;
        }
      );
    }
  }

  ngOnInit() {
  }

}

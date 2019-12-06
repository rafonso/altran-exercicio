import {Component, OnInit} from '@angular/core';
import {User} from '../user';
import {UsersService} from '../users.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-user-get',
  templateUrl: './user-get.component.html',
  styleUrls: ['./user-get.component.css']
})
export class UserGetComponent implements OnInit {

  message: string;
  error: string;
  users: User[];

  constructor(private router: Router, private usersService: UsersService) {
    const navigation = this.router.getCurrentNavigation();
    if (!navigation.extras.state) {
      return;
    }
    const state = navigation.extras.state as { data: string };
    this.message = state.data;
  }

  private fillUsers() {
    this.usersService.getUsers().subscribe((data: User[]) => {
      this.users = data;
    });
  }

  deleteUser(id) {
    this.usersService.deleteUser(id)
      .subscribe(
        data => {
          this.fillUsers();
          this.message = 'User Removed with success';
        },
        err => {
          this.error = err.error.message;
        }
      );
  }

  ngOnInit() {
    this.fillUsers();
  }

}

import {Component, OnInit} from '@angular/core';
import {User} from '../user';
import {UsersService} from '../users.service';

@Component({
  selector: 'app-user-get',
  templateUrl: './user-get.component.html',
  styleUrls: ['./user-get.component.css']
})
export class UserGetComponent implements OnInit {

  users: User[];

  constructor(private usersService: UsersService) {
  }

  deleteUser(id) {
    this.usersService.deleteUser(id).subscribe(res => {
      this.users.splice(id, 1);
    });
  }

  ngOnInit() {
    this.usersService.getUsers().subscribe((data: User[]) => {
      this.users = data;
    });
  }

}

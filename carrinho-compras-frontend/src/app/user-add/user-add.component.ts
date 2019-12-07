import {Component, OnInit} from '@angular/core';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import {UsersService} from '../users.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-user-add',
  templateUrl: './user-add.component.html',
  styleUrls: ['./user-add.component.css']
})
export class UserAddComponent implements OnInit {

  error: string;
  angForm: FormGroup;

  constructor(private router: Router, private fb: FormBuilder, private usersService: UsersService) {
    this.createForm();
  }

  createForm() {
    this.error = null;
    this.angForm = this.fb.group({
      userEmail: ['', Validators.compose([Validators.required, Validators.email])],
      userName: ['', Validators.required]
    });
  }

  addUser(userEmail, userName) {
    this.error = null;
    this.usersService.addUser(userEmail, userName)
      .subscribe(
        () => {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['users'], {state: {data: 'User Added with success'}} );
        },
        err => {
          this.error = err.error.message;
        }
      );
  }

  ngOnInit() {
  }

}

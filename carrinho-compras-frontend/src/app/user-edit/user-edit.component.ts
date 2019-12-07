import {Component, OnInit} from '@angular/core';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {UsersService} from '../users.service';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css']
})
export class UserEditComponent implements OnInit {

  error: string;
  angForm: FormGroup;
  user: any = {};

  constructor(private route: ActivatedRoute, private router: Router, private usersService: UsersService, private fb: FormBuilder) {
    this.createForm();
  }

  createForm() {
    this.angForm = this.fb.group({
      userEmail: ['', Validators.compose([Validators.required, Validators.email])],
      userName: ['', Validators.required]
    });
  }

  updateUser(userEmail, userName) {
    this.error = null;
    this.route.params.subscribe(params => {
      this.usersService.updateUser(userEmail, userName, params.id).subscribe(
        () => {
          // noinspection JSIgnoredPromiseFromCall
          this.router.navigate(['users'], {state: {data: 'User Updated with success'}});
        },
        err => {
          this.error = err.error.message;
        }
      );
    });
  }

  ngOnInit() {
    this.error = null;
    this.route.params.subscribe(params => {
      this.usersService.editUser(params.id).subscribe(
        res => {
          this.user = res;
        });
    });
  }

}

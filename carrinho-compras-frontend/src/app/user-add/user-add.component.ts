import { Component, OnInit } from '@angular/core';
import { FormGroup,  FormBuilder,  Validators } from '@angular/forms';
import {UsersService} from '../users.service';

@Component({
  selector: 'app-user-add',
  templateUrl: './user-add.component.html',
  styleUrls: ['./user-add.component.css']
})
export class UserAddComponent implements OnInit {

  angForm: FormGroup;
  constructor(private fb: FormBuilder, private usersService: UsersService) {
    this.createForm();
  }

  createForm() {
    this.angForm = this.fb.group({
      userEmail: ['', Validators.compose([Validators.required, Validators.email])],
      userName: ['', Validators.required ]
    });
  }

  addUser(userEmail, userName) {
    this.usersService.addUser(userEmail, userName);
  }

  ngOnInit() {
  }

}

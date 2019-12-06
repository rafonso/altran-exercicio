import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ItemsService} from '../items.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-item-add',
  templateUrl: './item-add.component.html',
  styleUrls: ['./item-add.component.css']
})
export class ItemAddComponent implements OnInit {

  error: string;
  angForm: FormGroup;

  constructor(private router: Router, private fb: FormBuilder, private itemsService: ItemsService) {
    this.createForm();
  }

  createForm() {
    this.error = null;
    this.angForm = this.fb.group({
      itemName: ['', Validators.compose([Validators.required])],
      itemValue: ['', Validators.compose([Validators.required])]
    });
  }

  addItem(itemName, itemValue) {
    this.error = null;
    this.itemsService.addItem(itemName, itemValue)
      .subscribe(
        data => {
          this.router.navigate(['items'], {state: {data: 'Item Added with success'}});
        },
        err => {
          this.error = err.error.message;
        }
      );
  }

  ngOnInit() {
  }

}

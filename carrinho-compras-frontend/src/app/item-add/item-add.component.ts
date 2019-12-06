import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ItemsService} from '../items.service';

@Component({
  selector: 'app-item-add',
  templateUrl: './item-add.component.html',
  styleUrls: ['./item-add.component.css']
})
export class ItemAddComponent implements OnInit {

  angForm: FormGroup;

  constructor(private fb: FormBuilder, private itemsService: ItemsService) {
    this.createForm();
  }

  createForm() {
    this.angForm = this.fb.group({
      itemName: ['', Validators.compose([Validators.required])],
      itemValue: ['', Validators.compose([Validators.required])]
    });
  }

  addItem(itemName, itemValue) {
    this.itemsService.addItem(itemName, itemValue);
  }

  ngOnInit() {
  }

}

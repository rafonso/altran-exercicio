import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {ItemsService} from '../items.service';

@Component({
  selector: 'app-item-edit',
  templateUrl: './item-edit.component.html',
  styleUrls: ['./item-edit.component.css']
})
export class ItemEditComponent implements OnInit {

  angForm: FormGroup;
  item: any = {};

  constructor(private route: ActivatedRoute, private router: Router, private itemsService: ItemsService, private fb: FormBuilder) {
    this.createForm();
  }

  createForm() {
    this.angForm = this.fb.group({
      itemName: ['', Validators.compose([Validators.required])],
      itemValue: ['', Validators.required ]
    });
  }

  updateItem(itemName, itemValue) {
    this.route.params.subscribe(params => {
      this.itemsService.updateItem(itemName, itemValue, params.id);
      this.router.navigate(['items']);
    });
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.itemsService.editItem(params.id).subscribe(res => {
        this.item = res;
      });
    });
  }


}

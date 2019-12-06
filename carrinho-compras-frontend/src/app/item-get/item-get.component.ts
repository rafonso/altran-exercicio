import { Component, OnInit } from '@angular/core';
import {Item} from '../item';
import {ItemsService} from '../items.service';

@Component({
  selector: 'app-item-get',
  templateUrl: './item-get.component.html',
  styleUrls: ['./item-get.component.css']
})
export class ItemGetComponent implements OnInit {

  items: Item[];

  constructor(private itemsService: ItemsService) {
  }

  deleteItem(id) {
    this.itemsService.deleteItem(id).subscribe(res => {
      this.items.splice(id, 1);
    });
  }

  ngOnInit() {
    this.itemsService.getItems().subscribe((data: Item[]) => {
      this.items = data;
    });
  }

}

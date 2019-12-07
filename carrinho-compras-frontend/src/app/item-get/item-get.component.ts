import {Component, OnInit} from '@angular/core';
import {Item} from '../item';
import {ItemsService} from '../items.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-item-get',
  templateUrl: './item-get.component.html',
  styleUrls: ['./item-get.component.css']
})
export class ItemGetComponent implements OnInit {

  message: string;
  error: string;
  items: Item[];

  constructor(private router: Router, private itemsService: ItemsService) {
    const navigation = this.router.getCurrentNavigation();
    if (!navigation.extras.state) {
      return;
    }
    const state = navigation.extras.state as { data: string };
    this.message = state.data;
  }

  private fillItems() {
    this.itemsService.getItems().subscribe((data: Item[]) => {
      this.items = data;
    });
  }

  deleteItem(id) {
    this.itemsService.deleteItem(id)
      .subscribe(
        () => {
          this.fillItems();
          this.message = 'Item Removed with success';
        },
        err => {
          this.error = err.error.message;
        }
      );
  }

  ngOnInit() {
    this.fillItems();
  }

}

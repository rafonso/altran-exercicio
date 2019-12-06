import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UserAddComponent } from './user-add/user-add.component';
import { UserEditComponent } from './user-edit/user-edit.component';
import { UserGetComponent } from './user-get/user-get.component';
import {ItemAddComponent} from './item-add/item-add.component';
import {ItemEditComponent} from './item-edit/item-edit.component';
import {ItemGetComponent} from './item-get/item-get.component';



const routes: Routes = [
  {
    path: 'user/create',
    component: UserAddComponent
  },
  {
    path: 'user/edit/:id',
    component: UserEditComponent
  },
  {
    path: 'users',
    component: UserGetComponent
  },
  {
    path: 'item/create',
    component: ItemAddComponent
  },
  {
    path: 'item/edit/:id',
    component: ItemEditComponent
  },
  {
    path: 'items',
    component: ItemGetComponent
  }
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

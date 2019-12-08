import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {UserAddComponent} from './user-add/user-add.component';
import {UserEditComponent} from './user-edit/user-edit.component';
import {UserGetComponent} from './user-get/user-get.component';
import {ItemAddComponent} from './item-add/item-add.component';
import {ItemEditComponent} from './item-edit/item-edit.component';
import {ItemGetComponent} from './item-get/item-get.component';
import {CartAddComponent} from './cart-add/cart-add.component';
import {CartEditComponent} from './cart-edit/cart-edit.component';
import {CartGetComponent} from './cart-get/cart-get.component';
import {CartItemAddComponent} from './cart-item-add/cart-item-add.component';
import {CartItemEditComponent} from './cart-item-edit/cart-item-edit.component';


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
  },
  {
    path: 'cart/create',
    component: CartAddComponent
  },
  {
    path: 'cart/edit/:id',
    component: CartEditComponent
  },
  {
    path: 'carts',
    component: CartGetComponent
  },
  {
    path: 'cart-item/create/:cartId',
    component: CartItemAddComponent
  },
  {
    path: 'cart-item/edit/:cartId/:itemCartId',
    component: CartItemEditComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

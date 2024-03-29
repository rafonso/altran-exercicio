import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {SlimLoadingBarModule} from 'ng2-slim-loading-bar';
import {ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {UsersService} from './users.service';
import {UserAddComponent} from './user-add/user-add.component';
import {UserGetComponent} from './user-get/user-get.component';
import {UserEditComponent} from './user-edit/user-edit.component';
import { ItemAddComponent } from './item-add/item-add.component';
import { ItemGetComponent } from './item-get/item-get.component';
import { ItemEditComponent } from './item-edit/item-edit.component';
import { CartAddComponent } from './cart-add/cart-add.component';
import { CartGetComponent } from './cart-get/cart-get.component';
import { CartEditComponent } from './cart-edit/cart-edit.component';
import { CartItemAddComponent } from './cart-item-add/cart-item-add.component';
import { CartItemEditComponent } from './cart-item-edit/cart-item-edit.component';

@NgModule({
  declarations: [
    AppComponent,
    UserAddComponent,
    UserGetComponent,
    UserEditComponent,
    ItemAddComponent,
    ItemGetComponent,
    ItemEditComponent,
    CartAddComponent,
    CartGetComponent,
    CartEditComponent,
    CartItemAddComponent,
    CartItemEditComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    SlimLoadingBarModule,
    HttpClientModule
  ],
  providers: [UsersService],
  bootstrap: [AppComponent]
})
export class AppModule {
}

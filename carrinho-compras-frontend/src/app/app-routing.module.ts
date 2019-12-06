import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UserAddComponent } from './user-add/user-add.component';
import { UserEditComponent } from './user-edit/user-edit.component';
import { UserGetComponent } from './user-get/user-get.component';



const routes: Routes = [
  {
    path: 'user/create',
    component: UserAddComponent
  },
  {
    path: 'edit/:id',
    component: UserEditComponent
  },
  {
    path: 'users',
    component: UserGetComponent
  }
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

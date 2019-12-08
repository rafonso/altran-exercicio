import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CartItemEditComponent } from './cart-item-edit.component';

describe('CartItemEditComponent', () => {
  let component: CartItemEditComponent;
  let fixture: ComponentFixture<CartItemEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CartItemEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CartItemEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CartGetComponent } from './cart-get.component';

describe('CartGetComponent', () => {
  let component: CartGetComponent;
  let fixture: ComponentFixture<CartGetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CartGetComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CartGetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

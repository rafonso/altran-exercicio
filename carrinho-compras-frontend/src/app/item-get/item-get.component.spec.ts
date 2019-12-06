import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ItemGetComponent } from './item-get.component';

describe('ItemGetComponent', () => {
  let component: ItemGetComponent;
  let fixture: ComponentFixture<ItemGetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ItemGetComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ItemGetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

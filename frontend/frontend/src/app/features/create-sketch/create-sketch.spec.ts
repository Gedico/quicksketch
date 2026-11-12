import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateSketch } from './create-sketch';

describe('CreateSketch', () => {
  let component: CreateSketch;
  let fixture: ComponentFixture<CreateSketch>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateSketch],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateSketch);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

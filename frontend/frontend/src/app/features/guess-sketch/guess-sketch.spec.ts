import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GuessSketch } from './guess-sketch';

describe('GuessSketch', () => {
  let component: GuessSketch;
  let fixture: ComponentFixture<GuessSketch>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GuessSketch],
    }).compileComponents();

    fixture = TestBed.createComponent(GuessSketch);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

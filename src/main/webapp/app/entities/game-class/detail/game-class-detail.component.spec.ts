import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GameClassDetailComponent } from './game-class-detail.component';

describe('GameClass Management Detail Component', () => {
  let comp: GameClassDetailComponent;
  let fixture: ComponentFixture<GameClassDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GameClassDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ gameClass: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(GameClassDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(GameClassDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load gameClass on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.gameClass).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});

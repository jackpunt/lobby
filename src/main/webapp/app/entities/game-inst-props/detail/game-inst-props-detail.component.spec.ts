import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GameInstPropsDetailComponent } from './game-inst-props-detail.component';

describe('GameInstProps Management Detail Component', () => {
  let comp: GameInstPropsDetailComponent;
  let fixture: ComponentFixture<GameInstPropsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GameInstPropsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ gameInstProps: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(GameInstPropsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(GameInstPropsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load gameInstProps on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.gameInstProps).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});

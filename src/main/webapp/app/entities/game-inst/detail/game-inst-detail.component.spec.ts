import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GameInstDetailComponent } from './game-inst-detail.component';

describe('GameInst Management Detail Component', () => {
  let comp: GameInstDetailComponent;
  let fixture: ComponentFixture<GameInstDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GameInstDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ gameInst: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(GameInstDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(GameInstDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load gameInst on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.gameInst).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});

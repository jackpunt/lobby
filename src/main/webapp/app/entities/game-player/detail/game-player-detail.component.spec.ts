import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GamePlayerDetailComponent } from './game-player-detail.component';

describe('GamePlayer Management Detail Component', () => {
  let comp: GamePlayerDetailComponent;
  let fixture: ComponentFixture<GamePlayerDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GamePlayerDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ gamePlayer: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(GamePlayerDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(GamePlayerDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load gamePlayer on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.gamePlayer).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});

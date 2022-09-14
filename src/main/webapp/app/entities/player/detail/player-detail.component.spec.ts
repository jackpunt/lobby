import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PlayerDetailComponent } from './player-detail.component';

describe('Player Management Detail Component', () => {
  let comp: PlayerDetailComponent;
  let fixture: ComponentFixture<PlayerDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PlayerDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ player: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PlayerDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PlayerDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load player on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.player).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});

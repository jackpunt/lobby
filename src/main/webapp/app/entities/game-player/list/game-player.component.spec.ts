import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { GamePlayerService } from '../service/game-player.service';

import { GamePlayerComponent } from './game-player.component';

describe('GamePlayer Management Component', () => {
  let comp: GamePlayerComponent;
  let fixture: ComponentFixture<GamePlayerComponent>;
  let service: GamePlayerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'game-player', component: GamePlayerComponent }]), HttpClientTestingModule],
      declarations: [GamePlayerComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(GamePlayerComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GamePlayerComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(GamePlayerService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.gamePlayers?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to gamePlayerService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getGamePlayerIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getGamePlayerIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { GameInstService } from '../service/game-inst.service';

import { GameInstComponent } from './game-inst.component';

describe('GameInst Management Component', () => {
  let comp: GameInstComponent;
  let fixture: ComponentFixture<GameInstComponent>;
  let service: GameInstService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'game-inst', component: GameInstComponent }]), HttpClientTestingModule],
      declarations: [GameInstComponent],
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
      .overrideTemplate(GameInstComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GameInstComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(GameInstService);

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
    expect(comp.gameInsts?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to gameInstService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getGameInstIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getGameInstIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});

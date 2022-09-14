import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { GameClassService } from '../service/game-class.service';

import { GameClassComponent } from './game-class.component';

describe('GameClass Management Component', () => {
  let comp: GameClassComponent;
  let fixture: ComponentFixture<GameClassComponent>;
  let service: GameClassService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'game-class', component: GameClassComponent }]), HttpClientTestingModule],
      declarations: [GameClassComponent],
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
      .overrideTemplate(GameClassComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GameClassComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(GameClassService);

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
    expect(comp.gameClasses?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to gameClassService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getGameClassIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getGameClassIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});

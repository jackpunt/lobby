import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { GameInstPropsService } from '../service/game-inst-props.service';

import { GameInstPropsComponent } from './game-inst-props.component';

describe('GameInstProps Management Component', () => {
  let comp: GameInstPropsComponent;
  let fixture: ComponentFixture<GameInstPropsComponent>;
  let service: GameInstPropsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'game-inst-props', component: GameInstPropsComponent }]), HttpClientTestingModule],
      declarations: [GameInstPropsComponent],
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
      .overrideTemplate(GameInstPropsComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GameInstPropsComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(GameInstPropsService);

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
    expect(comp.gameInstProps?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to gameInstPropsService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getGameInstPropsIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getGameInstPropsIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});

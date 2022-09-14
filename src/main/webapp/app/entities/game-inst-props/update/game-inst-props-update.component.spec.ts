import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GameInstPropsFormService } from './game-inst-props-form.service';
import { GameInstPropsService } from '../service/game-inst-props.service';
import { IGameInstProps } from '../game-inst-props.model';
import { IGameInst } from 'app/entities/game-inst/game-inst.model';
import { GameInstService } from 'app/entities/game-inst/service/game-inst.service';

import { GameInstPropsUpdateComponent } from './game-inst-props-update.component';

describe('GameInstProps Management Update Component', () => {
  let comp: GameInstPropsUpdateComponent;
  let fixture: ComponentFixture<GameInstPropsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let gameInstPropsFormService: GameInstPropsFormService;
  let gameInstPropsService: GameInstPropsService;
  let gameInstService: GameInstService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [GameInstPropsUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(GameInstPropsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GameInstPropsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    gameInstPropsFormService = TestBed.inject(GameInstPropsFormService);
    gameInstPropsService = TestBed.inject(GameInstPropsService);
    gameInstService = TestBed.inject(GameInstService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call gameInst query and add missing value', () => {
      const gameInstProps: IGameInstProps = { id: 456 };
      const gameInst: IGameInst = { id: 94154 };
      gameInstProps.gameInst = gameInst;

      const gameInstCollection: IGameInst[] = [{ id: 5587 }];
      jest.spyOn(gameInstService, 'query').mockReturnValue(of(new HttpResponse({ body: gameInstCollection })));
      const expectedCollection: IGameInst[] = [gameInst, ...gameInstCollection];
      jest.spyOn(gameInstService, 'addGameInstToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ gameInstProps });
      comp.ngOnInit();

      expect(gameInstService.query).toHaveBeenCalled();
      expect(gameInstService.addGameInstToCollectionIfMissing).toHaveBeenCalledWith(gameInstCollection, gameInst);
      expect(comp.gameInstsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const gameInstProps: IGameInstProps = { id: 456 };
      const gameInst: IGameInst = { id: 29122 };
      gameInstProps.gameInst = gameInst;

      activatedRoute.data = of({ gameInstProps });
      comp.ngOnInit();

      expect(comp.gameInstsCollection).toContain(gameInst);
      expect(comp.gameInstProps).toEqual(gameInstProps);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGameInstProps>>();
      const gameInstProps = { id: 123 };
      jest.spyOn(gameInstPropsFormService, 'getGameInstProps').mockReturnValue(gameInstProps);
      jest.spyOn(gameInstPropsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gameInstProps });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: gameInstProps }));
      saveSubject.complete();

      // THEN
      expect(gameInstPropsFormService.getGameInstProps).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(gameInstPropsService.update).toHaveBeenCalledWith(expect.objectContaining(gameInstProps));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGameInstProps>>();
      const gameInstProps = { id: 123 };
      jest.spyOn(gameInstPropsFormService, 'getGameInstProps').mockReturnValue({ id: null });
      jest.spyOn(gameInstPropsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gameInstProps: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: gameInstProps }));
      saveSubject.complete();

      // THEN
      expect(gameInstPropsFormService.getGameInstProps).toHaveBeenCalled();
      expect(gameInstPropsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGameInstProps>>();
      const gameInstProps = { id: 123 };
      jest.spyOn(gameInstPropsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gameInstProps });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(gameInstPropsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareGameInst', () => {
      it('Should forward to gameInstService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(gameInstService, 'compareGameInst');
        comp.compareGameInst(entity, entity2);
        expect(gameInstService.compareGameInst).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});

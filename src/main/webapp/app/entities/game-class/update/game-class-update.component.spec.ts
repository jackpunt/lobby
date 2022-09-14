import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GameClassFormService } from './game-class-form.service';
import { GameClassService } from '../service/game-class.service';
import { IGameClass } from '../game-class.model';

import { GameClassUpdateComponent } from './game-class-update.component';

describe('GameClass Management Update Component', () => {
  let comp: GameClassUpdateComponent;
  let fixture: ComponentFixture<GameClassUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let gameClassFormService: GameClassFormService;
  let gameClassService: GameClassService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [GameClassUpdateComponent],
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
      .overrideTemplate(GameClassUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GameClassUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    gameClassFormService = TestBed.inject(GameClassFormService);
    gameClassService = TestBed.inject(GameClassService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const gameClass: IGameClass = { id: 456 };

      activatedRoute.data = of({ gameClass });
      comp.ngOnInit();

      expect(comp.gameClass).toEqual(gameClass);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGameClass>>();
      const gameClass = { id: 123 };
      jest.spyOn(gameClassFormService, 'getGameClass').mockReturnValue(gameClass);
      jest.spyOn(gameClassService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gameClass });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: gameClass }));
      saveSubject.complete();

      // THEN
      expect(gameClassFormService.getGameClass).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(gameClassService.update).toHaveBeenCalledWith(expect.objectContaining(gameClass));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGameClass>>();
      const gameClass = { id: 123 };
      jest.spyOn(gameClassFormService, 'getGameClass').mockReturnValue({ id: null });
      jest.spyOn(gameClassService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gameClass: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: gameClass }));
      saveSubject.complete();

      // THEN
      expect(gameClassFormService.getGameClass).toHaveBeenCalled();
      expect(gameClassService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGameClass>>();
      const gameClass = { id: 123 };
      jest.spyOn(gameClassService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gameClass });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(gameClassService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GameInstFormService } from './game-inst-form.service';
import { GameInstService } from '../service/game-inst.service';
import { IGameInst } from '../game-inst.model';
import { IPlayer } from 'app/entities/player/player.model';
import { PlayerService } from 'app/entities/player/service/player.service';
import { IGameClass } from 'app/entities/game-class/game-class.model';
import { GameClassService } from 'app/entities/game-class/service/game-class.service';

import { GameInstUpdateComponent } from './game-inst-update.component';

describe('GameInst Management Update Component', () => {
  let comp: GameInstUpdateComponent;
  let fixture: ComponentFixture<GameInstUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let gameInstFormService: GameInstFormService;
  let gameInstService: GameInstService;
  let playerService: PlayerService;
  let gameClassService: GameClassService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [GameInstUpdateComponent],
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
      .overrideTemplate(GameInstUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GameInstUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    gameInstFormService = TestBed.inject(GameInstFormService);
    gameInstService = TestBed.inject(GameInstService);
    playerService = TestBed.inject(PlayerService);
    gameClassService = TestBed.inject(GameClassService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Player query and add missing value', () => {
      const gameInst: IGameInst = { id: 456 };
      const playerA: IPlayer = { id: 24830 };
      gameInst.playerA = playerA;
      const playerB: IPlayer = { id: 673 };
      gameInst.playerB = playerB;

      const playerCollection: IPlayer[] = [{ id: 30253 }];
      jest.spyOn(playerService, 'query').mockReturnValue(of(new HttpResponse({ body: playerCollection })));
      const additionalPlayers = [playerA, playerB];
      const expectedCollection: IPlayer[] = [...additionalPlayers, ...playerCollection];
      jest.spyOn(playerService, 'addPlayerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ gameInst });
      comp.ngOnInit();

      expect(playerService.query).toHaveBeenCalled();
      expect(playerService.addPlayerToCollectionIfMissing).toHaveBeenCalledWith(
        playerCollection,
        ...additionalPlayers.map(expect.objectContaining)
      );
      expect(comp.playersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call GameClass query and add missing value', () => {
      const gameInst: IGameInst = { id: 456 };
      const gameClass: IGameClass = { id: 15358 };
      gameInst.gameClass = gameClass;

      const gameClassCollection: IGameClass[] = [{ id: 17421 }];
      jest.spyOn(gameClassService, 'query').mockReturnValue(of(new HttpResponse({ body: gameClassCollection })));
      const additionalGameClasses = [gameClass];
      const expectedCollection: IGameClass[] = [...additionalGameClasses, ...gameClassCollection];
      jest.spyOn(gameClassService, 'addGameClassToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ gameInst });
      comp.ngOnInit();

      expect(gameClassService.query).toHaveBeenCalled();
      expect(gameClassService.addGameClassToCollectionIfMissing).toHaveBeenCalledWith(
        gameClassCollection,
        ...additionalGameClasses.map(expect.objectContaining)
      );
      expect(comp.gameClassesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const gameInst: IGameInst = { id: 456 };
      const playerA: IPlayer = { id: 32095 };
      gameInst.playerA = playerA;
      const playerB: IPlayer = { id: 87163 };
      gameInst.playerB = playerB;
      const gameClass: IGameClass = { id: 70074 };
      gameInst.gameClass = gameClass;

      activatedRoute.data = of({ gameInst });
      comp.ngOnInit();

      expect(comp.playersSharedCollection).toContain(playerA);
      expect(comp.playersSharedCollection).toContain(playerB);
      expect(comp.gameClassesSharedCollection).toContain(gameClass);
      expect(comp.gameInst).toEqual(gameInst);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGameInst>>();
      const gameInst = { id: 123 };
      jest.spyOn(gameInstFormService, 'getGameInst').mockReturnValue(gameInst);
      jest.spyOn(gameInstService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gameInst });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: gameInst }));
      saveSubject.complete();

      // THEN
      expect(gameInstFormService.getGameInst).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(gameInstService.update).toHaveBeenCalledWith(expect.objectContaining(gameInst));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGameInst>>();
      const gameInst = { id: 123 };
      jest.spyOn(gameInstFormService, 'getGameInst').mockReturnValue({ id: null });
      jest.spyOn(gameInstService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gameInst: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: gameInst }));
      saveSubject.complete();

      // THEN
      expect(gameInstFormService.getGameInst).toHaveBeenCalled();
      expect(gameInstService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGameInst>>();
      const gameInst = { id: 123 };
      jest.spyOn(gameInstService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gameInst });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(gameInstService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePlayer', () => {
      it('Should forward to playerService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(playerService, 'comparePlayer');
        comp.comparePlayer(entity, entity2);
        expect(playerService.comparePlayer).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareGameClass', () => {
      it('Should forward to gameClassService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(gameClassService, 'compareGameClass');
        comp.compareGameClass(entity, entity2);
        expect(gameClassService.compareGameClass).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});

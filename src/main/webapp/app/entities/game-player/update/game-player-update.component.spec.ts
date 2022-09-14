import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GamePlayerFormService } from './game-player-form.service';
import { GamePlayerService } from '../service/game-player.service';
import { IGamePlayer } from '../game-player.model';
import { IGameInst } from 'app/entities/game-inst/game-inst.model';
import { GameInstService } from 'app/entities/game-inst/service/game-inst.service';
import { IPlayer } from 'app/entities/player/player.model';
import { PlayerService } from 'app/entities/player/service/player.service';

import { GamePlayerUpdateComponent } from './game-player-update.component';

describe('GamePlayer Management Update Component', () => {
  let comp: GamePlayerUpdateComponent;
  let fixture: ComponentFixture<GamePlayerUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let gamePlayerFormService: GamePlayerFormService;
  let gamePlayerService: GamePlayerService;
  let gameInstService: GameInstService;
  let playerService: PlayerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [GamePlayerUpdateComponent],
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
      .overrideTemplate(GamePlayerUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GamePlayerUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    gamePlayerFormService = TestBed.inject(GamePlayerFormService);
    gamePlayerService = TestBed.inject(GamePlayerService);
    gameInstService = TestBed.inject(GameInstService);
    playerService = TestBed.inject(PlayerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call GameInst query and add missing value', () => {
      const gamePlayer: IGamePlayer = { id: 456 };
      const gameInst: IGameInst = { id: 81483 };
      gamePlayer.gameInst = gameInst;

      const gameInstCollection: IGameInst[] = [{ id: 3184 }];
      jest.spyOn(gameInstService, 'query').mockReturnValue(of(new HttpResponse({ body: gameInstCollection })));
      const additionalGameInsts = [gameInst];
      const expectedCollection: IGameInst[] = [...additionalGameInsts, ...gameInstCollection];
      jest.spyOn(gameInstService, 'addGameInstToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ gamePlayer });
      comp.ngOnInit();

      expect(gameInstService.query).toHaveBeenCalled();
      expect(gameInstService.addGameInstToCollectionIfMissing).toHaveBeenCalledWith(
        gameInstCollection,
        ...additionalGameInsts.map(expect.objectContaining)
      );
      expect(comp.gameInstsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Player query and add missing value', () => {
      const gamePlayer: IGamePlayer = { id: 456 };
      const player: IPlayer = { id: 62236 };
      gamePlayer.player = player;

      const playerCollection: IPlayer[] = [{ id: 74673 }];
      jest.spyOn(playerService, 'query').mockReturnValue(of(new HttpResponse({ body: playerCollection })));
      const additionalPlayers = [player];
      const expectedCollection: IPlayer[] = [...additionalPlayers, ...playerCollection];
      jest.spyOn(playerService, 'addPlayerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ gamePlayer });
      comp.ngOnInit();

      expect(playerService.query).toHaveBeenCalled();
      expect(playerService.addPlayerToCollectionIfMissing).toHaveBeenCalledWith(
        playerCollection,
        ...additionalPlayers.map(expect.objectContaining)
      );
      expect(comp.playersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const gamePlayer: IGamePlayer = { id: 456 };
      const gameInst: IGameInst = { id: 79117 };
      gamePlayer.gameInst = gameInst;
      const player: IPlayer = { id: 63420 };
      gamePlayer.player = player;

      activatedRoute.data = of({ gamePlayer });
      comp.ngOnInit();

      expect(comp.gameInstsSharedCollection).toContain(gameInst);
      expect(comp.playersSharedCollection).toContain(player);
      expect(comp.gamePlayer).toEqual(gamePlayer);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGamePlayer>>();
      const gamePlayer = { id: 123 };
      jest.spyOn(gamePlayerFormService, 'getGamePlayer').mockReturnValue(gamePlayer);
      jest.spyOn(gamePlayerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gamePlayer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: gamePlayer }));
      saveSubject.complete();

      // THEN
      expect(gamePlayerFormService.getGamePlayer).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(gamePlayerService.update).toHaveBeenCalledWith(expect.objectContaining(gamePlayer));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGamePlayer>>();
      const gamePlayer = { id: 123 };
      jest.spyOn(gamePlayerFormService, 'getGamePlayer').mockReturnValue({ id: null });
      jest.spyOn(gamePlayerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gamePlayer: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: gamePlayer }));
      saveSubject.complete();

      // THEN
      expect(gamePlayerFormService.getGamePlayer).toHaveBeenCalled();
      expect(gamePlayerService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGamePlayer>>();
      const gamePlayer = { id: 123 };
      jest.spyOn(gamePlayerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gamePlayer });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(gamePlayerService.update).toHaveBeenCalled();
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

    describe('comparePlayer', () => {
      it('Should forward to playerService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(playerService, 'comparePlayer');
        comp.comparePlayer(entity, entity2);
        expect(playerService.comparePlayer).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});

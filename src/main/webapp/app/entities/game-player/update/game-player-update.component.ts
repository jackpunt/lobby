import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { GamePlayerFormService, GamePlayerFormGroup } from './game-player-form.service';
import { IGamePlayer } from '../game-player.model';
import { GamePlayerService } from '../service/game-player.service';
import { IGameInst } from 'app/entities/game-inst/game-inst.model';
import { GameInstService } from 'app/entities/game-inst/service/game-inst.service';
import { IPlayer } from 'app/entities/player/player.model';
import { PlayerService } from 'app/entities/player/service/player.service';

@Component({
  selector: 'jhi-game-player-update',
  templateUrl: './game-player-update.component.html',
})
export class GamePlayerUpdateComponent implements OnInit {
  isSaving = false;
  gamePlayer: IGamePlayer | null = null;

  gameInstsSharedCollection: IGameInst[] = [];
  playersSharedCollection: IPlayer[] = [];

  editForm: GamePlayerFormGroup = this.gamePlayerFormService.createGamePlayerFormGroup();

  constructor(
    protected gamePlayerService: GamePlayerService,
    protected gamePlayerFormService: GamePlayerFormService,
    protected gameInstService: GameInstService,
    protected playerService: PlayerService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareGameInst = (o1: IGameInst | null, o2: IGameInst | null): boolean => this.gameInstService.compareGameInst(o1, o2);

  comparePlayer = (o1: IPlayer | null, o2: IPlayer | null): boolean => this.playerService.comparePlayer(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ gamePlayer }) => {
      this.gamePlayer = gamePlayer;
      if (gamePlayer) {
        this.updateForm(gamePlayer);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const gamePlayer = this.gamePlayerFormService.getGamePlayer(this.editForm);
    if (gamePlayer.id !== null) {
      this.subscribeToSaveResponse(this.gamePlayerService.update(gamePlayer));
    } else {
      this.subscribeToSaveResponse(this.gamePlayerService.create(gamePlayer));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGamePlayer>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(gamePlayer: IGamePlayer): void {
    this.gamePlayer = gamePlayer;
    this.gamePlayerFormService.resetForm(this.editForm, gamePlayer);

    this.gameInstsSharedCollection = this.gameInstService.addGameInstToCollectionIfMissing<IGameInst>(
      this.gameInstsSharedCollection,
      gamePlayer.gameInst
    );
    this.playersSharedCollection = this.playerService.addPlayerToCollectionIfMissing<IPlayer>(
      this.playersSharedCollection,
      gamePlayer.player
    );
  }

  protected loadRelationshipsOptions(): void {
    this.gameInstService
      .query()
      .pipe(map((res: HttpResponse<IGameInst[]>) => res.body ?? []))
      .pipe(
        map((gameInsts: IGameInst[]) =>
          this.gameInstService.addGameInstToCollectionIfMissing<IGameInst>(gameInsts, this.gamePlayer?.gameInst)
        )
      )
      .subscribe((gameInsts: IGameInst[]) => (this.gameInstsSharedCollection = gameInsts));

    this.playerService
      .query()
      .pipe(map((res: HttpResponse<IPlayer[]>) => res.body ?? []))
      .pipe(map((players: IPlayer[]) => this.playerService.addPlayerToCollectionIfMissing<IPlayer>(players, this.gamePlayer?.player)))
      .subscribe((players: IPlayer[]) => (this.playersSharedCollection = players));
  }
}

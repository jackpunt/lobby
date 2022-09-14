import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { GameInstFormService, GameInstFormGroup } from './game-inst-form.service';
import { IGameInst } from '../game-inst.model';
import { GameInstService } from '../service/game-inst.service';
import { IPlayer } from 'app/entities/player/player.model';
import { PlayerService } from 'app/entities/player/service/player.service';
import { IGameClass } from 'app/entities/game-class/game-class.model';
import { GameClassService } from 'app/entities/game-class/service/game-class.service';

@Component({
  selector: 'jhi-game-inst-update',
  templateUrl: './game-inst-update.component.html',
})
export class GameInstUpdateComponent implements OnInit {
  isSaving = false;
  gameInst: IGameInst | null = null;

  playersSharedCollection: IPlayer[] = [];
  gameClassesSharedCollection: IGameClass[] = [];

  editForm: GameInstFormGroup = this.gameInstFormService.createGameInstFormGroup();

  constructor(
    protected gameInstService: GameInstService,
    protected gameInstFormService: GameInstFormService,
    protected playerService: PlayerService,
    protected gameClassService: GameClassService,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePlayer = (o1: IPlayer | null, o2: IPlayer | null): boolean => this.playerService.comparePlayer(o1, o2);

  compareGameClass = (o1: IGameClass | null, o2: IGameClass | null): boolean => this.gameClassService.compareGameClass(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ gameInst }) => {
      this.gameInst = gameInst;
      if (gameInst) {
        this.updateForm(gameInst);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const gameInst = this.gameInstFormService.getGameInst(this.editForm);
    if (gameInst.id !== null) {
      this.subscribeToSaveResponse(this.gameInstService.update(gameInst));
    } else {
      this.subscribeToSaveResponse(this.gameInstService.create(gameInst));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGameInst>>): void {
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

  protected updateForm(gameInst: IGameInst): void {
    this.gameInst = gameInst;
    this.gameInstFormService.resetForm(this.editForm, gameInst);

    this.playersSharedCollection = this.playerService.addPlayerToCollectionIfMissing<IPlayer>(
      this.playersSharedCollection,
      gameInst.playerA,
      gameInst.playerB
    );
    this.gameClassesSharedCollection = this.gameClassService.addGameClassToCollectionIfMissing<IGameClass>(
      this.gameClassesSharedCollection,
      gameInst.gameClass
    );
  }

  protected loadRelationshipsOptions(): void {
    this.playerService
      .query()
      .pipe(map((res: HttpResponse<IPlayer[]>) => res.body ?? []))
      .pipe(
        map((players: IPlayer[]) =>
          this.playerService.addPlayerToCollectionIfMissing<IPlayer>(players, this.gameInst?.playerA, this.gameInst?.playerB)
        )
      )
      .subscribe((players: IPlayer[]) => (this.playersSharedCollection = players));

    this.gameClassService
      .query()
      .pipe(map((res: HttpResponse<IGameClass[]>) => res.body ?? []))
      .pipe(
        map((gameClasses: IGameClass[]) =>
          this.gameClassService.addGameClassToCollectionIfMissing<IGameClass>(gameClasses, this.gameInst?.gameClass)
        )
      )
      .subscribe((gameClasses: IGameClass[]) => (this.gameClassesSharedCollection = gameClasses));
  }
}

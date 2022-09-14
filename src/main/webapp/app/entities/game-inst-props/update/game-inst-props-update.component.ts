import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { GameInstPropsFormService, GameInstPropsFormGroup } from './game-inst-props-form.service';
import { IGameInstProps } from '../game-inst-props.model';
import { GameInstPropsService } from '../service/game-inst-props.service';
import { IGameInst } from 'app/entities/game-inst/game-inst.model';
import { GameInstService } from 'app/entities/game-inst/service/game-inst.service';

@Component({
  selector: 'jhi-game-inst-props-update',
  templateUrl: './game-inst-props-update.component.html',
})
export class GameInstPropsUpdateComponent implements OnInit {
  isSaving = false;
  gameInstProps: IGameInstProps | null = null;

  gameInstsCollection: IGameInst[] = [];

  editForm: GameInstPropsFormGroup = this.gameInstPropsFormService.createGameInstPropsFormGroup();

  constructor(
    protected gameInstPropsService: GameInstPropsService,
    protected gameInstPropsFormService: GameInstPropsFormService,
    protected gameInstService: GameInstService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareGameInst = (o1: IGameInst | null, o2: IGameInst | null): boolean => this.gameInstService.compareGameInst(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ gameInstProps }) => {
      this.gameInstProps = gameInstProps;
      if (gameInstProps) {
        this.updateForm(gameInstProps);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const gameInstProps = this.gameInstPropsFormService.getGameInstProps(this.editForm);
    if (gameInstProps.id !== null) {
      this.subscribeToSaveResponse(this.gameInstPropsService.update(gameInstProps));
    } else {
      this.subscribeToSaveResponse(this.gameInstPropsService.create(gameInstProps));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGameInstProps>>): void {
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

  protected updateForm(gameInstProps: IGameInstProps): void {
    this.gameInstProps = gameInstProps;
    this.gameInstPropsFormService.resetForm(this.editForm, gameInstProps);

    this.gameInstsCollection = this.gameInstService.addGameInstToCollectionIfMissing<IGameInst>(
      this.gameInstsCollection,
      gameInstProps.gameInst
    );
  }

  protected loadRelationshipsOptions(): void {
    this.gameInstService
      .query({ filter: 'props-is-null' })
      .pipe(map((res: HttpResponse<IGameInst[]>) => res.body ?? []))
      .pipe(
        map((gameInsts: IGameInst[]) =>
          this.gameInstService.addGameInstToCollectionIfMissing<IGameInst>(gameInsts, this.gameInstProps?.gameInst)
        )
      )
      .subscribe((gameInsts: IGameInst[]) => (this.gameInstsCollection = gameInsts));
  }
}

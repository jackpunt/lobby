import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { GameClassFormService, GameClassFormGroup } from './game-class-form.service';
import { IGameClass } from '../game-class.model';
import { GameClassService } from '../service/game-class.service';

@Component({
  selector: 'jhi-game-class-update',
  templateUrl: './game-class-update.component.html',
})
export class GameClassUpdateComponent implements OnInit {
  isSaving = false;
  gameClass: IGameClass | null = null;

  editForm: GameClassFormGroup = this.gameClassFormService.createGameClassFormGroup();

  constructor(
    protected gameClassService: GameClassService,
    protected gameClassFormService: GameClassFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ gameClass }) => {
      this.gameClass = gameClass;
      if (gameClass) {
        this.updateForm(gameClass);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const gameClass = this.gameClassFormService.getGameClass(this.editForm);
    if (gameClass.id !== null) {
      this.subscribeToSaveResponse(this.gameClassService.update(gameClass));
    } else {
      this.subscribeToSaveResponse(this.gameClassService.create(gameClass));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGameClass>>): void {
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

  protected updateForm(gameClass: IGameClass): void {
    this.gameClass = gameClass;
    this.gameClassFormService.resetForm(this.editForm, gameClass);
  }
}

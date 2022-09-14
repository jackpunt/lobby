import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { PlayerFormService, PlayerFormGroup } from './player-form.service';
import { IPlayer } from '../player.model';
import { PlayerService } from '../service/player.service';
import { IGameClass } from 'app/entities/game-class/game-class.model';
import { GameClassService } from 'app/entities/game-class/service/game-class.service';
import { IAsset } from 'app/entities/asset/asset.model';
import { AssetService } from 'app/entities/asset/service/asset.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-player-update',
  templateUrl: './player-update.component.html',
})
export class PlayerUpdateComponent implements OnInit {
  isSaving = false;
  player: IPlayer | null = null;

  gameClassesSharedCollection: IGameClass[] = [];
  assetsSharedCollection: IAsset[] = [];
  usersSharedCollection: IUser[] = [];

  editForm: PlayerFormGroup = this.playerFormService.createPlayerFormGroup();

  constructor(
    protected playerService: PlayerService,
    protected playerFormService: PlayerFormService,
    protected gameClassService: GameClassService,
    protected assetService: AssetService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareGameClass = (o1: IGameClass | null, o2: IGameClass | null): boolean => this.gameClassService.compareGameClass(o1, o2);

  compareAsset = (o1: IAsset | null, o2: IAsset | null): boolean => this.assetService.compareAsset(o1, o2);

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ player }) => {
      this.player = player;
      if (player) {
        this.updateForm(player);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const player = this.playerFormService.getPlayer(this.editForm);
    if (player.id !== null) {
      this.subscribeToSaveResponse(this.playerService.update(player));
    } else {
      this.subscribeToSaveResponse(this.playerService.create(player));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPlayer>>): void {
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

  protected updateForm(player: IPlayer): void {
    this.player = player;
    this.playerFormService.resetForm(this.editForm, player);

    this.gameClassesSharedCollection = this.gameClassService.addGameClassToCollectionIfMissing<IGameClass>(
      this.gameClassesSharedCollection,
      player.gameClass
    );
    this.assetsSharedCollection = this.assetService.addAssetToCollectionIfMissing<IAsset>(this.assetsSharedCollection, player.mainJar);
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, player.user);
  }

  protected loadRelationshipsOptions(): void {
    this.gameClassService
      .query()
      .pipe(map((res: HttpResponse<IGameClass[]>) => res.body ?? []))
      .pipe(
        map((gameClasses: IGameClass[]) =>
          this.gameClassService.addGameClassToCollectionIfMissing<IGameClass>(gameClasses, this.player?.gameClass)
        )
      )
      .subscribe((gameClasses: IGameClass[]) => (this.gameClassesSharedCollection = gameClasses));

    this.assetService
      .query()
      .pipe(map((res: HttpResponse<IAsset[]>) => res.body ?? []))
      .pipe(map((assets: IAsset[]) => this.assetService.addAssetToCollectionIfMissing<IAsset>(assets, this.player?.mainJar)))
      .subscribe((assets: IAsset[]) => (this.assetsSharedCollection = assets));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.player?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}

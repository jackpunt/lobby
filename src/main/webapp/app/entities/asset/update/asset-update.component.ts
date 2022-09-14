import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { AssetFormService, AssetFormGroup } from './asset-form.service';
import { IAsset } from '../asset.model';
import { AssetService } from '../service/asset.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-asset-update',
  templateUrl: './asset-update.component.html',
})
export class AssetUpdateComponent implements OnInit {
  isSaving = false;
  asset: IAsset | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: AssetFormGroup = this.assetFormService.createAssetFormGroup();

  constructor(
    protected assetService: AssetService,
    protected assetFormService: AssetFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ asset }) => {
      this.asset = asset;
      if (asset) {
        this.updateForm(asset);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const asset = this.assetFormService.getAsset(this.editForm);
    if (asset.id !== null) {
      this.subscribeToSaveResponse(this.assetService.update(asset));
    } else {
      this.subscribeToSaveResponse(this.assetService.create(asset));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAsset>>): void {
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

  protected updateForm(asset: IAsset): void {
    this.asset = asset;
    this.assetFormService.resetForm(this.editForm, asset);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, asset.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.asset?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}

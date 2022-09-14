import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { AccountInfoFormService, AccountInfoFormGroup } from './account-info-form.service';
import { IAccountInfo } from '../account-info.model';
import { AccountInfoService } from '../service/account-info.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-account-info-update',
  templateUrl: './account-info-update.component.html',
})
export class AccountInfoUpdateComponent implements OnInit {
  isSaving = false;
  accountInfo: IAccountInfo | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: AccountInfoFormGroup = this.accountInfoFormService.createAccountInfoFormGroup();

  constructor(
    protected accountInfoService: AccountInfoService,
    protected accountInfoFormService: AccountInfoFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ accountInfo }) => {
      this.accountInfo = accountInfo;
      if (accountInfo) {
        this.updateForm(accountInfo);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const accountInfo = this.accountInfoFormService.getAccountInfo(this.editForm);
    if (accountInfo.id !== null) {
      this.subscribeToSaveResponse(this.accountInfoService.update(accountInfo));
    } else {
      this.subscribeToSaveResponse(this.accountInfoService.create(accountInfo));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAccountInfo>>): void {
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

  protected updateForm(accountInfo: IAccountInfo): void {
    this.accountInfo = accountInfo;
    this.accountInfoFormService.resetForm(this.editForm, accountInfo);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, accountInfo.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.accountInfo?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}

import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { MemberGamePropsFormService, MemberGamePropsFormGroup } from './member-game-props-form.service';
import { IMemberGameProps } from '../member-game-props.model';
import { MemberGamePropsService } from '../service/member-game-props.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IGameClass } from 'app/entities/game-class/game-class.model';
import { GameClassService } from 'app/entities/game-class/service/game-class.service';

@Component({
  selector: 'jhi-member-game-props-update',
  templateUrl: './member-game-props-update.component.html',
})
export class MemberGamePropsUpdateComponent implements OnInit {
  isSaving = false;
  memberGameProps: IMemberGameProps | null = null;

  usersSharedCollection: IUser[] = [];
  gameClassesSharedCollection: IGameClass[] = [];

  editForm: MemberGamePropsFormGroup = this.memberGamePropsFormService.createMemberGamePropsFormGroup();

  constructor(
    protected memberGamePropsService: MemberGamePropsService,
    protected memberGamePropsFormService: MemberGamePropsFormService,
    protected userService: UserService,
    protected gameClassService: GameClassService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareGameClass = (o1: IGameClass | null, o2: IGameClass | null): boolean => this.gameClassService.compareGameClass(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ memberGameProps }) => {
      this.memberGameProps = memberGameProps;
      if (memberGameProps) {
        this.updateForm(memberGameProps);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const memberGameProps = this.memberGamePropsFormService.getMemberGameProps(this.editForm);
    if (memberGameProps.id !== null) {
      this.subscribeToSaveResponse(this.memberGamePropsService.update(memberGameProps));
    } else {
      this.subscribeToSaveResponse(this.memberGamePropsService.create(memberGameProps));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMemberGameProps>>): void {
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

  protected updateForm(memberGameProps: IMemberGameProps): void {
    this.memberGameProps = memberGameProps;
    this.memberGamePropsFormService.resetForm(this.editForm, memberGameProps);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, memberGameProps.user);
    this.gameClassesSharedCollection = this.gameClassService.addGameClassToCollectionIfMissing<IGameClass>(
      this.gameClassesSharedCollection,
      memberGameProps.gameClass
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.memberGameProps?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.gameClassService
      .query()
      .pipe(map((res: HttpResponse<IGameClass[]>) => res.body ?? []))
      .pipe(
        map((gameClasses: IGameClass[]) =>
          this.gameClassService.addGameClassToCollectionIfMissing<IGameClass>(gameClasses, this.memberGameProps?.gameClass)
        )
      )
      .subscribe((gameClasses: IGameClass[]) => (this.gameClassesSharedCollection = gameClasses));
  }
}

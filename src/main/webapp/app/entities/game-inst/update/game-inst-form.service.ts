import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IGameInst, NewGameInst } from '../game-inst.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGameInst for edit and NewGameInstFormGroupInput for create.
 */
type GameInstFormGroupInput = IGameInst | PartialWithRequiredKeyOf<NewGameInst>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IGameInst | NewGameInst> = Omit<T, 'created' | 'started' | 'finished' | 'updated'> & {
  created?: string | null;
  started?: string | null;
  finished?: string | null;
  updated?: string | null;
};

type GameInstFormRawValue = FormValueOf<IGameInst>;

type NewGameInstFormRawValue = FormValueOf<NewGameInst>;

type GameInstFormDefaults = Pick<NewGameInst, 'id' | 'created' | 'started' | 'finished' | 'updated'>;

type GameInstFormGroupContent = {
  id: FormControl<GameInstFormRawValue['id'] | NewGameInst['id']>;
  version: FormControl<GameInstFormRawValue['version']>;
  gameName: FormControl<GameInstFormRawValue['gameName']>;
  hostUrl: FormControl<GameInstFormRawValue['hostUrl']>;
  passcode: FormControl<GameInstFormRawValue['passcode']>;
  created: FormControl<GameInstFormRawValue['created']>;
  started: FormControl<GameInstFormRawValue['started']>;
  finished: FormControl<GameInstFormRawValue['finished']>;
  updated: FormControl<GameInstFormRawValue['updated']>;
  scoreA: FormControl<GameInstFormRawValue['scoreA']>;
  scoreB: FormControl<GameInstFormRawValue['scoreB']>;
  ticks: FormControl<GameInstFormRawValue['ticks']>;
  playerA: FormControl<GameInstFormRawValue['playerA']>;
  playerB: FormControl<GameInstFormRawValue['playerB']>;
  gameClass: FormControl<GameInstFormRawValue['gameClass']>;
};

export type GameInstFormGroup = FormGroup<GameInstFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class GameInstFormService {
  createGameInstFormGroup(gameInst: GameInstFormGroupInput = { id: null }): GameInstFormGroup {
    const gameInstRawValue = this.convertGameInstToGameInstRawValue({
      ...this.getFormDefaults(),
      ...gameInst,
    });
    return new FormGroup<GameInstFormGroupContent>({
      id: new FormControl(
        { value: gameInstRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      version: new FormControl(gameInstRawValue.version),
      gameName: new FormControl(gameInstRawValue.gameName, {
        validators: [Validators.maxLength(64)],
      }),
      hostUrl: new FormControl(gameInstRawValue.hostUrl, {
        validators: [Validators.maxLength(64)],
      }),
      passcode: new FormControl(gameInstRawValue.passcode, {
        validators: [Validators.maxLength(64)],
      }),
      created: new FormControl(gameInstRawValue.created, {
        validators: [Validators.required],
      }),
      started: new FormControl(gameInstRawValue.started),
      finished: new FormControl(gameInstRawValue.finished),
      updated: new FormControl(gameInstRawValue.updated, {
        validators: [Validators.required],
      }),
      scoreA: new FormControl(gameInstRawValue.scoreA),
      scoreB: new FormControl(gameInstRawValue.scoreB),
      ticks: new FormControl(gameInstRawValue.ticks),
      playerA: new FormControl(gameInstRawValue.playerA),
      playerB: new FormControl(gameInstRawValue.playerB),
      gameClass: new FormControl(gameInstRawValue.gameClass),
    });
  }

  getGameInst(form: GameInstFormGroup): IGameInst | NewGameInst {
    return this.convertGameInstRawValueToGameInst(form.getRawValue() as GameInstFormRawValue | NewGameInstFormRawValue);
  }

  resetForm(form: GameInstFormGroup, gameInst: GameInstFormGroupInput): void {
    const gameInstRawValue = this.convertGameInstToGameInstRawValue({ ...this.getFormDefaults(), ...gameInst });
    form.reset(
      {
        ...gameInstRawValue,
        id: { value: gameInstRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): GameInstFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      created: currentTime,
      started: currentTime,
      finished: currentTime,
      updated: currentTime,
    };
  }

  private convertGameInstRawValueToGameInst(rawGameInst: GameInstFormRawValue | NewGameInstFormRawValue): IGameInst | NewGameInst {
    return {
      ...rawGameInst,
      created: dayjs(rawGameInst.created, DATE_TIME_FORMAT),
      started: dayjs(rawGameInst.started, DATE_TIME_FORMAT),
      finished: dayjs(rawGameInst.finished, DATE_TIME_FORMAT),
      updated: dayjs(rawGameInst.updated, DATE_TIME_FORMAT),
    };
  }

  private convertGameInstToGameInstRawValue(
    gameInst: IGameInst | (Partial<NewGameInst> & GameInstFormDefaults)
  ): GameInstFormRawValue | PartialWithRequiredKeyOf<NewGameInstFormRawValue> {
    return {
      ...gameInst,
      created: gameInst.created ? gameInst.created.format(DATE_TIME_FORMAT) : undefined,
      started: gameInst.started ? gameInst.started.format(DATE_TIME_FORMAT) : undefined,
      finished: gameInst.finished ? gameInst.finished.format(DATE_TIME_FORMAT) : undefined,
      updated: gameInst.updated ? gameInst.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}

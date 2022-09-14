import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IGameInstProps, NewGameInstProps } from '../game-inst-props.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGameInstProps for edit and NewGameInstPropsFormGroupInput for create.
 */
type GameInstPropsFormGroupInput = IGameInstProps | PartialWithRequiredKeyOf<NewGameInstProps>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IGameInstProps | NewGameInstProps> = Omit<T, 'updated'> & {
  updated?: string | null;
};

type GameInstPropsFormRawValue = FormValueOf<IGameInstProps>;

type NewGameInstPropsFormRawValue = FormValueOf<NewGameInstProps>;

type GameInstPropsFormDefaults = Pick<NewGameInstProps, 'id' | 'updated'>;

type GameInstPropsFormGroupContent = {
  id: FormControl<GameInstPropsFormRawValue['id'] | NewGameInstProps['id']>;
  version: FormControl<GameInstPropsFormRawValue['version']>;
  seed: FormControl<GameInstPropsFormRawValue['seed']>;
  mapName: FormControl<GameInstPropsFormRawValue['mapName']>;
  mapSize: FormControl<GameInstPropsFormRawValue['mapSize']>;
  npcCount: FormControl<GameInstPropsFormRawValue['npcCount']>;
  jsonProps: FormControl<GameInstPropsFormRawValue['jsonProps']>;
  updated: FormControl<GameInstPropsFormRawValue['updated']>;
  gameInst: FormControl<GameInstPropsFormRawValue['gameInst']>;
};

export type GameInstPropsFormGroup = FormGroup<GameInstPropsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class GameInstPropsFormService {
  createGameInstPropsFormGroup(gameInstProps: GameInstPropsFormGroupInput = { id: null }): GameInstPropsFormGroup {
    const gameInstPropsRawValue = this.convertGameInstPropsToGameInstPropsRawValue({
      ...this.getFormDefaults(),
      ...gameInstProps,
    });
    return new FormGroup<GameInstPropsFormGroupContent>({
      id: new FormControl(
        { value: gameInstPropsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      version: new FormControl(gameInstPropsRawValue.version),
      seed: new FormControl(gameInstPropsRawValue.seed),
      mapName: new FormControl(gameInstPropsRawValue.mapName, {
        validators: [Validators.maxLength(45)],
      }),
      mapSize: new FormControl(gameInstPropsRawValue.mapSize),
      npcCount: new FormControl(gameInstPropsRawValue.npcCount),
      jsonProps: new FormControl(gameInstPropsRawValue.jsonProps),
      updated: new FormControl(gameInstPropsRawValue.updated, {
        validators: [Validators.required],
      }),
      gameInst: new FormControl(gameInstPropsRawValue.gameInst),
    });
  }

  getGameInstProps(form: GameInstPropsFormGroup): IGameInstProps | NewGameInstProps {
    return this.convertGameInstPropsRawValueToGameInstProps(form.getRawValue() as GameInstPropsFormRawValue | NewGameInstPropsFormRawValue);
  }

  resetForm(form: GameInstPropsFormGroup, gameInstProps: GameInstPropsFormGroupInput): void {
    const gameInstPropsRawValue = this.convertGameInstPropsToGameInstPropsRawValue({ ...this.getFormDefaults(), ...gameInstProps });
    form.reset(
      {
        ...gameInstPropsRawValue,
        id: { value: gameInstPropsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): GameInstPropsFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      updated: currentTime,
    };
  }

  private convertGameInstPropsRawValueToGameInstProps(
    rawGameInstProps: GameInstPropsFormRawValue | NewGameInstPropsFormRawValue
  ): IGameInstProps | NewGameInstProps {
    return {
      ...rawGameInstProps,
      updated: dayjs(rawGameInstProps.updated, DATE_TIME_FORMAT),
    };
  }

  private convertGameInstPropsToGameInstPropsRawValue(
    gameInstProps: IGameInstProps | (Partial<NewGameInstProps> & GameInstPropsFormDefaults)
  ): GameInstPropsFormRawValue | PartialWithRequiredKeyOf<NewGameInstPropsFormRawValue> {
    return {
      ...gameInstProps,
      updated: gameInstProps.updated ? gameInstProps.updated.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}

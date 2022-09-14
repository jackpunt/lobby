import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPlayer, NewPlayer } from '../player.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPlayer for edit and NewPlayerFormGroupInput for create.
 */
type PlayerFormGroupInput = IPlayer | PartialWithRequiredKeyOf<NewPlayer>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPlayer | NewPlayer> = Omit<T, 'scoreTime' | 'rankTime'> & {
  scoreTime?: string | null;
  rankTime?: string | null;
};

type PlayerFormRawValue = FormValueOf<IPlayer>;

type NewPlayerFormRawValue = FormValueOf<NewPlayer>;

type PlayerFormDefaults = Pick<NewPlayer, 'id' | 'scoreTime' | 'rankTime'>;

type PlayerFormGroupContent = {
  id: FormControl<PlayerFormRawValue['id'] | NewPlayer['id']>;
  version: FormControl<PlayerFormRawValue['version']>;
  name: FormControl<PlayerFormRawValue['name']>;
  rank: FormControl<PlayerFormRawValue['rank']>;
  score: FormControl<PlayerFormRawValue['score']>;
  scoreTime: FormControl<PlayerFormRawValue['scoreTime']>;
  rankTime: FormControl<PlayerFormRawValue['rankTime']>;
  displayClient: FormControl<PlayerFormRawValue['displayClient']>;
  gameClass: FormControl<PlayerFormRawValue['gameClass']>;
  mainJar: FormControl<PlayerFormRawValue['mainJar']>;
  user: FormControl<PlayerFormRawValue['user']>;
};

export type PlayerFormGroup = FormGroup<PlayerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PlayerFormService {
  createPlayerFormGroup(player: PlayerFormGroupInput = { id: null }): PlayerFormGroup {
    const playerRawValue = this.convertPlayerToPlayerRawValue({
      ...this.getFormDefaults(),
      ...player,
    });
    return new FormGroup<PlayerFormGroupContent>({
      id: new FormControl(
        { value: playerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      version: new FormControl(playerRawValue.version),
      name: new FormControl(playerRawValue.name, {
        validators: [Validators.maxLength(64)],
      }),
      rank: new FormControl(playerRawValue.rank),
      score: new FormControl(playerRawValue.score),
      scoreTime: new FormControl(playerRawValue.scoreTime),
      rankTime: new FormControl(playerRawValue.rankTime),
      displayClient: new FormControl(playerRawValue.displayClient, {
        validators: [Validators.maxLength(64)],
      }),
      gameClass: new FormControl(playerRawValue.gameClass),
      mainJar: new FormControl(playerRawValue.mainJar),
      user: new FormControl(playerRawValue.user),
    });
  }

  getPlayer(form: PlayerFormGroup): IPlayer | NewPlayer {
    return this.convertPlayerRawValueToPlayer(form.getRawValue() as PlayerFormRawValue | NewPlayerFormRawValue);
  }

  resetForm(form: PlayerFormGroup, player: PlayerFormGroupInput): void {
    const playerRawValue = this.convertPlayerToPlayerRawValue({ ...this.getFormDefaults(), ...player });
    form.reset(
      {
        ...playerRawValue,
        id: { value: playerRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PlayerFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      scoreTime: currentTime,
      rankTime: currentTime,
    };
  }

  private convertPlayerRawValueToPlayer(rawPlayer: PlayerFormRawValue | NewPlayerFormRawValue): IPlayer | NewPlayer {
    return {
      ...rawPlayer,
      scoreTime: dayjs(rawPlayer.scoreTime, DATE_TIME_FORMAT),
      rankTime: dayjs(rawPlayer.rankTime, DATE_TIME_FORMAT),
    };
  }

  private convertPlayerToPlayerRawValue(
    player: IPlayer | (Partial<NewPlayer> & PlayerFormDefaults)
  ): PlayerFormRawValue | PartialWithRequiredKeyOf<NewPlayerFormRawValue> {
    return {
      ...player,
      scoreTime: player.scoreTime ? player.scoreTime.format(DATE_TIME_FORMAT) : undefined,
      rankTime: player.rankTime ? player.rankTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}

import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IGamePlayer, NewGamePlayer } from '../game-player.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGamePlayer for edit and NewGamePlayerFormGroupInput for create.
 */
type GamePlayerFormGroupInput = IGamePlayer | PartialWithRequiredKeyOf<NewGamePlayer>;

type GamePlayerFormDefaults = Pick<NewGamePlayer, 'id'>;

type GamePlayerFormGroupContent = {
  id: FormControl<IGamePlayer['id'] | NewGamePlayer['id']>;
  version: FormControl<IGamePlayer['version']>;
  role: FormControl<IGamePlayer['role']>;
  ready: FormControl<IGamePlayer['ready']>;
  gameInst: FormControl<IGamePlayer['gameInst']>;
  player: FormControl<IGamePlayer['player']>;
};

export type GamePlayerFormGroup = FormGroup<GamePlayerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class GamePlayerFormService {
  createGamePlayerFormGroup(gamePlayer: GamePlayerFormGroupInput = { id: null }): GamePlayerFormGroup {
    const gamePlayerRawValue = {
      ...this.getFormDefaults(),
      ...gamePlayer,
    };
    return new FormGroup<GamePlayerFormGroupContent>({
      id: new FormControl(
        { value: gamePlayerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      version: new FormControl(gamePlayerRawValue.version),
      role: new FormControl(gamePlayerRawValue.role, {
        validators: [Validators.required, Validators.maxLength(4)],
      }),
      ready: new FormControl(gamePlayerRawValue.ready, {
        validators: [Validators.required],
      }),
      gameInst: new FormControl(gamePlayerRawValue.gameInst),
      player: new FormControl(gamePlayerRawValue.player),
    });
  }

  getGamePlayer(form: GamePlayerFormGroup): IGamePlayer | NewGamePlayer {
    return form.getRawValue() as IGamePlayer | NewGamePlayer;
  }

  resetForm(form: GamePlayerFormGroup, gamePlayer: GamePlayerFormGroupInput): void {
    const gamePlayerRawValue = { ...this.getFormDefaults(), ...gamePlayer };
    form.reset(
      {
        ...gamePlayerRawValue,
        id: { value: gamePlayerRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): GamePlayerFormDefaults {
    return {
      id: null,
    };
  }
}

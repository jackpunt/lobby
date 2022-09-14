import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IMemberGameProps, NewMemberGameProps } from '../member-game-props.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMemberGameProps for edit and NewMemberGamePropsFormGroupInput for create.
 */
type MemberGamePropsFormGroupInput = IMemberGameProps | PartialWithRequiredKeyOf<NewMemberGameProps>;

type MemberGamePropsFormDefaults = Pick<NewMemberGameProps, 'id'>;

type MemberGamePropsFormGroupContent = {
  id: FormControl<IMemberGameProps['id'] | NewMemberGameProps['id']>;
  version: FormControl<IMemberGameProps['version']>;
  seed: FormControl<IMemberGameProps['seed']>;
  mapName: FormControl<IMemberGameProps['mapName']>;
  mapSize: FormControl<IMemberGameProps['mapSize']>;
  npcCount: FormControl<IMemberGameProps['npcCount']>;
  jsonProps: FormControl<IMemberGameProps['jsonProps']>;
  configName: FormControl<IMemberGameProps['configName']>;
  user: FormControl<IMemberGameProps['user']>;
  gameClass: FormControl<IMemberGameProps['gameClass']>;
};

export type MemberGamePropsFormGroup = FormGroup<MemberGamePropsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MemberGamePropsFormService {
  createMemberGamePropsFormGroup(memberGameProps: MemberGamePropsFormGroupInput = { id: null }): MemberGamePropsFormGroup {
    const memberGamePropsRawValue = {
      ...this.getFormDefaults(),
      ...memberGameProps,
    };
    return new FormGroup<MemberGamePropsFormGroupContent>({
      id: new FormControl(
        { value: memberGamePropsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      version: new FormControl(memberGamePropsRawValue.version),
      seed: new FormControl(memberGamePropsRawValue.seed),
      mapName: new FormControl(memberGamePropsRawValue.mapName, {
        validators: [Validators.maxLength(45)],
      }),
      mapSize: new FormControl(memberGamePropsRawValue.mapSize),
      npcCount: new FormControl(memberGamePropsRawValue.npcCount),
      jsonProps: new FormControl(memberGamePropsRawValue.jsonProps),
      configName: new FormControl(memberGamePropsRawValue.configName, {
        validators: [Validators.maxLength(45)],
      }),
      user: new FormControl(memberGamePropsRawValue.user),
      gameClass: new FormControl(memberGamePropsRawValue.gameClass),
    });
  }

  getMemberGameProps(form: MemberGamePropsFormGroup): IMemberGameProps | NewMemberGameProps {
    return form.getRawValue() as IMemberGameProps | NewMemberGameProps;
  }

  resetForm(form: MemberGamePropsFormGroup, memberGameProps: MemberGamePropsFormGroupInput): void {
    const memberGamePropsRawValue = { ...this.getFormDefaults(), ...memberGameProps };
    form.reset(
      {
        ...memberGamePropsRawValue,
        id: { value: memberGamePropsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): MemberGamePropsFormDefaults {
    return {
      id: null,
    };
  }
}

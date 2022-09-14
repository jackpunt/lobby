import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IAsset, NewAsset } from '../asset.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAsset for edit and NewAssetFormGroupInput for create.
 */
type AssetFormGroupInput = IAsset | PartialWithRequiredKeyOf<NewAsset>;

type AssetFormDefaults = Pick<NewAsset, 'id' | 'main' | 'auto'>;

type AssetFormGroupContent = {
  id: FormControl<IAsset['id'] | NewAsset['id']>;
  version: FormControl<IAsset['version']>;
  name: FormControl<IAsset['name']>;
  main: FormControl<IAsset['main']>;
  auto: FormControl<IAsset['auto']>;
  path: FormControl<IAsset['path']>;
  include: FormControl<IAsset['include']>;
  user: FormControl<IAsset['user']>;
};

export type AssetFormGroup = FormGroup<AssetFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AssetFormService {
  createAssetFormGroup(asset: AssetFormGroupInput = { id: null }): AssetFormGroup {
    const assetRawValue = {
      ...this.getFormDefaults(),
      ...asset,
    };
    return new FormGroup<AssetFormGroupContent>({
      id: new FormControl(
        { value: assetRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      version: new FormControl(assetRawValue.version),
      name: new FormControl(assetRawValue.name, {
        validators: [Validators.maxLength(45)],
      }),
      main: new FormControl(assetRawValue.main),
      auto: new FormControl(assetRawValue.auto),
      path: new FormControl(assetRawValue.path),
      include: new FormControl(assetRawValue.include),
      user: new FormControl(assetRawValue.user),
    });
  }

  getAsset(form: AssetFormGroup): IAsset | NewAsset {
    return form.getRawValue() as IAsset | NewAsset;
  }

  resetForm(form: AssetFormGroup, asset: AssetFormGroupInput): void {
    const assetRawValue = { ...this.getFormDefaults(), ...asset };
    form.reset(
      {
        ...assetRawValue,
        id: { value: assetRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): AssetFormDefaults {
    return {
      id: null,
      main: false,
      auto: false,
    };
  }
}

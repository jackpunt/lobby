import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../account-info.test-samples';

import { AccountInfoFormService } from './account-info-form.service';

describe('AccountInfo Form Service', () => {
  let service: AccountInfoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AccountInfoFormService);
  });

  describe('Service methods', () => {
    describe('createAccountInfoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAccountInfoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            version: expect.any(Object),
            type: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });

      it('passing IAccountInfo should create a new form with FormGroup', () => {
        const formGroup = service.createAccountInfoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            version: expect.any(Object),
            type: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });
    });

    describe('getAccountInfo', () => {
      it('should return NewAccountInfo for default AccountInfo initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createAccountInfoFormGroup(sampleWithNewData);

        const accountInfo = service.getAccountInfo(formGroup) as any;

        expect(accountInfo).toMatchObject(sampleWithNewData);
      });

      it('should return NewAccountInfo for empty AccountInfo initial value', () => {
        const formGroup = service.createAccountInfoFormGroup();

        const accountInfo = service.getAccountInfo(formGroup) as any;

        expect(accountInfo).toMatchObject({});
      });

      it('should return IAccountInfo', () => {
        const formGroup = service.createAccountInfoFormGroup(sampleWithRequiredData);

        const accountInfo = service.getAccountInfo(formGroup) as any;

        expect(accountInfo).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAccountInfo should not enable id FormControl', () => {
        const formGroup = service.createAccountInfoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAccountInfo should disable id FormControl', () => {
        const formGroup = service.createAccountInfoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});

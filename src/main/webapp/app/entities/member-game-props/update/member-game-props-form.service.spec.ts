import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../member-game-props.test-samples';

import { MemberGamePropsFormService } from './member-game-props-form.service';

describe('MemberGameProps Form Service', () => {
  let service: MemberGamePropsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MemberGamePropsFormService);
  });

  describe('Service methods', () => {
    describe('createMemberGamePropsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMemberGamePropsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            version: expect.any(Object),
            seed: expect.any(Object),
            mapName: expect.any(Object),
            mapSize: expect.any(Object),
            npcCount: expect.any(Object),
            jsonProps: expect.any(Object),
            configName: expect.any(Object),
            user: expect.any(Object),
            gameClass: expect.any(Object),
          })
        );
      });

      it('passing IMemberGameProps should create a new form with FormGroup', () => {
        const formGroup = service.createMemberGamePropsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            version: expect.any(Object),
            seed: expect.any(Object),
            mapName: expect.any(Object),
            mapSize: expect.any(Object),
            npcCount: expect.any(Object),
            jsonProps: expect.any(Object),
            configName: expect.any(Object),
            user: expect.any(Object),
            gameClass: expect.any(Object),
          })
        );
      });
    });

    describe('getMemberGameProps', () => {
      it('should return NewMemberGameProps for default MemberGameProps initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createMemberGamePropsFormGroup(sampleWithNewData);

        const memberGameProps = service.getMemberGameProps(formGroup) as any;

        expect(memberGameProps).toMatchObject(sampleWithNewData);
      });

      it('should return NewMemberGameProps for empty MemberGameProps initial value', () => {
        const formGroup = service.createMemberGamePropsFormGroup();

        const memberGameProps = service.getMemberGameProps(formGroup) as any;

        expect(memberGameProps).toMatchObject({});
      });

      it('should return IMemberGameProps', () => {
        const formGroup = service.createMemberGamePropsFormGroup(sampleWithRequiredData);

        const memberGameProps = service.getMemberGameProps(formGroup) as any;

        expect(memberGameProps).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMemberGameProps should not enable id FormControl', () => {
        const formGroup = service.createMemberGamePropsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMemberGameProps should disable id FormControl', () => {
        const formGroup = service.createMemberGamePropsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});

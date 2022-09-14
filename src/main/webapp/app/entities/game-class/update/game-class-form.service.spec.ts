import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../game-class.test-samples';

import { GameClassFormService } from './game-class-form.service';

describe('GameClass Form Service', () => {
  let service: GameClassFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GameClassFormService);
  });

  describe('Service methods', () => {
    describe('createGameClassFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createGameClassFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            version: expect.any(Object),
            name: expect.any(Object),
            revision: expect.any(Object),
            launcherPath: expect.any(Object),
            gamePath: expect.any(Object),
            docsPath: expect.any(Object),
            propNames: expect.any(Object),
            updated: expect.any(Object),
          })
        );
      });

      it('passing IGameClass should create a new form with FormGroup', () => {
        const formGroup = service.createGameClassFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            version: expect.any(Object),
            name: expect.any(Object),
            revision: expect.any(Object),
            launcherPath: expect.any(Object),
            gamePath: expect.any(Object),
            docsPath: expect.any(Object),
            propNames: expect.any(Object),
            updated: expect.any(Object),
          })
        );
      });
    });

    describe('getGameClass', () => {
      it('should return NewGameClass for default GameClass initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createGameClassFormGroup(sampleWithNewData);

        const gameClass = service.getGameClass(formGroup) as any;

        expect(gameClass).toMatchObject(sampleWithNewData);
      });

      it('should return NewGameClass for empty GameClass initial value', () => {
        const formGroup = service.createGameClassFormGroup();

        const gameClass = service.getGameClass(formGroup) as any;

        expect(gameClass).toMatchObject({});
      });

      it('should return IGameClass', () => {
        const formGroup = service.createGameClassFormGroup(sampleWithRequiredData);

        const gameClass = service.getGameClass(formGroup) as any;

        expect(gameClass).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IGameClass should not enable id FormControl', () => {
        const formGroup = service.createGameClassFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewGameClass should disable id FormControl', () => {
        const formGroup = service.createGameClassFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});

import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../game-inst-props.test-samples';

import { GameInstPropsFormService } from './game-inst-props-form.service';

describe('GameInstProps Form Service', () => {
  let service: GameInstPropsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GameInstPropsFormService);
  });

  describe('Service methods', () => {
    describe('createGameInstPropsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createGameInstPropsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            version: expect.any(Object),
            seed: expect.any(Object),
            mapName: expect.any(Object),
            mapSize: expect.any(Object),
            npcCount: expect.any(Object),
            jsonProps: expect.any(Object),
            updated: expect.any(Object),
            gameInst: expect.any(Object),
          })
        );
      });

      it('passing IGameInstProps should create a new form with FormGroup', () => {
        const formGroup = service.createGameInstPropsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            version: expect.any(Object),
            seed: expect.any(Object),
            mapName: expect.any(Object),
            mapSize: expect.any(Object),
            npcCount: expect.any(Object),
            jsonProps: expect.any(Object),
            updated: expect.any(Object),
            gameInst: expect.any(Object),
          })
        );
      });
    });

    describe('getGameInstProps', () => {
      it('should return NewGameInstProps for default GameInstProps initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createGameInstPropsFormGroup(sampleWithNewData);

        const gameInstProps = service.getGameInstProps(formGroup) as any;

        expect(gameInstProps).toMatchObject(sampleWithNewData);
      });

      it('should return NewGameInstProps for empty GameInstProps initial value', () => {
        const formGroup = service.createGameInstPropsFormGroup();

        const gameInstProps = service.getGameInstProps(formGroup) as any;

        expect(gameInstProps).toMatchObject({});
      });

      it('should return IGameInstProps', () => {
        const formGroup = service.createGameInstPropsFormGroup(sampleWithRequiredData);

        const gameInstProps = service.getGameInstProps(formGroup) as any;

        expect(gameInstProps).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IGameInstProps should not enable id FormControl', () => {
        const formGroup = service.createGameInstPropsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewGameInstProps should disable id FormControl', () => {
        const formGroup = service.createGameInstPropsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});

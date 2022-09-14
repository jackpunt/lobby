import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../game-inst.test-samples';

import { GameInstFormService } from './game-inst-form.service';

describe('GameInst Form Service', () => {
  let service: GameInstFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GameInstFormService);
  });

  describe('Service methods', () => {
    describe('createGameInstFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createGameInstFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            version: expect.any(Object),
            gameName: expect.any(Object),
            hostUrl: expect.any(Object),
            passcode: expect.any(Object),
            created: expect.any(Object),
            started: expect.any(Object),
            finished: expect.any(Object),
            updated: expect.any(Object),
            scoreA: expect.any(Object),
            scoreB: expect.any(Object),
            ticks: expect.any(Object),
            playerA: expect.any(Object),
            playerB: expect.any(Object),
            gameClass: expect.any(Object),
          })
        );
      });

      it('passing IGameInst should create a new form with FormGroup', () => {
        const formGroup = service.createGameInstFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            version: expect.any(Object),
            gameName: expect.any(Object),
            hostUrl: expect.any(Object),
            passcode: expect.any(Object),
            created: expect.any(Object),
            started: expect.any(Object),
            finished: expect.any(Object),
            updated: expect.any(Object),
            scoreA: expect.any(Object),
            scoreB: expect.any(Object),
            ticks: expect.any(Object),
            playerA: expect.any(Object),
            playerB: expect.any(Object),
            gameClass: expect.any(Object),
          })
        );
      });
    });

    describe('getGameInst', () => {
      it('should return NewGameInst for default GameInst initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createGameInstFormGroup(sampleWithNewData);

        const gameInst = service.getGameInst(formGroup) as any;

        expect(gameInst).toMatchObject(sampleWithNewData);
      });

      it('should return NewGameInst for empty GameInst initial value', () => {
        const formGroup = service.createGameInstFormGroup();

        const gameInst = service.getGameInst(formGroup) as any;

        expect(gameInst).toMatchObject({});
      });

      it('should return IGameInst', () => {
        const formGroup = service.createGameInstFormGroup(sampleWithRequiredData);

        const gameInst = service.getGameInst(formGroup) as any;

        expect(gameInst).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IGameInst should not enable id FormControl', () => {
        const formGroup = service.createGameInstFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewGameInst should disable id FormControl', () => {
        const formGroup = service.createGameInstFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});

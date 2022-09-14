import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../game-player.test-samples';

import { GamePlayerFormService } from './game-player-form.service';

describe('GamePlayer Form Service', () => {
  let service: GamePlayerFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GamePlayerFormService);
  });

  describe('Service methods', () => {
    describe('createGamePlayerFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createGamePlayerFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            version: expect.any(Object),
            role: expect.any(Object),
            ready: expect.any(Object),
            gameInst: expect.any(Object),
            player: expect.any(Object),
          })
        );
      });

      it('passing IGamePlayer should create a new form with FormGroup', () => {
        const formGroup = service.createGamePlayerFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            version: expect.any(Object),
            role: expect.any(Object),
            ready: expect.any(Object),
            gameInst: expect.any(Object),
            player: expect.any(Object),
          })
        );
      });
    });

    describe('getGamePlayer', () => {
      it('should return NewGamePlayer for default GamePlayer initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createGamePlayerFormGroup(sampleWithNewData);

        const gamePlayer = service.getGamePlayer(formGroup) as any;

        expect(gamePlayer).toMatchObject(sampleWithNewData);
      });

      it('should return NewGamePlayer for empty GamePlayer initial value', () => {
        const formGroup = service.createGamePlayerFormGroup();

        const gamePlayer = service.getGamePlayer(formGroup) as any;

        expect(gamePlayer).toMatchObject({});
      });

      it('should return IGamePlayer', () => {
        const formGroup = service.createGamePlayerFormGroup(sampleWithRequiredData);

        const gamePlayer = service.getGamePlayer(formGroup) as any;

        expect(gamePlayer).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IGamePlayer should not enable id FormControl', () => {
        const formGroup = service.createGamePlayerFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewGamePlayer should disable id FormControl', () => {
        const formGroup = service.createGamePlayerFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});

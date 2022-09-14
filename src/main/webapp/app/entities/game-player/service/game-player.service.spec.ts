import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IGamePlayer } from '../game-player.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../game-player.test-samples';

import { GamePlayerService } from './game-player.service';

const requireRestSample: IGamePlayer = {
  ...sampleWithRequiredData,
};

describe('GamePlayer Service', () => {
  let service: GamePlayerService;
  let httpMock: HttpTestingController;
  let expectedResult: IGamePlayer | IGamePlayer[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(GamePlayerService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a GamePlayer', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const gamePlayer = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(gamePlayer).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a GamePlayer', () => {
      const gamePlayer = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(gamePlayer).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a GamePlayer', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of GamePlayer', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a GamePlayer', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addGamePlayerToCollectionIfMissing', () => {
      it('should add a GamePlayer to an empty array', () => {
        const gamePlayer: IGamePlayer = sampleWithRequiredData;
        expectedResult = service.addGamePlayerToCollectionIfMissing([], gamePlayer);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(gamePlayer);
      });

      it('should not add a GamePlayer to an array that contains it', () => {
        const gamePlayer: IGamePlayer = sampleWithRequiredData;
        const gamePlayerCollection: IGamePlayer[] = [
          {
            ...gamePlayer,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addGamePlayerToCollectionIfMissing(gamePlayerCollection, gamePlayer);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a GamePlayer to an array that doesn't contain it", () => {
        const gamePlayer: IGamePlayer = sampleWithRequiredData;
        const gamePlayerCollection: IGamePlayer[] = [sampleWithPartialData];
        expectedResult = service.addGamePlayerToCollectionIfMissing(gamePlayerCollection, gamePlayer);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(gamePlayer);
      });

      it('should add only unique GamePlayer to an array', () => {
        const gamePlayerArray: IGamePlayer[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const gamePlayerCollection: IGamePlayer[] = [sampleWithRequiredData];
        expectedResult = service.addGamePlayerToCollectionIfMissing(gamePlayerCollection, ...gamePlayerArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const gamePlayer: IGamePlayer = sampleWithRequiredData;
        const gamePlayer2: IGamePlayer = sampleWithPartialData;
        expectedResult = service.addGamePlayerToCollectionIfMissing([], gamePlayer, gamePlayer2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(gamePlayer);
        expect(expectedResult).toContain(gamePlayer2);
      });

      it('should accept null and undefined values', () => {
        const gamePlayer: IGamePlayer = sampleWithRequiredData;
        expectedResult = service.addGamePlayerToCollectionIfMissing([], null, gamePlayer, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(gamePlayer);
      });

      it('should return initial array if no GamePlayer is added', () => {
        const gamePlayerCollection: IGamePlayer[] = [sampleWithRequiredData];
        expectedResult = service.addGamePlayerToCollectionIfMissing(gamePlayerCollection, undefined, null);
        expect(expectedResult).toEqual(gamePlayerCollection);
      });
    });

    describe('compareGamePlayer', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareGamePlayer(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareGamePlayer(entity1, entity2);
        const compareResult2 = service.compareGamePlayer(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareGamePlayer(entity1, entity2);
        const compareResult2 = service.compareGamePlayer(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareGamePlayer(entity1, entity2);
        const compareResult2 = service.compareGamePlayer(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

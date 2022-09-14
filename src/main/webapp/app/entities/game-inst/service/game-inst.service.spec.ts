import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IGameInst } from '../game-inst.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../game-inst.test-samples';

import { GameInstService, RestGameInst } from './game-inst.service';

const requireRestSample: RestGameInst = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.toJSON(),
  started: sampleWithRequiredData.started?.toJSON(),
  finished: sampleWithRequiredData.finished?.toJSON(),
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('GameInst Service', () => {
  let service: GameInstService;
  let httpMock: HttpTestingController;
  let expectedResult: IGameInst | IGameInst[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(GameInstService);
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

    it('should create a GameInst', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const gameInst = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(gameInst).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a GameInst', () => {
      const gameInst = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(gameInst).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a GameInst', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of GameInst', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a GameInst', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addGameInstToCollectionIfMissing', () => {
      it('should add a GameInst to an empty array', () => {
        const gameInst: IGameInst = sampleWithRequiredData;
        expectedResult = service.addGameInstToCollectionIfMissing([], gameInst);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(gameInst);
      });

      it('should not add a GameInst to an array that contains it', () => {
        const gameInst: IGameInst = sampleWithRequiredData;
        const gameInstCollection: IGameInst[] = [
          {
            ...gameInst,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addGameInstToCollectionIfMissing(gameInstCollection, gameInst);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a GameInst to an array that doesn't contain it", () => {
        const gameInst: IGameInst = sampleWithRequiredData;
        const gameInstCollection: IGameInst[] = [sampleWithPartialData];
        expectedResult = service.addGameInstToCollectionIfMissing(gameInstCollection, gameInst);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(gameInst);
      });

      it('should add only unique GameInst to an array', () => {
        const gameInstArray: IGameInst[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const gameInstCollection: IGameInst[] = [sampleWithRequiredData];
        expectedResult = service.addGameInstToCollectionIfMissing(gameInstCollection, ...gameInstArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const gameInst: IGameInst = sampleWithRequiredData;
        const gameInst2: IGameInst = sampleWithPartialData;
        expectedResult = service.addGameInstToCollectionIfMissing([], gameInst, gameInst2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(gameInst);
        expect(expectedResult).toContain(gameInst2);
      });

      it('should accept null and undefined values', () => {
        const gameInst: IGameInst = sampleWithRequiredData;
        expectedResult = service.addGameInstToCollectionIfMissing([], null, gameInst, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(gameInst);
      });

      it('should return initial array if no GameInst is added', () => {
        const gameInstCollection: IGameInst[] = [sampleWithRequiredData];
        expectedResult = service.addGameInstToCollectionIfMissing(gameInstCollection, undefined, null);
        expect(expectedResult).toEqual(gameInstCollection);
      });
    });

    describe('compareGameInst', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareGameInst(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareGameInst(entity1, entity2);
        const compareResult2 = service.compareGameInst(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareGameInst(entity1, entity2);
        const compareResult2 = service.compareGameInst(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareGameInst(entity1, entity2);
        const compareResult2 = service.compareGameInst(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

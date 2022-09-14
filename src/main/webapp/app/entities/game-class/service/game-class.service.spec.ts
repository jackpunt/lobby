import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IGameClass } from '../game-class.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../game-class.test-samples';

import { GameClassService, RestGameClass } from './game-class.service';

const requireRestSample: RestGameClass = {
  ...sampleWithRequiredData,
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('GameClass Service', () => {
  let service: GameClassService;
  let httpMock: HttpTestingController;
  let expectedResult: IGameClass | IGameClass[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(GameClassService);
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

    it('should create a GameClass', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const gameClass = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(gameClass).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a GameClass', () => {
      const gameClass = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(gameClass).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a GameClass', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of GameClass', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a GameClass', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addGameClassToCollectionIfMissing', () => {
      it('should add a GameClass to an empty array', () => {
        const gameClass: IGameClass = sampleWithRequiredData;
        expectedResult = service.addGameClassToCollectionIfMissing([], gameClass);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(gameClass);
      });

      it('should not add a GameClass to an array that contains it', () => {
        const gameClass: IGameClass = sampleWithRequiredData;
        const gameClassCollection: IGameClass[] = [
          {
            ...gameClass,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addGameClassToCollectionIfMissing(gameClassCollection, gameClass);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a GameClass to an array that doesn't contain it", () => {
        const gameClass: IGameClass = sampleWithRequiredData;
        const gameClassCollection: IGameClass[] = [sampleWithPartialData];
        expectedResult = service.addGameClassToCollectionIfMissing(gameClassCollection, gameClass);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(gameClass);
      });

      it('should add only unique GameClass to an array', () => {
        const gameClassArray: IGameClass[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const gameClassCollection: IGameClass[] = [sampleWithRequiredData];
        expectedResult = service.addGameClassToCollectionIfMissing(gameClassCollection, ...gameClassArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const gameClass: IGameClass = sampleWithRequiredData;
        const gameClass2: IGameClass = sampleWithPartialData;
        expectedResult = service.addGameClassToCollectionIfMissing([], gameClass, gameClass2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(gameClass);
        expect(expectedResult).toContain(gameClass2);
      });

      it('should accept null and undefined values', () => {
        const gameClass: IGameClass = sampleWithRequiredData;
        expectedResult = service.addGameClassToCollectionIfMissing([], null, gameClass, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(gameClass);
      });

      it('should return initial array if no GameClass is added', () => {
        const gameClassCollection: IGameClass[] = [sampleWithRequiredData];
        expectedResult = service.addGameClassToCollectionIfMissing(gameClassCollection, undefined, null);
        expect(expectedResult).toEqual(gameClassCollection);
      });
    });

    describe('compareGameClass', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareGameClass(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareGameClass(entity1, entity2);
        const compareResult2 = service.compareGameClass(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareGameClass(entity1, entity2);
        const compareResult2 = service.compareGameClass(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareGameClass(entity1, entity2);
        const compareResult2 = service.compareGameClass(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

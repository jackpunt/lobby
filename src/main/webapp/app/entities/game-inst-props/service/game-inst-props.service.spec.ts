import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IGameInstProps } from '../game-inst-props.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../game-inst-props.test-samples';

import { GameInstPropsService, RestGameInstProps } from './game-inst-props.service';

const requireRestSample: RestGameInstProps = {
  ...sampleWithRequiredData,
  updated: sampleWithRequiredData.updated?.toJSON(),
};

describe('GameInstProps Service', () => {
  let service: GameInstPropsService;
  let httpMock: HttpTestingController;
  let expectedResult: IGameInstProps | IGameInstProps[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(GameInstPropsService);
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

    it('should create a GameInstProps', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const gameInstProps = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(gameInstProps).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a GameInstProps', () => {
      const gameInstProps = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(gameInstProps).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a GameInstProps', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of GameInstProps', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a GameInstProps', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addGameInstPropsToCollectionIfMissing', () => {
      it('should add a GameInstProps to an empty array', () => {
        const gameInstProps: IGameInstProps = sampleWithRequiredData;
        expectedResult = service.addGameInstPropsToCollectionIfMissing([], gameInstProps);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(gameInstProps);
      });

      it('should not add a GameInstProps to an array that contains it', () => {
        const gameInstProps: IGameInstProps = sampleWithRequiredData;
        const gameInstPropsCollection: IGameInstProps[] = [
          {
            ...gameInstProps,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addGameInstPropsToCollectionIfMissing(gameInstPropsCollection, gameInstProps);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a GameInstProps to an array that doesn't contain it", () => {
        const gameInstProps: IGameInstProps = sampleWithRequiredData;
        const gameInstPropsCollection: IGameInstProps[] = [sampleWithPartialData];
        expectedResult = service.addGameInstPropsToCollectionIfMissing(gameInstPropsCollection, gameInstProps);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(gameInstProps);
      });

      it('should add only unique GameInstProps to an array', () => {
        const gameInstPropsArray: IGameInstProps[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const gameInstPropsCollection: IGameInstProps[] = [sampleWithRequiredData];
        expectedResult = service.addGameInstPropsToCollectionIfMissing(gameInstPropsCollection, ...gameInstPropsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const gameInstProps: IGameInstProps = sampleWithRequiredData;
        const gameInstProps2: IGameInstProps = sampleWithPartialData;
        expectedResult = service.addGameInstPropsToCollectionIfMissing([], gameInstProps, gameInstProps2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(gameInstProps);
        expect(expectedResult).toContain(gameInstProps2);
      });

      it('should accept null and undefined values', () => {
        const gameInstProps: IGameInstProps = sampleWithRequiredData;
        expectedResult = service.addGameInstPropsToCollectionIfMissing([], null, gameInstProps, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(gameInstProps);
      });

      it('should return initial array if no GameInstProps is added', () => {
        const gameInstPropsCollection: IGameInstProps[] = [sampleWithRequiredData];
        expectedResult = service.addGameInstPropsToCollectionIfMissing(gameInstPropsCollection, undefined, null);
        expect(expectedResult).toEqual(gameInstPropsCollection);
      });
    });

    describe('compareGameInstProps', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareGameInstProps(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareGameInstProps(entity1, entity2);
        const compareResult2 = service.compareGameInstProps(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareGameInstProps(entity1, entity2);
        const compareResult2 = service.compareGameInstProps(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareGameInstProps(entity1, entity2);
        const compareResult2 = service.compareGameInstProps(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

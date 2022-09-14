import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMemberGameProps } from '../member-game-props.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../member-game-props.test-samples';

import { MemberGamePropsService } from './member-game-props.service';

const requireRestSample: IMemberGameProps = {
  ...sampleWithRequiredData,
};

describe('MemberGameProps Service', () => {
  let service: MemberGamePropsService;
  let httpMock: HttpTestingController;
  let expectedResult: IMemberGameProps | IMemberGameProps[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MemberGamePropsService);
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

    it('should create a MemberGameProps', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const memberGameProps = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(memberGameProps).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MemberGameProps', () => {
      const memberGameProps = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(memberGameProps).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MemberGameProps', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MemberGameProps', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MemberGameProps', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addMemberGamePropsToCollectionIfMissing', () => {
      it('should add a MemberGameProps to an empty array', () => {
        const memberGameProps: IMemberGameProps = sampleWithRequiredData;
        expectedResult = service.addMemberGamePropsToCollectionIfMissing([], memberGameProps);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(memberGameProps);
      });

      it('should not add a MemberGameProps to an array that contains it', () => {
        const memberGameProps: IMemberGameProps = sampleWithRequiredData;
        const memberGamePropsCollection: IMemberGameProps[] = [
          {
            ...memberGameProps,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMemberGamePropsToCollectionIfMissing(memberGamePropsCollection, memberGameProps);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MemberGameProps to an array that doesn't contain it", () => {
        const memberGameProps: IMemberGameProps = sampleWithRequiredData;
        const memberGamePropsCollection: IMemberGameProps[] = [sampleWithPartialData];
        expectedResult = service.addMemberGamePropsToCollectionIfMissing(memberGamePropsCollection, memberGameProps);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(memberGameProps);
      });

      it('should add only unique MemberGameProps to an array', () => {
        const memberGamePropsArray: IMemberGameProps[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const memberGamePropsCollection: IMemberGameProps[] = [sampleWithRequiredData];
        expectedResult = service.addMemberGamePropsToCollectionIfMissing(memberGamePropsCollection, ...memberGamePropsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const memberGameProps: IMemberGameProps = sampleWithRequiredData;
        const memberGameProps2: IMemberGameProps = sampleWithPartialData;
        expectedResult = service.addMemberGamePropsToCollectionIfMissing([], memberGameProps, memberGameProps2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(memberGameProps);
        expect(expectedResult).toContain(memberGameProps2);
      });

      it('should accept null and undefined values', () => {
        const memberGameProps: IMemberGameProps = sampleWithRequiredData;
        expectedResult = service.addMemberGamePropsToCollectionIfMissing([], null, memberGameProps, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(memberGameProps);
      });

      it('should return initial array if no MemberGameProps is added', () => {
        const memberGamePropsCollection: IMemberGameProps[] = [sampleWithRequiredData];
        expectedResult = service.addMemberGamePropsToCollectionIfMissing(memberGamePropsCollection, undefined, null);
        expect(expectedResult).toEqual(memberGamePropsCollection);
      });
    });

    describe('compareMemberGameProps', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMemberGameProps(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareMemberGameProps(entity1, entity2);
        const compareResult2 = service.compareMemberGameProps(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareMemberGameProps(entity1, entity2);
        const compareResult2 = service.compareMemberGameProps(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareMemberGameProps(entity1, entity2);
        const compareResult2 = service.compareMemberGameProps(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AccountInfoFormService } from './account-info-form.service';
import { AccountInfoService } from '../service/account-info.service';
import { IAccountInfo } from '../account-info.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { AccountInfoUpdateComponent } from './account-info-update.component';

describe('AccountInfo Management Update Component', () => {
  let comp: AccountInfoUpdateComponent;
  let fixture: ComponentFixture<AccountInfoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let accountInfoFormService: AccountInfoFormService;
  let accountInfoService: AccountInfoService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AccountInfoUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(AccountInfoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AccountInfoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    accountInfoFormService = TestBed.inject(AccountInfoFormService);
    accountInfoService = TestBed.inject(AccountInfoService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const accountInfo: IAccountInfo = { id: 456 };
      const user: IUser = { id: 69265 };
      accountInfo.user = user;

      const userCollection: IUser[] = [{ id: 13482 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ accountInfo });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const accountInfo: IAccountInfo = { id: 456 };
      const user: IUser = { id: 73058 };
      accountInfo.user = user;

      activatedRoute.data = of({ accountInfo });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.accountInfo).toEqual(accountInfo);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAccountInfo>>();
      const accountInfo = { id: 123 };
      jest.spyOn(accountInfoFormService, 'getAccountInfo').mockReturnValue(accountInfo);
      jest.spyOn(accountInfoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ accountInfo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: accountInfo }));
      saveSubject.complete();

      // THEN
      expect(accountInfoFormService.getAccountInfo).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(accountInfoService.update).toHaveBeenCalledWith(expect.objectContaining(accountInfo));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAccountInfo>>();
      const accountInfo = { id: 123 };
      jest.spyOn(accountInfoFormService, 'getAccountInfo').mockReturnValue({ id: null });
      jest.spyOn(accountInfoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ accountInfo: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: accountInfo }));
      saveSubject.complete();

      // THEN
      expect(accountInfoFormService.getAccountInfo).toHaveBeenCalled();
      expect(accountInfoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAccountInfo>>();
      const accountInfo = { id: 123 };
      jest.spyOn(accountInfoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ accountInfo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(accountInfoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});

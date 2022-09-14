import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MemberGamePropsFormService } from './member-game-props-form.service';
import { MemberGamePropsService } from '../service/member-game-props.service';
import { IMemberGameProps } from '../member-game-props.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IGameClass } from 'app/entities/game-class/game-class.model';
import { GameClassService } from 'app/entities/game-class/service/game-class.service';

import { MemberGamePropsUpdateComponent } from './member-game-props-update.component';

describe('MemberGameProps Management Update Component', () => {
  let comp: MemberGamePropsUpdateComponent;
  let fixture: ComponentFixture<MemberGamePropsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let memberGamePropsFormService: MemberGamePropsFormService;
  let memberGamePropsService: MemberGamePropsService;
  let userService: UserService;
  let gameClassService: GameClassService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MemberGamePropsUpdateComponent],
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
      .overrideTemplate(MemberGamePropsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MemberGamePropsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    memberGamePropsFormService = TestBed.inject(MemberGamePropsFormService);
    memberGamePropsService = TestBed.inject(MemberGamePropsService);
    userService = TestBed.inject(UserService);
    gameClassService = TestBed.inject(GameClassService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const memberGameProps: IMemberGameProps = { id: 456 };
      const user: IUser = { id: 49892 };
      memberGameProps.user = user;

      const userCollection: IUser[] = [{ id: 60530 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ memberGameProps });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call GameClass query and add missing value', () => {
      const memberGameProps: IMemberGameProps = { id: 456 };
      const gameClass: IGameClass = { id: 54933 };
      memberGameProps.gameClass = gameClass;

      const gameClassCollection: IGameClass[] = [{ id: 60527 }];
      jest.spyOn(gameClassService, 'query').mockReturnValue(of(new HttpResponse({ body: gameClassCollection })));
      const additionalGameClasses = [gameClass];
      const expectedCollection: IGameClass[] = [...additionalGameClasses, ...gameClassCollection];
      jest.spyOn(gameClassService, 'addGameClassToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ memberGameProps });
      comp.ngOnInit();

      expect(gameClassService.query).toHaveBeenCalled();
      expect(gameClassService.addGameClassToCollectionIfMissing).toHaveBeenCalledWith(
        gameClassCollection,
        ...additionalGameClasses.map(expect.objectContaining)
      );
      expect(comp.gameClassesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const memberGameProps: IMemberGameProps = { id: 456 };
      const user: IUser = { id: 28377 };
      memberGameProps.user = user;
      const gameClass: IGameClass = { id: 87177 };
      memberGameProps.gameClass = gameClass;

      activatedRoute.data = of({ memberGameProps });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.gameClassesSharedCollection).toContain(gameClass);
      expect(comp.memberGameProps).toEqual(memberGameProps);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMemberGameProps>>();
      const memberGameProps = { id: 123 };
      jest.spyOn(memberGamePropsFormService, 'getMemberGameProps').mockReturnValue(memberGameProps);
      jest.spyOn(memberGamePropsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ memberGameProps });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: memberGameProps }));
      saveSubject.complete();

      // THEN
      expect(memberGamePropsFormService.getMemberGameProps).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(memberGamePropsService.update).toHaveBeenCalledWith(expect.objectContaining(memberGameProps));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMemberGameProps>>();
      const memberGameProps = { id: 123 };
      jest.spyOn(memberGamePropsFormService, 'getMemberGameProps').mockReturnValue({ id: null });
      jest.spyOn(memberGamePropsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ memberGameProps: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: memberGameProps }));
      saveSubject.complete();

      // THEN
      expect(memberGamePropsFormService.getMemberGameProps).toHaveBeenCalled();
      expect(memberGamePropsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMemberGameProps>>();
      const memberGameProps = { id: 123 };
      jest.spyOn(memberGamePropsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ memberGameProps });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(memberGamePropsService.update).toHaveBeenCalled();
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

    describe('compareGameClass', () => {
      it('Should forward to gameClassService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(gameClassService, 'compareGameClass');
        comp.compareGameClass(entity, entity2);
        expect(gameClassService.compareGameClass).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});

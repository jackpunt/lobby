import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MemberGamePropsDetailComponent } from './member-game-props-detail.component';

describe('MemberGameProps Management Detail Component', () => {
  let comp: MemberGamePropsDetailComponent;
  let fixture: ComponentFixture<MemberGamePropsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MemberGamePropsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ memberGameProps: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(MemberGamePropsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(MemberGamePropsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load memberGameProps on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.memberGameProps).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});

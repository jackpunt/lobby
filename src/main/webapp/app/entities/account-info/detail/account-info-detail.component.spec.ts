import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AccountInfoDetailComponent } from './account-info-detail.component';

describe('AccountInfo Management Detail Component', () => {
  let comp: AccountInfoDetailComponent;
  let fixture: ComponentFixture<AccountInfoDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AccountInfoDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ accountInfo: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(AccountInfoDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(AccountInfoDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load accountInfo on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.accountInfo).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAccountInfo } from '../account-info.model';

@Component({
  selector: 'jhi-account-info-detail',
  templateUrl: './account-info-detail.component.html',
})
export class AccountInfoDetailComponent implements OnInit {
  accountInfo: IAccountInfo | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ accountInfo }) => {
      this.accountInfo = accountInfo;
    });
  }

  previousState(): void {
    window.history.back();
  }
}

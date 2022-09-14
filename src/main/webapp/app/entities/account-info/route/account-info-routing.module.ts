import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AccountInfoComponent } from '../list/account-info.component';
import { AccountInfoDetailComponent } from '../detail/account-info-detail.component';
import { AccountInfoUpdateComponent } from '../update/account-info-update.component';
import { AccountInfoRoutingResolveService } from './account-info-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const accountInfoRoute: Routes = [
  {
    path: '',
    component: AccountInfoComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AccountInfoDetailComponent,
    resolve: {
      accountInfo: AccountInfoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AccountInfoUpdateComponent,
    resolve: {
      accountInfo: AccountInfoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AccountInfoUpdateComponent,
    resolve: {
      accountInfo: AccountInfoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(accountInfoRoute)],
  exports: [RouterModule],
})
export class AccountInfoRoutingModule {}

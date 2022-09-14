import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MemberGamePropsComponent } from '../list/member-game-props.component';
import { MemberGamePropsDetailComponent } from '../detail/member-game-props-detail.component';
import { MemberGamePropsUpdateComponent } from '../update/member-game-props-update.component';
import { MemberGamePropsRoutingResolveService } from './member-game-props-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const memberGamePropsRoute: Routes = [
  {
    path: '',
    component: MemberGamePropsComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MemberGamePropsDetailComponent,
    resolve: {
      memberGameProps: MemberGamePropsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MemberGamePropsUpdateComponent,
    resolve: {
      memberGameProps: MemberGamePropsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MemberGamePropsUpdateComponent,
    resolve: {
      memberGameProps: MemberGamePropsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(memberGamePropsRoute)],
  exports: [RouterModule],
})
export class MemberGamePropsRoutingModule {}

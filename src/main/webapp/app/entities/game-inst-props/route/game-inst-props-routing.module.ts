import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { GameInstPropsComponent } from '../list/game-inst-props.component';
import { GameInstPropsDetailComponent } from '../detail/game-inst-props-detail.component';
import { GameInstPropsUpdateComponent } from '../update/game-inst-props-update.component';
import { GameInstPropsRoutingResolveService } from './game-inst-props-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const gameInstPropsRoute: Routes = [
  {
    path: '',
    component: GameInstPropsComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GameInstPropsDetailComponent,
    resolve: {
      gameInstProps: GameInstPropsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GameInstPropsUpdateComponent,
    resolve: {
      gameInstProps: GameInstPropsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GameInstPropsUpdateComponent,
    resolve: {
      gameInstProps: GameInstPropsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(gameInstPropsRoute)],
  exports: [RouterModule],
})
export class GameInstPropsRoutingModule {}

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { GameInstComponent } from '../list/game-inst.component';
import { GameInstDetailComponent } from '../detail/game-inst-detail.component';
import { GameInstUpdateComponent } from '../update/game-inst-update.component';
import { GameInstRoutingResolveService } from './game-inst-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const gameInstRoute: Routes = [
  {
    path: '',
    component: GameInstComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GameInstDetailComponent,
    resolve: {
      gameInst: GameInstRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GameInstUpdateComponent,
    resolve: {
      gameInst: GameInstRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GameInstUpdateComponent,
    resolve: {
      gameInst: GameInstRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(gameInstRoute)],
  exports: [RouterModule],
})
export class GameInstRoutingModule {}

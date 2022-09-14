import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { GameClassComponent } from '../list/game-class.component';
import { GameClassDetailComponent } from '../detail/game-class-detail.component';
import { GameClassUpdateComponent } from '../update/game-class-update.component';
import { GameClassRoutingResolveService } from './game-class-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const gameClassRoute: Routes = [
  {
    path: '',
    component: GameClassComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GameClassDetailComponent,
    resolve: {
      gameClass: GameClassRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GameClassUpdateComponent,
    resolve: {
      gameClass: GameClassRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GameClassUpdateComponent,
    resolve: {
      gameClass: GameClassRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(gameClassRoute)],
  exports: [RouterModule],
})
export class GameClassRoutingModule {}

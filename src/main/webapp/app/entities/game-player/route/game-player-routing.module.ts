import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { GamePlayerComponent } from '../list/game-player.component';
import { GamePlayerDetailComponent } from '../detail/game-player-detail.component';
import { GamePlayerUpdateComponent } from '../update/game-player-update.component';
import { GamePlayerRoutingResolveService } from './game-player-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const gamePlayerRoute: Routes = [
  {
    path: '',
    component: GamePlayerComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GamePlayerDetailComponent,
    resolve: {
      gamePlayer: GamePlayerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GamePlayerUpdateComponent,
    resolve: {
      gamePlayer: GamePlayerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GamePlayerUpdateComponent,
    resolve: {
      gamePlayer: GamePlayerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(gamePlayerRoute)],
  exports: [RouterModule],
})
export class GamePlayerRoutingModule {}

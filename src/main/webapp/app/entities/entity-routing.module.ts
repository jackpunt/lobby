import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'account-info',
        data: { pageTitle: 'lobbyApp.accountInfo.home.title' },
        loadChildren: () => import('./account-info/account-info.module').then(m => m.AccountInfoModule),
      },
      {
        path: 'asset',
        data: { pageTitle: 'lobbyApp.asset.home.title' },
        loadChildren: () => import('./asset/asset.module').then(m => m.AssetModule),
      },
      {
        path: 'game-class',
        data: { pageTitle: 'lobbyApp.gameClass.home.title' },
        loadChildren: () => import('./game-class/game-class.module').then(m => m.GameClassModule),
      },
      {
        path: 'game-inst',
        data: { pageTitle: 'lobbyApp.gameInst.home.title' },
        loadChildren: () => import('./game-inst/game-inst.module').then(m => m.GameInstModule),
      },
      {
        path: 'game-inst-props',
        data: { pageTitle: 'lobbyApp.gameInstProps.home.title' },
        loadChildren: () => import('./game-inst-props/game-inst-props.module').then(m => m.GameInstPropsModule),
      },
      {
        path: 'game-player',
        data: { pageTitle: 'lobbyApp.gamePlayer.home.title' },
        loadChildren: () => import('./game-player/game-player.module').then(m => m.GamePlayerModule),
      },
      {
        path: 'member-game-props',
        data: { pageTitle: 'lobbyApp.memberGameProps.home.title' },
        loadChildren: () => import('./member-game-props/member-game-props.module').then(m => m.MemberGamePropsModule),
      },
      {
        path: 'player',
        data: { pageTitle: 'lobbyApp.player.home.title' },
        loadChildren: () => import('./player/player.module').then(m => m.PlayerModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}

import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { GamePlayerComponent } from './list/game-player.component';
import { GamePlayerDetailComponent } from './detail/game-player-detail.component';
import { GamePlayerUpdateComponent } from './update/game-player-update.component';
import { GamePlayerDeleteDialogComponent } from './delete/game-player-delete-dialog.component';
import { GamePlayerRoutingModule } from './route/game-player-routing.module';

@NgModule({
  imports: [SharedModule, GamePlayerRoutingModule],
  declarations: [GamePlayerComponent, GamePlayerDetailComponent, GamePlayerUpdateComponent, GamePlayerDeleteDialogComponent],
})
export class GamePlayerModule {}

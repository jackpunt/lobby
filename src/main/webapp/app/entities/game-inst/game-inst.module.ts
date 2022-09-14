import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { GameInstComponent } from './list/game-inst.component';
import { GameInstDetailComponent } from './detail/game-inst-detail.component';
import { GameInstUpdateComponent } from './update/game-inst-update.component';
import { GameInstDeleteDialogComponent } from './delete/game-inst-delete-dialog.component';
import { GameInstRoutingModule } from './route/game-inst-routing.module';

@NgModule({
  imports: [SharedModule, GameInstRoutingModule],
  declarations: [GameInstComponent, GameInstDetailComponent, GameInstUpdateComponent, GameInstDeleteDialogComponent],
})
export class GameInstModule {}

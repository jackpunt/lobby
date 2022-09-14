import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { GameClassComponent } from './list/game-class.component';
import { GameClassDetailComponent } from './detail/game-class-detail.component';
import { GameClassUpdateComponent } from './update/game-class-update.component';
import { GameClassDeleteDialogComponent } from './delete/game-class-delete-dialog.component';
import { GameClassRoutingModule } from './route/game-class-routing.module';

@NgModule({
  imports: [SharedModule, GameClassRoutingModule],
  declarations: [GameClassComponent, GameClassDetailComponent, GameClassUpdateComponent, GameClassDeleteDialogComponent],
})
export class GameClassModule {}

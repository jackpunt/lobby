import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { GameInstPropsComponent } from './list/game-inst-props.component';
import { GameInstPropsDetailComponent } from './detail/game-inst-props-detail.component';
import { GameInstPropsUpdateComponent } from './update/game-inst-props-update.component';
import { GameInstPropsDeleteDialogComponent } from './delete/game-inst-props-delete-dialog.component';
import { GameInstPropsRoutingModule } from './route/game-inst-props-routing.module';

@NgModule({
  imports: [SharedModule, GameInstPropsRoutingModule],
  declarations: [GameInstPropsComponent, GameInstPropsDetailComponent, GameInstPropsUpdateComponent, GameInstPropsDeleteDialogComponent],
})
export class GameInstPropsModule {}

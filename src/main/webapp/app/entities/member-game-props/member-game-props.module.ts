import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { MemberGamePropsComponent } from './list/member-game-props.component';
import { MemberGamePropsDetailComponent } from './detail/member-game-props-detail.component';
import { MemberGamePropsUpdateComponent } from './update/member-game-props-update.component';
import { MemberGamePropsDeleteDialogComponent } from './delete/member-game-props-delete-dialog.component';
import { MemberGamePropsRoutingModule } from './route/member-game-props-routing.module';

@NgModule({
  imports: [SharedModule, MemberGamePropsRoutingModule],
  declarations: [
    MemberGamePropsComponent,
    MemberGamePropsDetailComponent,
    MemberGamePropsUpdateComponent,
    MemberGamePropsDeleteDialogComponent,
  ],
})
export class MemberGamePropsModule {}

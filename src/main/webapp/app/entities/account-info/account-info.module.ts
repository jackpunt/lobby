import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AccountInfoComponent } from './list/account-info.component';
import { AccountInfoDetailComponent } from './detail/account-info-detail.component';
import { AccountInfoUpdateComponent } from './update/account-info-update.component';
import { AccountInfoDeleteDialogComponent } from './delete/account-info-delete-dialog.component';
import { AccountInfoRoutingModule } from './route/account-info-routing.module';

@NgModule({
  imports: [SharedModule, AccountInfoRoutingModule],
  declarations: [AccountInfoComponent, AccountInfoDetailComponent, AccountInfoUpdateComponent, AccountInfoDeleteDialogComponent],
})
export class AccountInfoModule {}

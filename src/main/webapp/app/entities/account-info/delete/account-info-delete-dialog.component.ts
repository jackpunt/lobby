import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAccountInfo } from '../account-info.model';
import { AccountInfoService } from '../service/account-info.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './account-info-delete-dialog.component.html',
})
export class AccountInfoDeleteDialogComponent {
  accountInfo?: IAccountInfo;

  constructor(protected accountInfoService: AccountInfoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.accountInfoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}

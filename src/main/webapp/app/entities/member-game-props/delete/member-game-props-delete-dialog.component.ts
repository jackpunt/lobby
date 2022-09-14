import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IMemberGameProps } from '../member-game-props.model';
import { MemberGamePropsService } from '../service/member-game-props.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './member-game-props-delete-dialog.component.html',
})
export class MemberGamePropsDeleteDialogComponent {
  memberGameProps?: IMemberGameProps;

  constructor(protected memberGamePropsService: MemberGamePropsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.memberGamePropsService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}

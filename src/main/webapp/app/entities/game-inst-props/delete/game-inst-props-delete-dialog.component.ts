import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IGameInstProps } from '../game-inst-props.model';
import { GameInstPropsService } from '../service/game-inst-props.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './game-inst-props-delete-dialog.component.html',
})
export class GameInstPropsDeleteDialogComponent {
  gameInstProps?: IGameInstProps;

  constructor(protected gameInstPropsService: GameInstPropsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.gameInstPropsService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}

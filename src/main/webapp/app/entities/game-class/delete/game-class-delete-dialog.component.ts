import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IGameClass } from '../game-class.model';
import { GameClassService } from '../service/game-class.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './game-class-delete-dialog.component.html',
})
export class GameClassDeleteDialogComponent {
  gameClass?: IGameClass;

  constructor(protected gameClassService: GameClassService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.gameClassService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}

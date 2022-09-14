import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IGameInst } from '../game-inst.model';
import { GameInstService } from '../service/game-inst.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './game-inst-delete-dialog.component.html',
})
export class GameInstDeleteDialogComponent {
  gameInst?: IGameInst;

  constructor(protected gameInstService: GameInstService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.gameInstService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}

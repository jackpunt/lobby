import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IGamePlayer } from '../game-player.model';
import { GamePlayerService } from '../service/game-player.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './game-player-delete-dialog.component.html',
})
export class GamePlayerDeleteDialogComponent {
  gamePlayer?: IGamePlayer;

  constructor(protected gamePlayerService: GamePlayerService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.gamePlayerService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}

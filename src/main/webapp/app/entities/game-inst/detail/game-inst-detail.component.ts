import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IGameInst } from '../game-inst.model';

@Component({
  selector: 'jhi-game-inst-detail',
  templateUrl: './game-inst-detail.component.html',
})
export class GameInstDetailComponent implements OnInit {
  gameInst: IGameInst | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ gameInst }) => {
      this.gameInst = gameInst;
    });
  }

  previousState(): void {
    window.history.back();
  }
}

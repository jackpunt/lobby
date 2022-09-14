import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IGamePlayer } from '../game-player.model';

@Component({
  selector: 'jhi-game-player-detail',
  templateUrl: './game-player-detail.component.html',
})
export class GamePlayerDetailComponent implements OnInit {
  gamePlayer: IGamePlayer | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ gamePlayer }) => {
      this.gamePlayer = gamePlayer;
    });
  }

  previousState(): void {
    window.history.back();
  }
}

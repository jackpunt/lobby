import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IGameClass } from '../game-class.model';

@Component({
  selector: 'jhi-game-class-detail',
  templateUrl: './game-class-detail.component.html',
})
export class GameClassDetailComponent implements OnInit {
  gameClass: IGameClass | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ gameClass }) => {
      this.gameClass = gameClass;
    });
  }

  previousState(): void {
    window.history.back();
  }
}

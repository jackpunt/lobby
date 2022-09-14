import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IGameInstProps } from '../game-inst-props.model';

@Component({
  selector: 'jhi-game-inst-props-detail',
  templateUrl: './game-inst-props-detail.component.html',
})
export class GameInstPropsDetailComponent implements OnInit {
  gameInstProps: IGameInstProps | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ gameInstProps }) => {
      this.gameInstProps = gameInstProps;
    });
  }

  previousState(): void {
    window.history.back();
  }
}

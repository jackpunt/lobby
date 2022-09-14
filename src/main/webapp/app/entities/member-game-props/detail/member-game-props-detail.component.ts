import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMemberGameProps } from '../member-game-props.model';

@Component({
  selector: 'jhi-member-game-props-detail',
  templateUrl: './member-game-props-detail.component.html',
})
export class MemberGamePropsDetailComponent implements OnInit {
  memberGameProps: IMemberGameProps | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ memberGameProps }) => {
      this.memberGameProps = memberGameProps;
    });
  }

  previousState(): void {
    window.history.back();
  }
}

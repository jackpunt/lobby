import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGamePlayer } from '../game-player.model';
import { GamePlayerService } from '../service/game-player.service';

@Injectable({ providedIn: 'root' })
export class GamePlayerRoutingResolveService implements Resolve<IGamePlayer | null> {
  constructor(protected service: GamePlayerService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGamePlayer | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((gamePlayer: HttpResponse<IGamePlayer>) => {
          if (gamePlayer.body) {
            return of(gamePlayer.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}

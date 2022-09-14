import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGameClass } from '../game-class.model';
import { GameClassService } from '../service/game-class.service';

@Injectable({ providedIn: 'root' })
export class GameClassRoutingResolveService implements Resolve<IGameClass | null> {
  constructor(protected service: GameClassService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGameClass | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((gameClass: HttpResponse<IGameClass>) => {
          if (gameClass.body) {
            return of(gameClass.body);
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

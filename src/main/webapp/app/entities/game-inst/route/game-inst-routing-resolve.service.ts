import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGameInst } from '../game-inst.model';
import { GameInstService } from '../service/game-inst.service';

@Injectable({ providedIn: 'root' })
export class GameInstRoutingResolveService implements Resolve<IGameInst | null> {
  constructor(protected service: GameInstService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGameInst | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((gameInst: HttpResponse<IGameInst>) => {
          if (gameInst.body) {
            return of(gameInst.body);
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

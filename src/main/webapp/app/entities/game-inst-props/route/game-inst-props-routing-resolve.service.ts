import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGameInstProps } from '../game-inst-props.model';
import { GameInstPropsService } from '../service/game-inst-props.service';

@Injectable({ providedIn: 'root' })
export class GameInstPropsRoutingResolveService implements Resolve<IGameInstProps | null> {
  constructor(protected service: GameInstPropsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGameInstProps | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((gameInstProps: HttpResponse<IGameInstProps>) => {
          if (gameInstProps.body) {
            return of(gameInstProps.body);
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

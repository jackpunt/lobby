import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMemberGameProps } from '../member-game-props.model';
import { MemberGamePropsService } from '../service/member-game-props.service';

@Injectable({ providedIn: 'root' })
export class MemberGamePropsRoutingResolveService implements Resolve<IMemberGameProps | null> {
  constructor(protected service: MemberGamePropsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMemberGameProps | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((memberGameProps: HttpResponse<IMemberGameProps>) => {
          if (memberGameProps.body) {
            return of(memberGameProps.body);
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

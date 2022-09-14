import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAccountInfo } from '../account-info.model';
import { AccountInfoService } from '../service/account-info.service';

@Injectable({ providedIn: 'root' })
export class AccountInfoRoutingResolveService implements Resolve<IAccountInfo | null> {
  constructor(protected service: AccountInfoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAccountInfo | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((accountInfo: HttpResponse<IAccountInfo>) => {
          if (accountInfo.body) {
            return of(accountInfo.body);
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

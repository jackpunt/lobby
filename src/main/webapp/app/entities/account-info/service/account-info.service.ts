import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAccountInfo, NewAccountInfo } from '../account-info.model';

export type PartialUpdateAccountInfo = Partial<IAccountInfo> & Pick<IAccountInfo, 'id'>;

export type EntityResponseType = HttpResponse<IAccountInfo>;
export type EntityArrayResponseType = HttpResponse<IAccountInfo[]>;

@Injectable({ providedIn: 'root' })
export class AccountInfoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/account-infos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(accountInfo: NewAccountInfo): Observable<EntityResponseType> {
    return this.http.post<IAccountInfo>(this.resourceUrl, accountInfo, { observe: 'response' });
  }

  update(accountInfo: IAccountInfo): Observable<EntityResponseType> {
    return this.http.put<IAccountInfo>(`${this.resourceUrl}/${this.getAccountInfoIdentifier(accountInfo)}`, accountInfo, {
      observe: 'response',
    });
  }

  partialUpdate(accountInfo: PartialUpdateAccountInfo): Observable<EntityResponseType> {
    return this.http.patch<IAccountInfo>(`${this.resourceUrl}/${this.getAccountInfoIdentifier(accountInfo)}`, accountInfo, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAccountInfo>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAccountInfo[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAccountInfoIdentifier(accountInfo: Pick<IAccountInfo, 'id'>): number {
    return accountInfo.id;
  }

  compareAccountInfo(o1: Pick<IAccountInfo, 'id'> | null, o2: Pick<IAccountInfo, 'id'> | null): boolean {
    return o1 && o2 ? this.getAccountInfoIdentifier(o1) === this.getAccountInfoIdentifier(o2) : o1 === o2;
  }

  addAccountInfoToCollectionIfMissing<Type extends Pick<IAccountInfo, 'id'>>(
    accountInfoCollection: Type[],
    ...accountInfosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const accountInfos: Type[] = accountInfosToCheck.filter(isPresent);
    if (accountInfos.length > 0) {
      const accountInfoCollectionIdentifiers = accountInfoCollection.map(
        accountInfoItem => this.getAccountInfoIdentifier(accountInfoItem)!
      );
      const accountInfosToAdd = accountInfos.filter(accountInfoItem => {
        const accountInfoIdentifier = this.getAccountInfoIdentifier(accountInfoItem);
        if (accountInfoCollectionIdentifiers.includes(accountInfoIdentifier)) {
          return false;
        }
        accountInfoCollectionIdentifiers.push(accountInfoIdentifier);
        return true;
      });
      return [...accountInfosToAdd, ...accountInfoCollection];
    }
    return accountInfoCollection;
  }
}

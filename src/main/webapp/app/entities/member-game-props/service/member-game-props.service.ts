import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMemberGameProps, NewMemberGameProps } from '../member-game-props.model';

export type PartialUpdateMemberGameProps = Partial<IMemberGameProps> & Pick<IMemberGameProps, 'id'>;

export type EntityResponseType = HttpResponse<IMemberGameProps>;
export type EntityArrayResponseType = HttpResponse<IMemberGameProps[]>;

@Injectable({ providedIn: 'root' })
export class MemberGamePropsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/member-game-props');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(memberGameProps: NewMemberGameProps): Observable<EntityResponseType> {
    return this.http.post<IMemberGameProps>(this.resourceUrl, memberGameProps, { observe: 'response' });
  }

  update(memberGameProps: IMemberGameProps): Observable<EntityResponseType> {
    return this.http.put<IMemberGameProps>(`${this.resourceUrl}/${this.getMemberGamePropsIdentifier(memberGameProps)}`, memberGameProps, {
      observe: 'response',
    });
  }

  partialUpdate(memberGameProps: PartialUpdateMemberGameProps): Observable<EntityResponseType> {
    return this.http.patch<IMemberGameProps>(`${this.resourceUrl}/${this.getMemberGamePropsIdentifier(memberGameProps)}`, memberGameProps, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMemberGameProps>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMemberGameProps[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMemberGamePropsIdentifier(memberGameProps: Pick<IMemberGameProps, 'id'>): number {
    return memberGameProps.id;
  }

  compareMemberGameProps(o1: Pick<IMemberGameProps, 'id'> | null, o2: Pick<IMemberGameProps, 'id'> | null): boolean {
    return o1 && o2 ? this.getMemberGamePropsIdentifier(o1) === this.getMemberGamePropsIdentifier(o2) : o1 === o2;
  }

  addMemberGamePropsToCollectionIfMissing<Type extends Pick<IMemberGameProps, 'id'>>(
    memberGamePropsCollection: Type[],
    ...memberGamePropsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const memberGameProps: Type[] = memberGamePropsToCheck.filter(isPresent);
    if (memberGameProps.length > 0) {
      const memberGamePropsCollectionIdentifiers = memberGamePropsCollection.map(
        memberGamePropsItem => this.getMemberGamePropsIdentifier(memberGamePropsItem)!
      );
      const memberGamePropsToAdd = memberGameProps.filter(memberGamePropsItem => {
        const memberGamePropsIdentifier = this.getMemberGamePropsIdentifier(memberGamePropsItem);
        if (memberGamePropsCollectionIdentifiers.includes(memberGamePropsIdentifier)) {
          return false;
        }
        memberGamePropsCollectionIdentifiers.push(memberGamePropsIdentifier);
        return true;
      });
      return [...memberGamePropsToAdd, ...memberGamePropsCollection];
    }
    return memberGamePropsCollection;
  }
}

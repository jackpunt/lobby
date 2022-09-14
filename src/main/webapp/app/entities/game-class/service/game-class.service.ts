import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGameClass, NewGameClass } from '../game-class.model';

export type PartialUpdateGameClass = Partial<IGameClass> & Pick<IGameClass, 'id'>;

type RestOf<T extends IGameClass | NewGameClass> = Omit<T, 'updated'> & {
  updated?: string | null;
};

export type RestGameClass = RestOf<IGameClass>;

export type NewRestGameClass = RestOf<NewGameClass>;

export type PartialUpdateRestGameClass = RestOf<PartialUpdateGameClass>;

export type EntityResponseType = HttpResponse<IGameClass>;
export type EntityArrayResponseType = HttpResponse<IGameClass[]>;

@Injectable({ providedIn: 'root' })
export class GameClassService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/game-classes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(gameClass: NewGameClass): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(gameClass);
    return this.http
      .post<RestGameClass>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(gameClass: IGameClass): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(gameClass);
    return this.http
      .put<RestGameClass>(`${this.resourceUrl}/${this.getGameClassIdentifier(gameClass)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(gameClass: PartialUpdateGameClass): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(gameClass);
    return this.http
      .patch<RestGameClass>(`${this.resourceUrl}/${this.getGameClassIdentifier(gameClass)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestGameClass>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestGameClass[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getGameClassIdentifier(gameClass: Pick<IGameClass, 'id'>): number {
    return gameClass.id;
  }

  compareGameClass(o1: Pick<IGameClass, 'id'> | null, o2: Pick<IGameClass, 'id'> | null): boolean {
    return o1 && o2 ? this.getGameClassIdentifier(o1) === this.getGameClassIdentifier(o2) : o1 === o2;
  }

  addGameClassToCollectionIfMissing<Type extends Pick<IGameClass, 'id'>>(
    gameClassCollection: Type[],
    ...gameClassesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const gameClasses: Type[] = gameClassesToCheck.filter(isPresent);
    if (gameClasses.length > 0) {
      const gameClassCollectionIdentifiers = gameClassCollection.map(gameClassItem => this.getGameClassIdentifier(gameClassItem)!);
      const gameClassesToAdd = gameClasses.filter(gameClassItem => {
        const gameClassIdentifier = this.getGameClassIdentifier(gameClassItem);
        if (gameClassCollectionIdentifiers.includes(gameClassIdentifier)) {
          return false;
        }
        gameClassCollectionIdentifiers.push(gameClassIdentifier);
        return true;
      });
      return [...gameClassesToAdd, ...gameClassCollection];
    }
    return gameClassCollection;
  }

  protected convertDateFromClient<T extends IGameClass | NewGameClass | PartialUpdateGameClass>(gameClass: T): RestOf<T> {
    return {
      ...gameClass,
      updated: gameClass.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restGameClass: RestGameClass): IGameClass {
    return {
      ...restGameClass,
      updated: restGameClass.updated ? dayjs(restGameClass.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestGameClass>): HttpResponse<IGameClass> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestGameClass[]>): HttpResponse<IGameClass[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}

import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGameInst, NewGameInst } from '../game-inst.model';

export type PartialUpdateGameInst = Partial<IGameInst> & Pick<IGameInst, 'id'>;

type RestOf<T extends IGameInst | NewGameInst> = Omit<T, 'created' | 'started' | 'finished' | 'updated'> & {
  created?: string | null;
  started?: string | null;
  finished?: string | null;
  updated?: string | null;
};

export type RestGameInst = RestOf<IGameInst>;

export type NewRestGameInst = RestOf<NewGameInst>;

export type PartialUpdateRestGameInst = RestOf<PartialUpdateGameInst>;

export type EntityResponseType = HttpResponse<IGameInst>;
export type EntityArrayResponseType = HttpResponse<IGameInst[]>;

@Injectable({ providedIn: 'root' })
export class GameInstService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/game-insts');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(gameInst: NewGameInst): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(gameInst);
    return this.http
      .post<RestGameInst>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(gameInst: IGameInst): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(gameInst);
    return this.http
      .put<RestGameInst>(`${this.resourceUrl}/${this.getGameInstIdentifier(gameInst)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(gameInst: PartialUpdateGameInst): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(gameInst);
    return this.http
      .patch<RestGameInst>(`${this.resourceUrl}/${this.getGameInstIdentifier(gameInst)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestGameInst>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestGameInst[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getGameInstIdentifier(gameInst: Pick<IGameInst, 'id'>): number {
    return gameInst.id;
  }

  compareGameInst(o1: Pick<IGameInst, 'id'> | null, o2: Pick<IGameInst, 'id'> | null): boolean {
    return o1 && o2 ? this.getGameInstIdentifier(o1) === this.getGameInstIdentifier(o2) : o1 === o2;
  }

  addGameInstToCollectionIfMissing<Type extends Pick<IGameInst, 'id'>>(
    gameInstCollection: Type[],
    ...gameInstsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const gameInsts: Type[] = gameInstsToCheck.filter(isPresent);
    if (gameInsts.length > 0) {
      const gameInstCollectionIdentifiers = gameInstCollection.map(gameInstItem => this.getGameInstIdentifier(gameInstItem)!);
      const gameInstsToAdd = gameInsts.filter(gameInstItem => {
        const gameInstIdentifier = this.getGameInstIdentifier(gameInstItem);
        if (gameInstCollectionIdentifiers.includes(gameInstIdentifier)) {
          return false;
        }
        gameInstCollectionIdentifiers.push(gameInstIdentifier);
        return true;
      });
      return [...gameInstsToAdd, ...gameInstCollection];
    }
    return gameInstCollection;
  }

  protected convertDateFromClient<T extends IGameInst | NewGameInst | PartialUpdateGameInst>(gameInst: T): RestOf<T> {
    return {
      ...gameInst,
      created: gameInst.created?.toJSON() ?? null,
      started: gameInst.started?.toJSON() ?? null,
      finished: gameInst.finished?.toJSON() ?? null,
      updated: gameInst.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restGameInst: RestGameInst): IGameInst {
    return {
      ...restGameInst,
      created: restGameInst.created ? dayjs(restGameInst.created) : undefined,
      started: restGameInst.started ? dayjs(restGameInst.started) : undefined,
      finished: restGameInst.finished ? dayjs(restGameInst.finished) : undefined,
      updated: restGameInst.updated ? dayjs(restGameInst.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestGameInst>): HttpResponse<IGameInst> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestGameInst[]>): HttpResponse<IGameInst[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}

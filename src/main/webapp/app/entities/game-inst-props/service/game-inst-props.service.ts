import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGameInstProps, NewGameInstProps } from '../game-inst-props.model';

export type PartialUpdateGameInstProps = Partial<IGameInstProps> & Pick<IGameInstProps, 'id'>;

type RestOf<T extends IGameInstProps | NewGameInstProps> = Omit<T, 'updated'> & {
  updated?: string | null;
};

export type RestGameInstProps = RestOf<IGameInstProps>;

export type NewRestGameInstProps = RestOf<NewGameInstProps>;

export type PartialUpdateRestGameInstProps = RestOf<PartialUpdateGameInstProps>;

export type EntityResponseType = HttpResponse<IGameInstProps>;
export type EntityArrayResponseType = HttpResponse<IGameInstProps[]>;

@Injectable({ providedIn: 'root' })
export class GameInstPropsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/game-inst-props');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(gameInstProps: NewGameInstProps): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(gameInstProps);
    return this.http
      .post<RestGameInstProps>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(gameInstProps: IGameInstProps): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(gameInstProps);
    return this.http
      .put<RestGameInstProps>(`${this.resourceUrl}/${this.getGameInstPropsIdentifier(gameInstProps)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(gameInstProps: PartialUpdateGameInstProps): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(gameInstProps);
    return this.http
      .patch<RestGameInstProps>(`${this.resourceUrl}/${this.getGameInstPropsIdentifier(gameInstProps)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestGameInstProps>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestGameInstProps[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getGameInstPropsIdentifier(gameInstProps: Pick<IGameInstProps, 'id'>): number {
    return gameInstProps.id;
  }

  compareGameInstProps(o1: Pick<IGameInstProps, 'id'> | null, o2: Pick<IGameInstProps, 'id'> | null): boolean {
    return o1 && o2 ? this.getGameInstPropsIdentifier(o1) === this.getGameInstPropsIdentifier(o2) : o1 === o2;
  }

  addGameInstPropsToCollectionIfMissing<Type extends Pick<IGameInstProps, 'id'>>(
    gameInstPropsCollection: Type[],
    ...gameInstPropsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const gameInstProps: Type[] = gameInstPropsToCheck.filter(isPresent);
    if (gameInstProps.length > 0) {
      const gameInstPropsCollectionIdentifiers = gameInstPropsCollection.map(
        gameInstPropsItem => this.getGameInstPropsIdentifier(gameInstPropsItem)!
      );
      const gameInstPropsToAdd = gameInstProps.filter(gameInstPropsItem => {
        const gameInstPropsIdentifier = this.getGameInstPropsIdentifier(gameInstPropsItem);
        if (gameInstPropsCollectionIdentifiers.includes(gameInstPropsIdentifier)) {
          return false;
        }
        gameInstPropsCollectionIdentifiers.push(gameInstPropsIdentifier);
        return true;
      });
      return [...gameInstPropsToAdd, ...gameInstPropsCollection];
    }
    return gameInstPropsCollection;
  }

  protected convertDateFromClient<T extends IGameInstProps | NewGameInstProps | PartialUpdateGameInstProps>(gameInstProps: T): RestOf<T> {
    return {
      ...gameInstProps,
      updated: gameInstProps.updated?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restGameInstProps: RestGameInstProps): IGameInstProps {
    return {
      ...restGameInstProps,
      updated: restGameInstProps.updated ? dayjs(restGameInstProps.updated) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestGameInstProps>): HttpResponse<IGameInstProps> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestGameInstProps[]>): HttpResponse<IGameInstProps[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}

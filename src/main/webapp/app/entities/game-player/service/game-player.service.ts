import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGamePlayer, NewGamePlayer } from '../game-player.model';

export type PartialUpdateGamePlayer = Partial<IGamePlayer> & Pick<IGamePlayer, 'id'>;

export type EntityResponseType = HttpResponse<IGamePlayer>;
export type EntityArrayResponseType = HttpResponse<IGamePlayer[]>;

@Injectable({ providedIn: 'root' })
export class GamePlayerService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/game-players');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(gamePlayer: NewGamePlayer): Observable<EntityResponseType> {
    return this.http.post<IGamePlayer>(this.resourceUrl, gamePlayer, { observe: 'response' });
  }

  update(gamePlayer: IGamePlayer): Observable<EntityResponseType> {
    return this.http.put<IGamePlayer>(`${this.resourceUrl}/${this.getGamePlayerIdentifier(gamePlayer)}`, gamePlayer, {
      observe: 'response',
    });
  }

  partialUpdate(gamePlayer: PartialUpdateGamePlayer): Observable<EntityResponseType> {
    return this.http.patch<IGamePlayer>(`${this.resourceUrl}/${this.getGamePlayerIdentifier(gamePlayer)}`, gamePlayer, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGamePlayer>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGamePlayer[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getGamePlayerIdentifier(gamePlayer: Pick<IGamePlayer, 'id'>): number {
    return gamePlayer.id;
  }

  compareGamePlayer(o1: Pick<IGamePlayer, 'id'> | null, o2: Pick<IGamePlayer, 'id'> | null): boolean {
    return o1 && o2 ? this.getGamePlayerIdentifier(o1) === this.getGamePlayerIdentifier(o2) : o1 === o2;
  }

  addGamePlayerToCollectionIfMissing<Type extends Pick<IGamePlayer, 'id'>>(
    gamePlayerCollection: Type[],
    ...gamePlayersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const gamePlayers: Type[] = gamePlayersToCheck.filter(isPresent);
    if (gamePlayers.length > 0) {
      const gamePlayerCollectionIdentifiers = gamePlayerCollection.map(gamePlayerItem => this.getGamePlayerIdentifier(gamePlayerItem)!);
      const gamePlayersToAdd = gamePlayers.filter(gamePlayerItem => {
        const gamePlayerIdentifier = this.getGamePlayerIdentifier(gamePlayerItem);
        if (gamePlayerCollectionIdentifiers.includes(gamePlayerIdentifier)) {
          return false;
        }
        gamePlayerCollectionIdentifiers.push(gamePlayerIdentifier);
        return true;
      });
      return [...gamePlayersToAdd, ...gamePlayerCollection];
    }
    return gamePlayerCollection;
  }
}

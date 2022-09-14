import { IGameInst } from 'app/entities/game-inst/game-inst.model';
import { IPlayer } from 'app/entities/player/player.model';

export interface IGamePlayer {
  id: number;
  version?: number | null;
  role?: string | null;
  ready?: number | null;
  gameInst?: Pick<IGameInst, 'id'> | null;
  player?: Pick<IPlayer, 'id'> | null;
}

export type NewGamePlayer = Omit<IGamePlayer, 'id'> & { id: null };

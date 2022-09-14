import dayjs from 'dayjs/esm';
import { IPlayer } from 'app/entities/player/player.model';
import { IGameClass } from 'app/entities/game-class/game-class.model';

export interface IGameInst {
  id: number;
  version?: number | null;
  gameName?: string | null;
  hostUrl?: string | null;
  passcode?: string | null;
  created?: dayjs.Dayjs | null;
  started?: dayjs.Dayjs | null;
  finished?: dayjs.Dayjs | null;
  updated?: dayjs.Dayjs | null;
  scoreA?: number | null;
  scoreB?: number | null;
  ticks?: number | null;
  playerA?: Pick<IPlayer, 'id'> | null;
  playerB?: Pick<IPlayer, 'id'> | null;
  gameClass?: Pick<IGameClass, 'id'> | null;
}

export type NewGameInst = Omit<IGameInst, 'id'> & { id: null };

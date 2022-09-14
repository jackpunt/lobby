import dayjs from 'dayjs/esm';
import { IGameClass } from 'app/entities/game-class/game-class.model';
import { IAsset } from 'app/entities/asset/asset.model';
import { IUser } from 'app/entities/user/user.model';

export interface IPlayer {
  id: number;
  version?: number | null;
  name?: string | null;
  rank?: number | null;
  score?: number | null;
  scoreTime?: dayjs.Dayjs | null;
  rankTime?: dayjs.Dayjs | null;
  displayClient?: string | null;
  gameClass?: Pick<IGameClass, 'id'> | null;
  mainJar?: Pick<IAsset, 'id'> | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewPlayer = Omit<IPlayer, 'id'> & { id: null };

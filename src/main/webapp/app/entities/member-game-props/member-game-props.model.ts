import { IUser } from 'app/entities/user/user.model';
import { IGameClass } from 'app/entities/game-class/game-class.model';

export interface IMemberGameProps {
  id: number;
  version?: number | null;
  seed?: number | null;
  mapName?: string | null;
  mapSize?: number | null;
  npcCount?: number | null;
  jsonProps?: string | null;
  configName?: string | null;
  user?: Pick<IUser, 'id'> | null;
  gameClass?: Pick<IGameClass, 'id'> | null;
}

export type NewMemberGameProps = Omit<IMemberGameProps, 'id'> & { id: null };

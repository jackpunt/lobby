import dayjs from 'dayjs/esm';
import { IGameInst } from 'app/entities/game-inst/game-inst.model';

export interface IGameInstProps {
  id: number;
  version?: number | null;
  seed?: number | null;
  mapName?: string | null;
  mapSize?: number | null;
  npcCount?: number | null;
  jsonProps?: string | null;
  updated?: dayjs.Dayjs | null;
  gameInst?: Pick<IGameInst, 'id'> | null;
}

export type NewGameInstProps = Omit<IGameInstProps, 'id'> & { id: null };

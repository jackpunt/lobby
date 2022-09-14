import { IUser } from 'app/entities/user/user.model';

export interface IAsset {
  id: number;
  version?: number | null;
  name?: string | null;
  main?: boolean | null;
  auto?: boolean | null;
  path?: string | null;
  include?: string | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewAsset = Omit<IAsset, 'id'> & { id: null };

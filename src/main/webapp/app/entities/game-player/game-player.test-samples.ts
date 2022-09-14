import { IGamePlayer, NewGamePlayer } from './game-player.model';

export const sampleWithRequiredData: IGamePlayer = {
  id: 8447,
  role: 'purp',
  ready: 21245,
};

export const sampleWithPartialData: IGamePlayer = {
  id: 95468,
  version: 12651,
  role: 'Avon',
  ready: 63309,
};

export const sampleWithFullData: IGamePlayer = {
  id: 22604,
  version: 34380,
  role: 'Toys',
  ready: 3368,
};

export const sampleWithNewData: NewGamePlayer = {
  role: 'Dyna',
  ready: 35099,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

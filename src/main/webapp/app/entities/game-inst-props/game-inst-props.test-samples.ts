import dayjs from 'dayjs/esm';

import { IGameInstProps, NewGameInstProps } from './game-inst-props.model';

export const sampleWithRequiredData: IGameInstProps = {
  id: 70844,
  updated: dayjs('2022-08-09T01:51'),
};

export const sampleWithPartialData: IGameInstProps = {
  id: 58138,
  seed: 28956,
  updated: dayjs('2022-08-09T08:40'),
};

export const sampleWithFullData: IGameInstProps = {
  id: 49123,
  version: 70943,
  seed: 6858,
  mapName: 'Loan',
  mapSize: 86117,
  npcCount: 38474,
  jsonProps: 'Soap exploit',
  updated: dayjs('2022-08-09T09:41'),
};

export const sampleWithNewData: NewGameInstProps = {
  updated: dayjs('2022-08-09T15:01'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

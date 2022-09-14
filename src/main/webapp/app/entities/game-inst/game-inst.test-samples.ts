import dayjs from 'dayjs/esm';

import { IGameInst, NewGameInst } from './game-inst.model';

export const sampleWithRequiredData: IGameInst = {
  id: 82250,
  created: dayjs('2022-08-09T14:52'),
  updated: dayjs('2022-08-09T07:09'),
};

export const sampleWithPartialData: IGameInst = {
  id: 18754,
  version: 94694,
  gameName: 'mindshare',
  hostUrl: 'Shoes',
  passcode: 'Practical architectures Chicken',
  created: dayjs('2022-08-09T18:34'),
  finished: dayjs('2022-08-09T13:49'),
  updated: dayjs('2022-08-09T15:04'),
  scoreB: 7036,
};

export const sampleWithFullData: IGameInst = {
  id: 76184,
  version: 98916,
  gameName: 'Uruguay',
  hostUrl: 'connecting Cameroon hardware',
  passcode: 'mobile',
  created: dayjs('2022-08-09T20:05'),
  started: dayjs('2022-08-08T23:04'),
  finished: dayjs('2022-08-09T11:59'),
  updated: dayjs('2022-08-09T06:29'),
  scoreA: 28501,
  scoreB: 45049,
  ticks: 44541,
};

export const sampleWithNewData: NewGameInst = {
  created: dayjs('2022-08-09T15:13'),
  updated: dayjs('2022-08-09T06:04'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

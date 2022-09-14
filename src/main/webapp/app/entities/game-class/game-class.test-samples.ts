import dayjs from 'dayjs/esm';

import { IGameClass, NewGameClass } from './game-class.model';

export const sampleWithRequiredData: IGameClass = {
  id: 28064,
  name: 'Paradigm Mouse District',
  updated: dayjs('2022-08-09T06:40'),
};

export const sampleWithPartialData: IGameClass = {
  id: 35477,
  version: 30565,
  name: 'visionary B2B Plastic',
  propNames: 'visionary Ball online',
  updated: dayjs('2022-08-09T19:50'),
};

export const sampleWithFullData: IGameClass = {
  id: 16170,
  version: 46368,
  name: 'Global',
  revision: 'well-modulated Turkmenis',
  launcherPath: 'Future Skyway Naira',
  gamePath: 'invoice Savings Florida',
  docsPath: 'methodologies Vermont',
  propNames: 'e-enable',
  updated: dayjs('2022-08-09T10:13'),
};

export const sampleWithNewData: NewGameClass = {
  name: 'Pizza',
  updated: dayjs('2022-08-08T22:25'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

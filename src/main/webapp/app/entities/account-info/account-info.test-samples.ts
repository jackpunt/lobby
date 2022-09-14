import { IAccountInfo, NewAccountInfo } from './account-info.model';

export const sampleWithRequiredData: IAccountInfo = {
  id: 2760,
};

export const sampleWithPartialData: IAccountInfo = {
  id: 12560,
  version: 34430,
  type: 'Granite invoice',
};

export const sampleWithFullData: IAccountInfo = {
  id: 67736,
  version: 28926,
  type: 'leverage COM',
};

export const sampleWithNewData: NewAccountInfo = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

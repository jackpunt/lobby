import { IAsset, NewAsset } from './asset.model';

export const sampleWithRequiredData: IAsset = {
  id: 64724,
};

export const sampleWithPartialData: IAsset = {
  id: 9020,
  main: false,
  path: 'Montana deposit',
};

export const sampleWithFullData: IAsset = {
  id: 6997,
  version: 72609,
  name: 'Hat',
  main: true,
  auto: false,
  path: 'utilize Pass',
  include: 'multi-state communities Intranet',
};

export const sampleWithNewData: NewAsset = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

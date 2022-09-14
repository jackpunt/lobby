import { IMemberGameProps, NewMemberGameProps } from './member-game-props.model';

export const sampleWithRequiredData: IMemberGameProps = {
  id: 51636,
};

export const sampleWithPartialData: IMemberGameProps = {
  id: 23522,
  version: 77436,
  seed: 88428,
  mapName: 'eco-centric Generic mobile',
  mapSize: 11527,
  npcCount: 82621,
  jsonProps: 'Liaison',
};

export const sampleWithFullData: IMemberGameProps = {
  id: 73984,
  version: 48332,
  seed: 32231,
  mapName: 'indexing Gorgeous',
  mapSize: 34926,
  npcCount: 4611,
  jsonProps: 'capacitor Accountability',
  configName: 'experiences Manager Alabama',
};

export const sampleWithNewData: NewMemberGameProps = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

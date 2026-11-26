import dayjs from 'dayjs/esm';

import { IRendezVous, NewRendezVous } from './rendez-vous.model';

export const sampleWithRequiredData: IRendezVous = {
  id: 19768,
  date: dayjs('2026-11-26'),
  heure: '19:51',
  motif: 'puisque',
};

export const sampleWithPartialData: IRendezVous = {
  id: 29663,
  date: dayjs('2026-11-26'),
  heure: '21:37',
  motif: 'ding rose',
};

export const sampleWithFullData: IRendezVous = {
  id: 19313,
  date: dayjs('2026-11-26'),
  heure: '21:45',
  motif: 'prévenir',
};

export const sampleWithNewData: NewRendezVous = {
  date: dayjs('2026-11-26'),
  heure: '04:24',
  motif: 'clientèle',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

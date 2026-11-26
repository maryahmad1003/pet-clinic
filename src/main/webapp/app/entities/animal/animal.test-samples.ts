import dayjs from 'dayjs/esm';

import { IAnimal, NewAnimal } from './animal.model';

export const sampleWithRequiredData: IAnimal = {
  id: 15325,
  nom: 'crac conférer',
  espece: 'à même grandement',
};

export const sampleWithPartialData: IAnimal = {
  id: 32160,
  nom: 'passager',
  espece: 'touriste',
  dateNaissance: dayjs('2026-11-26'),
  poids: 19737.27,
};

export const sampleWithFullData: IAnimal = {
  id: 28924,
  nom: 'bientôt',
  espece: 'à la faveur de apporter',
  dateNaissance: dayjs('2026-11-25'),
  poids: 3484.96,
};

export const sampleWithNewData: NewAnimal = {
  nom: 'timide',
  espece: "d'abord extrêmement",
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

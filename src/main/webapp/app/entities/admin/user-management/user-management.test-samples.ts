import dayjs from 'dayjs/esm';

import { IUserManagement, NewUserManagement } from './user-management.model';

export const sampleWithRequiredData: IUserManagement = {
  login: 'Quentin.Petit',
  email: 'Julia5@hotmail.fr',
};

export const sampleWithPartialData: IUserManagement = {
  id: 18824,
  login: 'Rejeanne.Louis4',
  firstName: 'Patrice',
  lastName: 'Cousin',
  email: 'Daphne44@hotmail.fr',
  activated: false,
  langKey: 'fr',
  lastModifiedBy: 'reproduire drelin',
  lastModifiedDate: dayjs('2026-11-25T17:46'),
};

export const sampleWithFullData: IUserManagement = {
  id: 26558,
  login: 'Anastasie72',
  firstName: 'Jocelyne',
  lastName: 'Blanc',
  email: 'Serge_Andre65@hotmail.fr',
  activated: false,
  langKey: 'fr',
  imageUrl: 'de sorte que assurément en decà de',
  createdBy: 'aux alentours de admirablement',
  createdDate: dayjs('2026-11-25T22:01'),
  lastModifiedBy: 'de manière à ce que sédentaire deçà',
  lastModifiedDate: dayjs('2026-11-25T17:55'),
};

export const sampleWithNewData: NewUserManagement = {
  email: 'Gontran82@yahoo.fr',
  login: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

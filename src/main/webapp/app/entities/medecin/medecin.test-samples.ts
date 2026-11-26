import { IMedecin, NewMedecin } from './medecin.model';

export const sampleWithRequiredData: IMedecin = {
  id: 3958,
  nom: 'engendrer vlan',
  prenom: 'jusque',
  specialite: 'entre',
  email: 'Judicael_Rousseau@yahoo.fr',
};

export const sampleWithPartialData: IMedecin = {
  id: 24370,
  nom: 'renseigner',
  prenom: 'chut sale',
  specialite: 'tant que smack',
  email: 'Rejeanne_David@hotmail.fr',
};

export const sampleWithFullData: IMedecin = {
  id: 22099,
  nom: 'concernant à même bien que',
  prenom: 'étant donné que feindre vu que',
  specialite: 'super préférer',
  email: 'Florence_Garnier92@yahoo.fr',
};

export const sampleWithNewData: NewMedecin = {
  nom: 'hé',
  prenom: 'sous géométrique de façon que',
  specialite: 'aussitôt que',
  email: 'Maguelone_Lucas@gmail.com',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

import { IClinique, NewClinique } from './clinique.model';

export const sampleWithRequiredData: IClinique = {
  id: 24981,
  nom: 'répartir là-haut puisque',
  adresse: 'marier',
};

export const sampleWithPartialData: IClinique = {
  id: 28263,
  nom: 'toc',
  adresse: 'ronron aimable',
  telephone: '0675990273',
};

export const sampleWithFullData: IClinique = {
  id: 212,
  nom: 'innombrable là',
  adresse: 'si bien que près',
  telephone: '+33 343177744',
};

export const sampleWithNewData: NewClinique = {
  nom: 'aigre',
  adresse: 'aux environs de lors',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

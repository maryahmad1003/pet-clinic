import { IClient, NewClient } from './client.model';

export const sampleWithRequiredData: IClient = {
  id: 16289,
  nom: 'du côté de chialer oh',
  prenom: 'à même ailleurs partenaire',
};

export const sampleWithPartialData: IClient = {
  id: 14463,
  nom: 'à seule fin de',
  prenom: 'apaiser',
  adresse: 'tandis que personnel',
  telephone: '+33 712458332',
};

export const sampleWithFullData: IClient = {
  id: 31496,
  nom: 'bzzz lectorat séculaire',
  prenom: 'tellement de peur que',
  adresse: 'diplomate',
  telephone: '0259588688',
};

export const sampleWithNewData: NewClient = {
  nom: 'tôt à la merci',
  prenom: 'infime tranquille',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

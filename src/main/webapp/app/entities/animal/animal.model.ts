import dayjs from 'dayjs/esm';

import { IClient } from 'app/entities/client/client.model';

export interface IAnimal {
  id: number;
  nom?: string | null;
  espece?: string | null;
  dateNaissance?: dayjs.Dayjs | null;
  poids?: number | null;
  client?: IClient | null;
}

export type NewAnimal = Omit<IAnimal, 'id'> & { id: null };

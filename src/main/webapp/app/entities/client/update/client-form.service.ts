import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IClient, NewClient } from '../client.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IClient for edit and NewClientFormGroupInput for create.
 */
type ClientFormGroupInput = IClient | PartialWithRequiredKeyOf<NewClient>;

type ClientFormDefaults = Pick<NewClient, 'id'>;

type ClientFormGroupContent = {
  id: FormControl<IClient['id'] | NewClient['id']>;
  nom: FormControl<IClient['nom']>;
  prenom: FormControl<IClient['prenom']>;
  adresse: FormControl<IClient['adresse']>;
  telephone: FormControl<IClient['telephone']>;
};

export type ClientFormGroup = FormGroup<ClientFormGroupContent>;

@Service()
export class ClientFormService {
  createClientFormGroup(client?: ClientFormGroupInput): ClientFormGroup {
    const clientRawValue = {
      ...this.getFormDefaults(),
      ...(client ?? { id: null }),
    };

    return new FormGroup<ClientFormGroupContent>({
      id: new FormControl(
        { value: clientRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nom: new FormControl(clientRawValue.nom, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      prenom: new FormControl(clientRawValue.prenom, {
        validators: [Validators.required, Validators.maxLength(80)],
      }),
      adresse: new FormControl(clientRawValue.adresse, {
        validators: [Validators.maxLength(255)],
      }),
      telephone: new FormControl(clientRawValue.telephone, {
        validators: [Validators.maxLength(20)],
      }),
    });
  }

  getClient(form: ClientFormGroup): IClient | NewClient {
    return form.getRawValue();
  }

  resetForm(form: ClientFormGroup, client: ClientFormGroupInput): void {
    const clientRawValue = { ...this.getFormDefaults(), ...client };
    form.reset({
      ...clientRawValue,
      id: { value: clientRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ClientFormDefaults {
    return {
      id: null,
    };
  }
}

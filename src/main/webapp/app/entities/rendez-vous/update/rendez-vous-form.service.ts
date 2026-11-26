import { Service } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IRendezVous, NewRendezVous } from '../rendez-vous.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRendezVous for edit and NewRendezVousFormGroupInput for create.
 */
type RendezVousFormGroupInput = IRendezVous | PartialWithRequiredKeyOf<NewRendezVous>;

type RendezVousFormDefaults = Pick<NewRendezVous, 'id'>;

type RendezVousFormGroupContent = {
  id: FormControl<IRendezVous['id'] | NewRendezVous['id']>;
  date: FormControl<IRendezVous['date']>;
  heure: FormControl<IRendezVous['heure']>;
  motif: FormControl<IRendezVous['motif']>;
  animal: FormControl<IRendezVous['animal']>;
  medecin: FormControl<IRendezVous['medecin']>;
  clinique: FormControl<IRendezVous['clinique']>;
};

export type RendezVousFormGroup = FormGroup<RendezVousFormGroupContent>;

@Service()
export class RendezVousFormService {
  createRendezVousFormGroup(rendezVous?: RendezVousFormGroupInput): RendezVousFormGroup {
    const rendezVousRawValue = {
      ...this.getFormDefaults(),
      ...(rendezVous ?? { id: null }),
    };

    return new FormGroup<RendezVousFormGroupContent>({
      id: new FormControl(
        { value: rendezVousRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      date: new FormControl(rendezVousRawValue.date, {
        validators: [Validators.required],
      }),
      heure: new FormControl(rendezVousRawValue.heure, {
        validators: [
          Validators.required,
          Validators.pattern('^([01]\\d|2[0-3]):[0-5]\\d$'), // NOSONAR
        ],
      }),
      motif: new FormControl(rendezVousRawValue.motif, {
        validators: [Validators.required, Validators.maxLength(500)],
      }),
      animal: new FormControl(rendezVousRawValue.animal),
      medecin: new FormControl(rendezVousRawValue.medecin),
      clinique: new FormControl(rendezVousRawValue.clinique),
    });
  }

  getRendezVous(form: RendezVousFormGroup): IRendezVous | NewRendezVous {
    return form.getRawValue();
  }

  resetForm(form: RendezVousFormGroup, rendezVous: RendezVousFormGroupInput): void {
    const rendezVousRawValue = { ...this.getFormDefaults(), ...rendezVous };
    form.reset({
      ...rendezVousRawValue,
      id: { value: rendezVousRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): RendezVousFormDefaults {
    return {
      id: null,
    };
  }
}

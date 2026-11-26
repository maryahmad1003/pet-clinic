import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { IAnimal } from '../animal.model';

@Component({
  selector: 'jhi-animal-detail',
  templateUrl: './animal-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, FormatMediumDatePipe],
})
export class AnimalDetail {
  readonly animal = input<IAnimal | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}

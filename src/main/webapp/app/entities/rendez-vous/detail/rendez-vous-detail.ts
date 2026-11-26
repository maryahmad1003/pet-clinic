import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { FormatMediumDatePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { IRendezVous } from '../rendez-vous.model';

@Component({
  selector: 'jhi-rendez-vous-detail',
  templateUrl: './rendez-vous-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink, FormatMediumDatePipe],
})
export class RendezVousDetail {
  readonly rendezVous = input<IRendezVous | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}

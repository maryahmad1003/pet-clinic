import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { IMedecin } from '../medecin.model';

@Component({
  selector: 'jhi-medecin-detail',
  templateUrl: './medecin-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink],
})
export class MedecinDetail {
  readonly medecin = input<IMedecin | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}

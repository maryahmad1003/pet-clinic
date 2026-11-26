import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert, AlertError } from 'app/shared/alert';
import { TranslateDirective } from 'app/shared/language';
import { IClinique } from '../clinique.model';

@Component({
  selector: 'jhi-clinique-detail',
  templateUrl: './clinique-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, RouterLink],
})
export class CliniqueDetail {
  readonly clinique = input<IClinique | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}

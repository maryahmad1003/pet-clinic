import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import RendezVousResolve from './route/rendez-vous-routing-resolve.service';

const rendezVousRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/rendez-vous').then(m => m.RendezVous),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/rendez-vous-detail').then(m => m.RendezVousDetail),
    resolve: {
      rendezVous: RendezVousResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/rendez-vous-update').then(m => m.RendezVousUpdate),
    resolve: {
      rendezVous: RendezVousResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/rendez-vous-update').then(m => m.RendezVousUpdate),
    resolve: {
      rendezVous: RendezVousResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default rendezVousRoute;

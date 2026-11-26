import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import MedecinResolve from './route/medecin-routing-resolve.service';

const medecinRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/medecin').then(m => m.Medecin),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/medecin-detail').then(m => m.MedecinDetail),
    resolve: {
      medecin: MedecinResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/medecin-update').then(m => m.MedecinUpdate),
    resolve: {
      medecin: MedecinResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/medecin-update').then(m => m.MedecinUpdate),
    resolve: {
      medecin: MedecinResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default medecinRoute;

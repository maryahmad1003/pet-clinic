import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import CliniqueResolve from './route/clinique-routing-resolve.service';

const cliniqueRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/clinique').then(m => m.Clinique),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/clinique-detail').then(m => m.CliniqueDetail),
    resolve: {
      clinique: CliniqueResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/clinique-update').then(m => m.CliniqueUpdate),
    resolve: {
      clinique: CliniqueResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/clinique-update').then(m => m.CliniqueUpdate),
    resolve: {
      clinique: CliniqueResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default cliniqueRoute;

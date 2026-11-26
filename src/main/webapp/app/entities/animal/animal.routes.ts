import { Routes } from '@angular/router';

import { ASC } from 'app/config';
import { userRouteAccessService } from 'app/core/auth';

import AnimalResolve from './route/animal-routing-resolve.service';

const animalRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/animal').then(m => m.Animal),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/animal-detail').then(m => m.AnimalDetail),
    resolve: {
      animal: AnimalResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/animal-update').then(m => m.AnimalUpdate),
    resolve: {
      animal: AnimalResolve,
    },
    canActivate: [userRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/animal-update').then(m => m.AnimalUpdate),
    resolve: {
      animal: AnimalResolve,
    },
    canActivate: [userRouteAccessService],
  },
];

export default animalRoute;

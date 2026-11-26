import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'user-management',
    title: 'userManagement.home.title',
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  {
    path: 'authority',
    title: 'petclinicApp.adminAuthority.home.title',
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'clinique',
    title: 'petclinicApp.clinique.home.title',
    loadChildren: () => import('./clinique/clinique.routes'),
  },
  {
    path: 'medecin',
    title: 'petclinicApp.medecin.home.title',
    loadChildren: () => import('./medecin/medecin.routes'),
  },
  {
    path: 'client',
    title: 'petclinicApp.client.home.title',
    loadChildren: () => import('./client/client.routes'),
  },
  {
    path: 'animal',
    title: 'petclinicApp.animal.home.title',
    loadChildren: () => import('./animal/animal.routes'),
  },
  {
    path: 'rendez-vous',
    title: 'petclinicApp.rendezVous.home.title',
    loadChildren: () => import('./rendez-vous/rendez-vous.routes'),
  },
  // jhipster-needle-add-entity-route - JHipster will add entity modules routes here
];

export default routes;

import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'plate-map',
        loadChildren: () => import('./plate-map/plate-map.module').then(m => m.CabinetPlateMapModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class CabinetEntityModule {}

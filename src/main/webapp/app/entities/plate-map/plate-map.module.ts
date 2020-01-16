import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CabinetSharedModule } from 'app/shared/shared.module';
import { PlateMapComponent } from './plate-map.component';
import { PlateMapDetailComponent } from './plate-map-detail.component';
import { PlateMapUpdateComponent } from './plate-map-update.component';
import { PlateMapDeleteDialogComponent } from './plate-map-delete-dialog.component';
import { plateMapRoute } from './plate-map.route';

@NgModule({
  imports: [CabinetSharedModule, RouterModule.forChild(plateMapRoute)],
  declarations: [PlateMapComponent, PlateMapDetailComponent, PlateMapUpdateComponent, PlateMapDeleteDialogComponent],
  entryComponents: [PlateMapDeleteDialogComponent]
})
export class CabinetPlateMapModule {}

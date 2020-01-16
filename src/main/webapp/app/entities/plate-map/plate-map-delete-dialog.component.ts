import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IPlateMap } from 'app/shared/model/plate-map.model';
import { PlateMapService } from './plate-map.service';

@Component({
  templateUrl: './plate-map-delete-dialog.component.html'
})
export class PlateMapDeleteDialogComponent {
  plateMap?: IPlateMap;

  constructor(protected plateMapService: PlateMapService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.plateMapService.delete(id).subscribe(() => {
      this.eventManager.broadcast('plateMapListModification');
      this.activeModal.close();
    });
  }
}

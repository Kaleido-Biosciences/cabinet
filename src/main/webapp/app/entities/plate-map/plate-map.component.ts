import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPlateMap } from 'app/shared/model/plate-map.model';
import { PlateMapService } from './plate-map.service';
import { PlateMapDeleteDialogComponent } from './plate-map-delete-dialog.component';

@Component({
  selector: 'jhi-plate-map',
  templateUrl: './plate-map.component.html'
})
export class PlateMapComponent implements OnInit, OnDestroy {
  plateMaps?: IPlateMap[];
  eventSubscriber?: Subscription;
  currentSearch: string;

  constructor(
    protected plateMapService: PlateMapService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch =
      this.activatedRoute.snapshot && this.activatedRoute.snapshot.queryParams['search']
        ? this.activatedRoute.snapshot.queryParams['search']
        : '';
  }

  loadAll(): void {
    if (this.currentSearch) {
      this.plateMapService
        .search({
          query: this.currentSearch
        })
        .subscribe((res: HttpResponse<IPlateMap[]>) => (this.plateMaps = res.body ? res.body : []));
      return;
    }
    this.plateMapService.query().subscribe((res: HttpResponse<IPlateMap[]>) => {
      this.plateMaps = res.body ? res.body : [];
      this.currentSearch = '';
    });
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInPlateMaps();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IPlateMap): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInPlateMaps(): void {
    this.eventSubscriber = this.eventManager.subscribe('plateMapListModification', () => this.loadAll());
  }

  delete(plateMap: IPlateMap): void {
    const modalRef = this.modalService.open(PlateMapDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.plateMap = plateMap;
  }
}

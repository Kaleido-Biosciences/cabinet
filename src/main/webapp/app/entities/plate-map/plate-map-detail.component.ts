import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPlateMap } from 'app/shared/model/plate-map.model';

@Component({
  selector: 'jhi-plate-map-detail',
  templateUrl: './plate-map-detail.component.html'
})
export class PlateMapDetailComponent implements OnInit {
  plateMap: IPlateMap | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ plateMap }) => {
      this.plateMap = plateMap;
    });
  }

  previousState(): void {
    window.history.back();
  }
}

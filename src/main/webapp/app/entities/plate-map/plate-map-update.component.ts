import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IPlateMap, PlateMap } from 'app/shared/model/plate-map.model';
import { PlateMapService } from './plate-map.service';

@Component({
  selector: 'jhi-plate-map-update',
  templateUrl: './plate-map-update.component.html'
})
export class PlateMapUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    status: [],
    lastModified: [],
    checksum: [],
    activityName: [],
    data: [null, [Validators.maxLength(10485760)]],
    numPlates: []
  });

  constructor(protected plateMapService: PlateMapService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ plateMap }) => {
      this.updateForm(plateMap);
    });
  }

  updateForm(plateMap: IPlateMap): void {
    this.editForm.patchValue({
      id: plateMap.id,
      status: plateMap.status,
      lastModified: plateMap.lastModified != null ? plateMap.lastModified.format(DATE_TIME_FORMAT) : null,
      checksum: plateMap.checksum,
      activityName: plateMap.activityName,
      data: plateMap.data,
      numPlates: plateMap.numPlates
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const plateMap = this.createFromForm();
    if (plateMap.id !== undefined) {
      this.subscribeToSaveResponse(this.plateMapService.update(plateMap));
    } else {
      this.subscribeToSaveResponse(this.plateMapService.create(plateMap));
    }
  }

  private createFromForm(): IPlateMap {
    return {
      ...new PlateMap(),
      id: this.editForm.get(['id'])!.value,
      status: this.editForm.get(['status'])!.value,
      lastModified:
        this.editForm.get(['lastModified'])!.value != null
          ? moment(this.editForm.get(['lastModified'])!.value, DATE_TIME_FORMAT)
          : undefined,
      checksum: this.editForm.get(['checksum'])!.value,
      activityName: this.editForm.get(['activityName'])!.value,
      data: this.editForm.get(['data'])!.value,
      numPlates: this.editForm.get(['numPlates'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPlateMap>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }
}

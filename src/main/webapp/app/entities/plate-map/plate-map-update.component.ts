import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IPlateMap, PlateMap } from 'app/shared/model/plate-map.model';
import { PlateMapService } from './plate-map.service';
import { IActivity } from 'app/shared/model/activity.model';
import { ActivityService } from 'app/entities/activity/activity.service';

@Component({
  selector: 'jhi-plate-map-update',
  templateUrl: './plate-map-update.component.html'
})
export class PlateMapUpdateComponent implements OnInit {
  isSaving = false;

  activities: IActivity[] = [];

  editForm = this.fb.group({
    id: [],
    status: [],
    lastModified: [],
    checksum: [],
    data: [null, [Validators.maxLength(1073741824)]],
    activity: []
  });

  constructor(
    protected plateMapService: PlateMapService,
    protected activityService: ActivityService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ plateMap }) => {
      this.updateForm(plateMap);

      this.activityService
        .query()
        .pipe(
          map((res: HttpResponse<IActivity[]>) => {
            return res.body ? res.body : [];
          })
        )
        .subscribe((resBody: IActivity[]) => (this.activities = resBody));
    });
  }

  updateForm(plateMap: IPlateMap): void {
    this.editForm.patchValue({
      id: plateMap.id,
      status: plateMap.status,
      lastModified: plateMap.lastModified != null ? plateMap.lastModified.format(DATE_TIME_FORMAT) : null,
      checksum: plateMap.checksum,
      data: plateMap.data,
      activity: plateMap.activity
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
      data: this.editForm.get(['data'])!.value,
      activity: this.editForm.get(['activity'])!.value
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

  trackById(index: number, item: IActivity): any {
    return item.id;
  }
}

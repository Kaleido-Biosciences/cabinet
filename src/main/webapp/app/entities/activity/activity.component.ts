import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IActivity } from 'app/shared/model/activity.model';
import { ActivityService } from './activity.service';
import { ActivityDeleteDialogComponent } from './activity-delete-dialog.component';

@Component({
  selector: 'jhi-activity',
  templateUrl: './activity.component.html'
})
export class ActivityComponent implements OnInit, OnDestroy {
  activities?: IActivity[];
  eventSubscriber?: Subscription;
  currentSearch: string;

  constructor(
    protected activityService: ActivityService,
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
      this.activityService
        .search({
          query: this.currentSearch
        })
        .subscribe((res: HttpResponse<IActivity[]>) => (this.activities = res.body ? res.body : []));
      return;
    }
    this.activityService.query().subscribe((res: HttpResponse<IActivity[]>) => {
      this.activities = res.body ? res.body : [];
      this.currentSearch = '';
    });
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInActivities();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IActivity): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInActivities(): void {
    this.eventSubscriber = this.eventManager.subscribe('activityListModification', () => this.loadAll());
  }

  delete(activity: IActivity): void {
    const modalRef = this.modalService.open(ActivityDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.activity = activity;
  }
}

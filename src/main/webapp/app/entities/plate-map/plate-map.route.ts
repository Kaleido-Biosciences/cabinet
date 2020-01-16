import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IPlateMap, PlateMap } from 'app/shared/model/plate-map.model';
import { PlateMapService } from './plate-map.service';
import { PlateMapComponent } from './plate-map.component';
import { PlateMapDetailComponent } from './plate-map-detail.component';
import { PlateMapUpdateComponent } from './plate-map-update.component';

@Injectable({ providedIn: 'root' })
export class PlateMapResolve implements Resolve<IPlateMap> {
  constructor(private service: PlateMapService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPlateMap> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((plateMap: HttpResponse<PlateMap>) => {
          if (plateMap.body) {
            return of(plateMap.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new PlateMap());
  }
}

export const plateMapRoute: Routes = [
  {
    path: '',
    component: PlateMapComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'PlateMaps'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: PlateMapDetailComponent,
    resolve: {
      plateMap: PlateMapResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'PlateMaps'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: PlateMapUpdateComponent,
    resolve: {
      plateMap: PlateMapResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'PlateMaps'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: PlateMapUpdateComponent,
    resolve: {
      plateMap: PlateMapResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'PlateMaps'
    },
    canActivate: [UserRouteAccessService]
  }
];

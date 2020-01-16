import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, Search } from 'app/shared/util/request-util';
import { IPlateMap } from 'app/shared/model/plate-map.model';

type EntityResponseType = HttpResponse<IPlateMap>;
type EntityArrayResponseType = HttpResponse<IPlateMap[]>;

@Injectable({ providedIn: 'root' })
export class PlateMapService {
  public resourceUrl = SERVER_API_URL + 'api/plate-maps';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/plate-maps';

  constructor(protected http: HttpClient) {}

  create(plateMap: IPlateMap): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(plateMap);
    return this.http
      .post<IPlateMap>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(plateMap: IPlateMap): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(plateMap);
    return this.http
      .put<IPlateMap>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IPlateMap>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPlateMap[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPlateMap[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  protected convertDateFromClient(plateMap: IPlateMap): IPlateMap {
    const copy: IPlateMap = Object.assign({}, plateMap, {
      lastModified: plateMap.lastModified && plateMap.lastModified.isValid() ? plateMap.lastModified.toJSON() : undefined
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.lastModified = res.body.lastModified ? moment(res.body.lastModified) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((plateMap: IPlateMap) => {
        plateMap.lastModified = plateMap.lastModified ? moment(plateMap.lastModified) : undefined;
      });
    }
    return res;
  }
}

import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { take, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { PlateMapService } from 'app/entities/plate-map/plate-map.service';
import { IPlateMap, PlateMap } from 'app/shared/model/plate-map.model';
import { Status } from 'app/shared/model/enumerations/status.model';

describe('Service Tests', () => {
  describe('PlateMap Service', () => {
    let injector: TestBed;
    let service: PlateMapService;
    let httpMock: HttpTestingController;
    let elemDefault: IPlateMap;
    let expectedResult: IPlateMap | IPlateMap[] | boolean | null;
    let currentDate: moment.Moment;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(PlateMapService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new PlateMap(0, Status.DRAFT, currentDate, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA');
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            lastModified: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );
        service
          .find(123)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a PlateMap', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            lastModified: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            lastModified: currentDate
          },
          returnedFromService
        );
        service
          .create(new PlateMap())
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp.body));
        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a PlateMap', () => {
        const returnedFromService = Object.assign(
          {
            status: 'BBBBBB',
            lastModified: currentDate.format(DATE_TIME_FORMAT),
            checksum: 'BBBBBB',
            activityName: 'BBBBBB',
            data: 'BBBBBB',
            numPlates: 1
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            lastModified: currentDate
          },
          returnedFromService
        );
        service
          .update(expected)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp.body));
        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of PlateMap', () => {
        const returnedFromService = Object.assign(
          {
            status: 'BBBBBB',
            lastModified: currentDate.format(DATE_TIME_FORMAT),
            checksum: 'BBBBBB',
            activityName: 'BBBBBB',
            data: 'BBBBBB',
            numPlates: 1
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            lastModified: currentDate
          },
          returnedFromService
        );
        service
          .query()
          .pipe(
            take(1),
            map(resp => resp.body)
          )
          .subscribe(body => (expectedResult = body));
        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a PlateMap', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});

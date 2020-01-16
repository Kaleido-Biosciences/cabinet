import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { CabinetTestModule } from '../../../test.module';
import { PlateMapComponent } from 'app/entities/plate-map/plate-map.component';
import { PlateMapService } from 'app/entities/plate-map/plate-map.service';
import { PlateMap } from 'app/shared/model/plate-map.model';

describe('Component Tests', () => {
  describe('PlateMap Management Component', () => {
    let comp: PlateMapComponent;
    let fixture: ComponentFixture<PlateMapComponent>;
    let service: PlateMapService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CabinetTestModule],
        declarations: [PlateMapComponent],
        providers: []
      })
        .overrideTemplate(PlateMapComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PlateMapComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(PlateMapService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new PlateMap(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.plateMaps && comp.plateMaps[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});

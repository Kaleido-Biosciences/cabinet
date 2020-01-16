import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CabinetTestModule } from '../../../test.module';
import { PlateMapDetailComponent } from 'app/entities/plate-map/plate-map-detail.component';
import { PlateMap } from 'app/shared/model/plate-map.model';

describe('Component Tests', () => {
  describe('PlateMap Management Detail Component', () => {
    let comp: PlateMapDetailComponent;
    let fixture: ComponentFixture<PlateMapDetailComponent>;
    const route = ({ data: of({ plateMap: new PlateMap(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CabinetTestModule],
        declarations: [PlateMapDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(PlateMapDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(PlateMapDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load plateMap on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.plateMap).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});

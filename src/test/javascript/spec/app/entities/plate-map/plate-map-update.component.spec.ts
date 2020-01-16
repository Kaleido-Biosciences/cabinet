import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { CabinetTestModule } from '../../../test.module';
import { PlateMapUpdateComponent } from 'app/entities/plate-map/plate-map-update.component';
import { PlateMapService } from 'app/entities/plate-map/plate-map.service';
import { PlateMap } from 'app/shared/model/plate-map.model';

describe('Component Tests', () => {
  describe('PlateMap Management Update Component', () => {
    let comp: PlateMapUpdateComponent;
    let fixture: ComponentFixture<PlateMapUpdateComponent>;
    let service: PlateMapService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [CabinetTestModule],
        declarations: [PlateMapUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(PlateMapUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PlateMapUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(PlateMapService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new PlateMap(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new PlateMap();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});

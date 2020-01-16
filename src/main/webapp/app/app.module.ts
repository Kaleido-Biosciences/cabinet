import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { CabinetSharedModule } from 'app/shared/shared.module';
import { CabinetCoreModule } from 'app/core/core.module';
import { CabinetAppRoutingModule } from './app-routing.module';
import { CabinetHomeModule } from './home/home.module';
import { CabinetEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';

@NgModule({
  imports: [
    BrowserModule,
    CabinetSharedModule,
    CabinetCoreModule,
    CabinetHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    CabinetEntityModule,
    CabinetAppRoutingModule
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [MainComponent]
})
export class CabinetAppModule {}

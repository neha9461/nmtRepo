// modules & services
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { QuillModule } from 'ngx-quill';
import { SharedModule } from '../shared/shared.module';
import { InMemoryWebApiModule } from 'angular-in-memory-web-api';
import { ArticleData } from '../mock/article-data';
import { AppRoutingModule } from './app-routing.module';
import { AuthGuard } from '../shared/service/helper/auth-guards';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';
import { MaterialModule } from '../shared/material.module';
import {ToasterModule, ToasterService} from 'angular5-toaster';
import { Ng4LoadingSpinnerModule } from 'ng4-loading-spinner';
import { BreadcrumbComponent } from './app.breadcrumbs';
import { RootComponent } from './root.component';

// components
import { LoginComponent } from './login/login.component';
import { AppComponent } from './app.component';
import { EditNoteComponent } from './edit-note/edit-note.component';
import { AddNoteComponent } from './add-note/add-note.component';
import { SignUpComponent } from './sign-up/sign-up.component';
import { ViewNoteComponent } from './view-note/view-note.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MainComponent } from './main/main.component';
import { LeftPanelComponent } from './left-panel/left-panel.component';
import { FooterComponent } from './footer/footer.component';
import { ListNoteComponent } from './list-note/list-note.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';


@NgModule({
  declarations: [
    AppComponent,
    BreadcrumbComponent,
    RootComponent,
    LoginComponent,
    AddNoteComponent,
    EditNoteComponent,
    SignUpComponent,
    FooterComponent,
    LeftPanelComponent,
    ListNoteComponent,
    MainComponent,
    ViewNoteComponent,
    ForgotPasswordComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    QuillModule,
    HttpClientModule ,
    HttpModule,
    SharedModule.forRoot(),
    InMemoryWebApiModule.forRoot(ArticleData, {
      passThruUnknownUrl: true
    }),
    AppRoutingModule,
    MaterialModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    ToasterModule,
    Ng4LoadingSpinnerModule.forRoot()
  ],
  entryComponents: [],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

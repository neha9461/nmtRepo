import { TestBed, async } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LeftPanelComponent } from './left-panel/left-panel.component';
import { RouterModule, Routes } from '@angular/router';
import { AppRoutingModule } from './app-routing.module';
import { MaterialModule } from '../shared/material.module';
import {ToasterModule, ToasterService} from 'angular5-toaster';
import { Ng4LoadingSpinnerModule } from 'ng4-loading-spinner';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { AddNoteComponent } from './add-note/add-note.component';
import { ListNoteComponent } from './list-note/list-note.component';
import { ViewNoteComponent } from './view-note/view-note.component';
import { EditNoteComponent } from './edit-note/edit-note.component';
import { SignUpComponent } from './sign-up/sign-up.component';
import { FooterComponent } from './footer/footer.component';
import { QuillModule } from 'ngx-quill';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientModule } from '@angular/common/http';
import { AuthenticationService } from '../shared/service/authentication/authentication.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MessageService } from '../shared/service/message/message';
import { RootComponent } from './root.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { BreadcrumbComponent } from './app.breadcrumbs';
describe('AppComponent', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports:[FormsModule,QuillModule, AppRoutingModule,ReactiveFormsModule, MaterialModule, ToasterModule, Ng4LoadingSpinnerModule.forRoot(), 
        RouterTestingModule, HttpClientModule, BrowserAnimationsModule ],
      declarations: [
        AppComponent, LeftPanelComponent, LoginComponent,
        ListNoteComponent, AddNoteComponent, ViewNoteComponent,
        EditNoteComponent, SignUpComponent, FooterComponent,
        RootComponent, ForgotPasswordComponent, BreadcrumbComponent
      ],
      providers: [ AuthenticationService, MessageService]
    }).compileComponents();
  }));
  it('should create the app', async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  }));
});

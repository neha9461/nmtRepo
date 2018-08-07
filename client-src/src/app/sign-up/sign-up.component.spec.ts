import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import {MaterialModule} from '../../shared/material.module';
import { UserService } from '../../shared/service/user/user.service';
import { HttpClientModule } from '@angular/common/http';
import { AuthenticationService } from '../../shared/service/authentication/authentication.service';
import { RouterTestingModule } from '@angular/router/testing';
import { ToasterService } from 'angular5-toaster';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SignUpComponent } from './sign-up.component';


describe('SignUpComponent', () => {
  let component: SignUpComponent;
  let fixture: ComponentFixture<SignUpComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers:[UserService, AuthenticationService, ToasterService],
      imports:[ReactiveFormsModule, MaterialModule, HttpClientModule, RouterTestingModule, BrowserAnimationsModule],
      declarations: [ SignUpComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SignUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

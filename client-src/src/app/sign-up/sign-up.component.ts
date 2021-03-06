import { Component, OnInit, ViewChild  } from '@angular/core';
import { UserService } from '../../shared/service/user/user.service';
import { Router } from '@angular/router';
import { User } from '../../shared/modals/user';
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ToasterService } from 'angular5-toaster';

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.scss']
})

export class SignUpComponent implements OnInit {

  users: User[];
  errorMessage: String;
  addUserName: String;
  user: FormGroup;
  get cpwd() {
    return this.user.get('confirmPassword');
   }

  constructor(
    private userService: UserService,
    private fb: FormBuilder,
    private router: Router,
    private toasterService: ToasterService
  ) {
  }

  ngOnInit() {
    this.user = this.fb.group({
      firstName: ['', [Validators.required]],
      lastName: ['', Validators.required],
      password: ['', Validators.required],
      email: ['', Validators.email],
      confirmPassword: ['', Validators.required],
    }, {validator: this.validateConfirmPassword('password', 'confirmPassword')});

  }

  validateConfirmPassword(passwordKey: string, passwordConfirmationKey: string) {
    return (group: FormGroup) => {
      const passwordInput = group.controls[passwordKey],
          passwordConfirmationInput = group.controls[passwordConfirmationKey];
      if (passwordInput.value !== passwordConfirmationInput.value) {
        return passwordConfirmationInput.setErrors({notEquivalent: true});
      }
        return passwordConfirmationInput.setErrors(null);
    };
  }

  onSubmit({value, valid}: {value: any, valid: boolean }) {
    delete value.confirmPassword;
    this.userService.createUser(value)
    .subscribe( data => {
      console.log('hello');
            this.toasterService.pop('success', '', data.success.message);
            this.onCancle();
         },
        data => {
            this.toasterService.pop('error', '', data.error.success.message);
        }
      );

  }

  onCancle() {
    this.router.navigate(['/login']);
  }


}

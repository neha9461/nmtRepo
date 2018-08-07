import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AddNoteComponent } from './add-note/add-note.component';
import { EditNoteComponent } from './edit-note/edit-note.component';
import { ViewNoteComponent } from './view-note/view-note.component';
import { LoginComponent } from './login/login.component';
import { SignUpComponent } from './sign-up/sign-up.component';
import { AuthGuard } from '../shared/service/helper/auth-guards';
import { ListNoteComponent } from './list-note/list-note.component';
import { RootComponent } from './root.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';

const routes:  Routes = [
    {
      path: '',
      component: RootComponent,
      children : [

        {
          path: 'login',
          component: LoginComponent,
          data: { title: 'Login' }
        },
        {
          path: 'signup',
          component: SignUpComponent,
          data: { title: 'SignUp' }
        },
        {
          path: 'forgot-password',
          component: ForgotPasswordComponent,
          data: { title: 'Forgot Password', breadcrumb: 'Forgot Password' }
        },
        {
          path: 'notes',
          canActivate: [AuthGuard],
          component: ListNoteComponent,
          data: { breadcrumb: 'Notes' },
          children : [
            {
              path: 'edit/:id',
              component: EditNoteComponent,
              data: { breadcrumb: 'Edit Note' }
            },
            {
              path: 'add',
              component: AddNoteComponent,
              data: { breadcrumb: 'Add Note' }
              },
            {
              path: 'detail/:id',
              component: ViewNoteComponent,
              data: { breadcrumb: 'Note Details' }
            }
          ]
        }
      ]
    }
  ];


@NgModule({
  imports: [RouterModule.forRoot(routes, {onSameUrlNavigation: 'reload', enableTracing: false })],
  exports: [RouterModule]
})
export class AppRoutingModule { }

import { Component, OnInit } from '@angular/core';
import { NotesService } from '../../shared/service/notes/notes.service';
import { Router } from '@angular/router';
import { KnowledgeBaseArticle } from '../../shared/modals/knowledge-base-article';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../shared/service/user/user.service';
import { AuthenticationService } from '../../shared/service/authentication/authentication.service';
import { ToasterService } from 'angular5-toaster';
import { saveAs } from 'file-saver/FileSaver';

@Component({
  selector: 'app-add-kb-article',
  templateUrl: './add-note.component.html',
  styleUrls: ['./add-note.component.scss']
})
export class AddNoteComponent implements OnInit {

  notes: KnowledgeBaseArticle[];
  errorMessage: String;
  noteTitle: String;
  note: FormGroup;
  errorMsg: string;

  constructor(
    private noteService: NotesService,
    private fb: FormBuilder,
    private router: Router,
    private userService: UserService,
    private auth: AuthenticationService,
    private toasterService: ToasterService
  ) {
    this.errorMsg = '';
  }

  ngOnInit() {

     this.note = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(2)]],
     description: ['', Validators.required],
     createdBy: ''
    });

    this.note.patchValue({
      createdBy: this.auth.getUserId(),
    });

  }


  onSubmit({value, valid}: {value: KnowledgeBaseArticle, valid: boolean }) {
    this.noteService.createNote(value)
    .subscribe( data => {
            this.toasterService.pop('success', '', data.success.message);
            this.onCancle();
         },
         error => {
             this.toasterService.pop('error', '', error.error.success.message);
         }
        );

  }

  onCancle() {
    this.router.navigateByUrl('/notes');
  }

}

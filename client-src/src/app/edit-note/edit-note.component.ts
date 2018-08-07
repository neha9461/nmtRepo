import { Component, OnInit } from '@angular/core';
import { NotesService } from '../../shared/service/notes/notes.service';
import { Router } from '@angular/router';
import { KnowledgeBaseArticle , UpdateKnowledgeBaseArticle} from '../../shared/modals/knowledge-base-article';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../shared/service/user/user.service';
import { AuthenticationService } from '../../shared/service/authentication/authentication.service';
import { ToasterService } from 'angular5-toaster';
import {ActivatedRoute} from '@angular/router';
import { saveAs } from 'file-saver/FileSaver';
import { Http, Headers } from '@angular/http';
import 'rxjs/add/operator/toPromise';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-edit-note',
  templateUrl: './edit-note.component.html',
  styleUrls: ['./edit-note.component.scss']
})
export class EditNoteComponent implements OnInit {

  notes: KnowledgeBaseArticle[];
  note: FormGroup;
  noteId: any;
  errorMsg: string;

  constructor(
    private noteService: NotesService,
    private fb: FormBuilder,
    private router: Router,
    private userService: UserService,
    private auth: AuthenticationService,
    private toasterService: ToasterService,
    private activatedRoute: ActivatedRoute,
    private http: Http,
      ) {

    this.errorMsg = '';
    this.activatedRoute.params.subscribe((params: any) => {
      this.noteId = params['id'];
      if (this.noteId) {
        this.noteService.reteriveNoteById(this.noteId).subscribe((data: any) => {
          console.log(data);
          if (data.hasOwnProperty('note')) {
            data = data.note;
          }
          const pipe = new DatePipe('en-US');

          this.note.setValue({
            title: data.title,
            description: data.description,
            createdByUser: data.createdBy.firstName + ' ' + data.createdBy.lastName,
            lastModified: pipe.transform(data.lastModifiedTime, 'MM/dd/yyyy hh:mm a')

          });
        });
      }
    });

  }

  ngOnInit() {

    this.note = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(2)]],
     description: ['', Validators.required],
     createdByUser: '',
     lastModified: ''
    });

  }


  onSubmit({value, valid}: {value: any, valid: boolean }) {
    delete value.createdByUser;
    delete value.lastModified;


    value = <UpdateKnowledgeBaseArticle> value;
    this.noteService.updateNote(this.noteId, value)
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

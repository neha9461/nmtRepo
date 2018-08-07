import { Component, OnInit } from '@angular/core';
import { NotesService } from '../../shared/service/notes/notes.service';
import { Router } from '@angular/router';
import {ActivatedRoute} from '@angular/router';
import { AuthenticationService } from '../../shared/service/authentication/authentication.service';
import { saveAs } from 'file-saver/FileSaver';
import { Http, Headers } from '@angular/http';


@Component({
  selector: 'app-view-note',
  templateUrl: './view-note.component.html',
  styleUrls: ['./view-note.component.scss']
})
export class ViewNoteComponent implements OnInit {

  noteId: any;
  note: any;
  currentUserId: any;

  constructor(private noteService: NotesService,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              private auth: AuthenticationService,

  ) {
    this.currentUserId = this.auth.getUserId();
  }

  onTapNavigation(route, param) {
      if (param) {
          this.router.navigate([route, param], {relativeTo: this.activatedRoute} );
      } else {
          this.router.navigate([route], {relativeTo: this.activatedRoute} );
      }
  }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: any) => {
      this.noteId = params['id'];
      if (this.noteId) {
      this.noteService.reteriveNoteById(this.noteId).subscribe((data: any) => {
          if (data.hasOwnProperty('note')) {
            data = data.note;
          }
          this.note = data;
         });
      }
    });
  }
}

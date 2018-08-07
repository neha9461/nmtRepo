import {Component, ViewChild, OnInit, Inject, OnDestroy} from '@angular/core';
import {MatPaginator, MatSort, MatTableDataSource, MatDialog, MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import { Router, ActivatedRoute, NavigationEnd } from '@angular/router';
import { ToasterService } from 'angular5-toaster';
import { NotesService } from '../../shared/service/notes/notes.service';
import { Aritcles } from '../../shared/modals/knowledge-base-article';
import { AuthenticationService } from '../../shared/service/authentication/authentication.service';

/**
 * @title Data table with sorting, pagination, and filtering.
 */
@Component({
  selector: 'app-content',
  templateUrl: './list-note.component.html',
  styleUrls: ['./list-note.component.scss']
})

export class ListNoteComponent implements OnInit, OnDestroy {
  displayedColumns = ['title', 'actions'];
  dataSource: MatTableDataSource<Aritcles>;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  noteList: any;
  filterList: any = [];
  currentUserId: any;
  totalNumberItems: Number;

  pageNo = 0;

  navigationSubscription;
  compType = '';

  constructor(
    private router: Router,
    private aRoute: ActivatedRoute,
    private toasterService: ToasterService,
    private noteService: NotesService,
    public dialog: MatDialog,
    private auth: AuthenticationService
    ) {
            this.currentUserId = this.auth.getUserId();

        this.navigationSubscription = this.router.events.subscribe((e: any) => {
            if (e instanceof NavigationEnd) {
              if (this.router.url === '/notes') {
                this.compType = 'list';
                this.initData();
              }
            }
        });
    }

    ngOnDestroy() {
        if (this.navigationSubscription) {
           this.navigationSubscription.unsubscribe();
        }
    }

    initData() {
        this.getNoteList(0);
    }

    ngOnInit() {
    }

    onClickviewAll() {
        this.initData();
    }

    onClickAssignedCreatedList(fromWhich) {
        this.getNoteList(0);
    }


    onPaginateChange(pageInfo) {
        this.pageNo = pageInfo.pageIndex;
        this.getNoteList(this.pageNo);
    }

    onTapDelete(noteId) {
        if (confirm('Would you like to delete the Note?')) {
            this.noteService.deleteNote(noteId).subscribe(
                data => {
                    this.toasterService.pop('success', '', data.success.message);
                    this.getNoteList(this.pageNo);
                },
                error => {
                    this.toasterService.pop('error', '', error.success.message);
                });
            }
    }

    getNoteList(pageNum) {
        let queryParam = '?page=' + pageNum;

        console.log(queryParam);
        this.noteService.listNotesqs(queryParam)
        .subscribe(
            data => {
                this.noteList = JSON.parse(JSON.stringify(data)).content;
                console.log(this.noteList);
                this.totalNumberItems = JSON.parse(JSON.stringify(data)).totalElements;
                this.createData(this.noteList);
            },
            error => {
                this.toasterService.pop('error', '', error.error.message);
            });
    }

    onTapNavigation(route, param) {
        this.compType = route;
        if (param) {
            this.router.navigate([route, param], {relativeTo: this.aRoute} );
        } else {
            this.router.navigate([route], {relativeTo: this.aRoute} );
        }
    }



    createData(data) {
        const notes: Aritcles[] = [];
        for (let i = 0; i < data.length; i++) {
          notes.push(this.createNewUser(data[i]));
        }

        console.log(notes);
        this.filterList = notes;
        this.dataSource = new MatTableDataSource(notes);
    }

  createNewUser(item: any): any {
    return {
        id: item.id,
        title: item.title,
        content: item,
        size: 20,
        totalPages: 1,
        totalElements: 20,
        numberOfElements: 20
    };
   }
}

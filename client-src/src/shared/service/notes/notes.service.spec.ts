import { TestBed, async } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { NotesService } from './notes.service';
import { Observable } from 'rxjs/Observable';
import { environment } from '../../../environments/environment';
import { Http, HttpModule } from '@angular/http';

let noteURL = environment.API_ENDPOINT + 'notes';


  describe('NotesService', () => {

    const mockArticleResponse = {
      "types": [
          {
              "id": "11",
              "type": "string"
          }
      ]
  };
    
    let notesService: NotesService;
    let httpMock: HttpTestingController;

    beforeEach(() => {

      TestBed.configureTestingModule({
        imports: [HttpModule , HttpClientTestingModule],
        providers: [NotesService]
      });

      notesService = TestBed.get(NotesService);
      httpMock = TestBed.get(HttpTestingController);
    });

    it('ListNotes', async(() => {

      let actualKnowledgeBaseContent: object[] = [];
      notesService.listNotes().subscribe((kb: object[]) => {
        actualKnowledgeBaseContent = kb;
      });

      const req = httpMock.expectOne(noteURL);
      expect(req.request.method).toBe('GET');
      req.flush(mockArticleResponse);
      
    }));
  });


import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { KnowledgeBaseArticle, UpdateKnowledgeBaseArticle } from '../.././modals/knowledge-base-article';
import { of } from 'rxjs/observable/of';
import { catchError, map, tap } from 'rxjs/operators';
import 'rxjs/add/operator/catch';
import { environment } from '../../../environments/environment';
import { Http, Response, RequestOptions, ResponseContentType } from '@angular/http';


/**
 * This class provides the Knowledgebase content service with methods to manage.
 */

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable()
export class NotesService {

  types: any[];
  isPending;
  /**
   * Creates a new KnowledgeBaseContentervice with the injected HttpClient.
   * @param {HttpClient} http - The injected HttpClient.
   * @constructor
   */
  private apiUrl = environment.API_ENDPOINT + 'notes';  // URL to web api environment.API_ENDPOINT

  constructor(private http: HttpClient, private httpMain: Http) { }

  /**
   * Returns an Observable for the HTTP GET request for the JSON resource.
   * @return {KnowledgeBaseArticle[]} The Observable for the HTTP request.
   */

  listNotesqs (queryParams): Observable<any[]> {
    return this.http.get(this.apiUrl + '' + queryParams)
    .catch(this.handleErrorObservable);
  }

  listNotes (): Observable<any[]> {
    return this.http.get(this.apiUrl )
      .catch(this.handleErrorObservable);
  }


  /**
   * Returns an Observable for the HTTP POST request for the JSON resource.
   * @return {any} The Observable for the HTTP request.
   */

  createNote (noteInfo: KnowledgeBaseArticle): Observable<any> {
        return this.http.post(this.apiUrl, {'note': noteInfo}, httpOptions)
                   .catch(this.handleErrorObservable);
}

  /**
   * Returns an Observable for the HTTP PUT request for the JSON resource.
   * @return {any} The Observable for the HTTP request.
   */

  updateNote (id: string, noteInfo: UpdateKnowledgeBaseArticle): Observable<any> {
    return this.http.put(this.apiUrl + '/' + id, {'note': noteInfo}, httpOptions)
               .catch(this.handleErrorObservable);
  }

  /**
   * Returns an Observable for the HTTP GET request for the JSON resource.
   * @return {any} The Observable for the HTTP request.
   */

  reteriveNoteById(noteId: Number): Observable<any> {
    return this.http.get(this.apiUrl + '/' + noteId).catch(this.handleErrorObservable);
}

  /**
   * Returns an Observable for the HTTP DELETE request for the JSON resource.
   * @return {UserService} The Observable for the HTTP request.
   */

  deleteNote (id): Observable<any> {
    return this.http.delete(this.apiUrl + '/' + id,  httpOptions)
               .catch(this.handleErrorObservable);
  }


/**
    * Handle HTTP error
    */
  private handleErrorObservable (error: Response | any) {
    console.error(error.success || error);
    return Observable.throw(error.success || error);
  }
}


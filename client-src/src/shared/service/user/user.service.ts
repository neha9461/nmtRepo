import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { User } from '../.././modals/user';
import { of } from 'rxjs/observable/of';
import { catchError, map, tap } from 'rxjs/operators';
import 'rxjs/add/operator/catch';
import { environment } from '../../../environments/environment';
import { AuthenticationService } from '../authentication/authentication.service';

/**
 * This class provides the User service with methods to manage.
 */


@Injectable()
export class UserService {
  httpOptions: any;
  roles: any[];
  /**
   * Creates a new KnowledgeBaseContentervice with the injected HttpClient.
   * @param {HttpClient} http - The injected HttpClient.
   * @constructor
   */
  private apiUrl = environment.API_ENDPOINT + 'users';  // URL to web api environment.API_ENDPOINT

  constructor(private http: HttpClient, private auth: AuthenticationService) {
      const token = auth.getAccessToken();
      this.httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json'})
    };
  }

  /**
   * Returns an Observable for the HTTP GET request for the JSON resource.
   * @return {User[]} The Observable for the HTTP request.
   */

   listUser (queryParams): Observable<any> {
    return this.http.get(this.apiUrl + '' + queryParams)
    .catch(this.handleErrorObservable);
  }
  /**
   * Returns an Observable for the HTTP POST request for the JSON resource.
   * @return {User} The Observable for the HTTP request.
   */

  createUser (userInfo: User): Observable<any> {
    console.log('hello service',userInfo);

    return this.http.post(this.apiUrl, {'user': userInfo}, this.httpOptions)
                   .catch(this.handleErrorObservable);
  }
  /**
   * Returns an Observable for the HTTP POST request for the JSON resource.
   * @return {UserService} The Observable for the HTTP request.
   */

  reteriveUserById(userId: String): Observable<any> {
    return this.http.get(this.apiUrl + '/' + userId, this.httpOptions).catch(this.handleErrorObservable);
}
/**
    * Handle HTTP error
    */
  private handleErrorObservable (error: Response | any) {
    console.error(error.success || error);
    return Observable.throw(error.success || error);
  }
}


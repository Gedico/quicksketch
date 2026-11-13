import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  const clonedReq = req.clone({
    withCredentials: true
  });

  return next(clonedReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Se una chiamata protetta (escluso il check iniziale /auth/me) riceve 401 o 403,
      // la sessione server non è più valida: riallineiamo subito lo stato UI
      if ((error.status === 401 || error.status === 403) && !req.url.includes('/api/auth/')) {
        authService.isAuthenticatedSignal.set(false);
      }
      return throwError(() => error);
    })
  );
};
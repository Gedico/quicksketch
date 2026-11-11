import { HttpInterceptorFn } from '@angular/common/http';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  // Istruisce il browser ad allegare automaticamente i Cookie HttpOnly a ogni richiesta HTTP
  const clonedReq = req.clone({
    withCredentials: true
  });
  
  return next(clonedReq);
};
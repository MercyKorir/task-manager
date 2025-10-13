import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth';
import { ToastService } from '../services/toast';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const toastService = inject(ToastService);
  const token = authService.getToken();

  if (token) {
    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(clonedRequest).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 || error.status === 403) {
          if (error.status === 401) {
            toastService.error('Session expired. Please login again.');
          } else if (error.status === 403) {
            toastService.error('Access denied. You don\'t have permission.');
          }
          authService.logout();
        }
        return throwError(() => error);
      })
    );
  }

  return next(req);
};

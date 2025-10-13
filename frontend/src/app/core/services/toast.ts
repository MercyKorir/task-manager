import { Injectable, signal } from '@angular/core';
import { Toast, ToastType } from '../models/toast';

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private toastsSignal = signal<Toast[]>([]);
  public toasts = this.toastsSignal.asReadonly();

  private defaultDuration = 3000;

  success(message: string, duration?: number): void {
    this.show(ToastType.SUCCESS, message, duration);
  }

  error(message: string, duration?: number): void {
    this.show(ToastType.ERROR, message, duration || 5000);
  }

  info(message: string, duration?: number): void {
    this.show(ToastType.INFO, message, duration);
  }

  warning(message: string, duration?: number): void {
    this.show(ToastType.WARNING, message, duration);
  }

  private show(type: ToastType, message: string, duration?: number): void {
    const toast: Toast = {
      id: this.generateId(),
      type,
      message,
      duration: duration || this.defaultDuration,
      autoDismiss: true
    };

    this.toastsSignal.update(toasts => [...toasts, toast]);

    if (toast.autoDismiss) {
      setTimeout(() => {
        this.dismiss(toast.id);
      }, toast.duration);
    }
  }

  dismiss(id: string): void {
    this.toastsSignal.update(toasts =>
      toasts.filter(toast => toast.id !== id)
    );
  }

  clear(): void {
    this.toastsSignal.set([]);
  }

  private generateId(): string {
    return `toast-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
  }
}

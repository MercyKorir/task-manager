import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../core/services/toast';
import { ToastType } from '../../../core/models/toast';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast-container.html',
  styleUrl: './toast-container.css'
})
export class ToastContainerComponent {
  private toastService = inject(ToastService);
  toasts = this.toastService.toasts;
  ToastType = ToastType;

  dismiss(id: string): void {
    this.toastService.dismiss(id);
  }

  getIcon(type: ToastType): string {
    switch (type) {
      case ToastType.SUCCESS:
        return '✓';
      case ToastType.ERROR:
        return '✕';
      case ToastType.INFO:
        return 'ℹ';
      case ToastType.WARNING:
        return '⚠';
      default:
        return 'ℹ';
    }
  }

}

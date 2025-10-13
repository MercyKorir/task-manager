export enum ToastType {
  SUCCESS = 'success',
  ERROR = 'error',
  INFO = 'info',
  WARNING = 'warning'
}

export interface Toast {
  id: string;
  type: ToastType;
  message: string;
  duration?: number;
  autoDismiss?: boolean;
}

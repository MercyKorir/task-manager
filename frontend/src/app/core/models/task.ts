export enum TaskStatus {
  PENDING = 'PENDING',
  COMPLETED = 'COMPLETED'
}

export interface Task {
  id?: number;
  title: string;
  description?: string;
  status: TaskStatus;
  userId?: number;
}

export interface TaskCreateRequest {
  title: string;
  description?: string;
  status: TaskStatus;
}

export interface TaskUpdateRequest {
  title?: string;
  description?: string;
  status?: TaskStatus;
}

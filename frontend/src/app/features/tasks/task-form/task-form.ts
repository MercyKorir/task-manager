import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../../core/services/task';
import { Task, TaskStatus, TaskCreateRequest, TaskUpdateRequest } from '../../../core/models/task';

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './task-form.html',
  styleUrl: './task-form.css'
})
export class TaskFormComponent implements OnInit {
  @Input() task: Task | null = null;
  @Output() save = new EventEmitter<Task>();
  @Output() cancel = new EventEmitter<void>();

  formData = {
    title: '',
    description: '',
    status: TaskStatus.PENDING
  };

  isLoading = false;
  errorMessage = '';
  TaskStatus = TaskStatus;

  constructor(private taskService: TaskService) {}

  ngOnInit(): void {
    if (this.task) {
      // Edit mode - populate form with existing task data
      this.formData = {
        title: this.task.title,
        description: this.task.description || '',
        status: this.task.status
      };
    }
  }

  get isEditMode(): boolean {
    return this.task !== null;
  }

  get modalTitle(): string {
    return this.isEditMode ? 'Edit Task' : 'Create New Task';
  }

  get submitButtonText(): string {
    return this.isEditMode ? 'Update Task' : 'Create Task';
  }

  onSubmit(): void {
    if (!this.formData.title.trim()) {
      this.errorMessage = 'Task title is required';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    if (this.isEditMode) {
      this.updateTask();
    } else {
      this.createTask();
    }
  }

  createTask(): void {
    const request: TaskCreateRequest = {
      title: this.formData.title.trim(),
      description: this.formData.description.trim() || undefined,
      status: this.formData.status
    };

    this.taskService.createTask(request).subscribe({
      next: (task) => {
        this.save.emit(task);
      },
      error: (error) => {
        console.error('Failed to create task', error);
        this.errorMessage = 'Failed to create task. Please try again.';
        this.isLoading = false;
      }
    });
  }

  updateTask(): void {
    const request: TaskUpdateRequest = {
      title: this.formData.title.trim(),
      description: this.formData.description.trim() || undefined,
      status: this.formData.status
    };

    this.taskService.updateTask(this.task!.id!, request).subscribe({
      next: (task) => {
        this.save.emit(task);
      },
      error: (error) => {
        console.error('Failed to update task', error);
        this.errorMessage = 'Failed to update task. Please try again.';
        this.isLoading = false;
      }
    });
  }

  onCancel(): void {
    this.cancel.emit();
  }

  onBackdropClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('modal-backdrop')) {
      this.onCancel();
    }
  }
}

import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Task, TaskStatus } from '../../../core/models/task';

@Component({
  selector: 'app-task-item',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './task-item.html',
  styleUrl: './task-item.css'
})
export class TaskItemComponent {
  @Input() task!: Task;
  @Output() edit = new EventEmitter<Task>();
  @Output() delete = new EventEmitter<number>();
  @Output() toggleStatus = new EventEmitter<Task>();

  TaskStatus = TaskStatus;

  onEdit(): void {
    this.edit.emit(this.task);
  }

  onDelete(): void {
    this.delete.emit(this.task.id!);
  }

  onToggleStatus(): void {
    this.toggleStatus.emit(this.task);
  }

  get statusClass(): string {
    return this.task.status === TaskStatus.COMPLETED ? 'completed' : 'pending';
  }

  get statusIcon(): string {
    return this.task.status === TaskStatus.COMPLETED ? '✅' : '⏳';
  }

  get statusText(): string {
    return this.task.status === TaskStatus.COMPLETED ? 'Completed' : 'Pending';
  }
}

import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule, Mailbox} from 'lucide-angular';
import { TaskService } from '../../../core/services/task';
import { ToastService } from '../../../core/services/toast';
import { Task, TaskStatus } from '../../../core/models/task';
import { TaskItemComponent } from '../task-item/task-item';
import { TaskFormComponent } from '../task-form/task-form';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule, TaskItemComponent, TaskFormComponent, LucideAngularModule],
  templateUrl: './task-list.html',
  styleUrl: './task-list.css'
})
export class TaskListComponent implements OnInit {
  tasks: Task[] = [];
  filteredTasks: Task[] = [];
  isLoading = false;
  showTaskForm = false;
  selectedTask: Task | null = null;
  filterStatus: 'ALL' | TaskStatus = 'ALL';

  readonly MailBox = Mailbox;

  TaskStatus = TaskStatus;

  constructor(
    private taskService: TaskService,
    private cdr: ChangeDetectorRef,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.isLoading = true;

    this.taskService.getAllTasks().subscribe({
      next: (tasks) => {

        this.tasks = tasks;
        this.applyFilter();
        this.isLoading = false;
        this.cdr.detectChanges();

      },
      error: (error) => {
        console.error('Failed to load tasks', error);
        this.toastService.error('Failed to load tasks. Please try again.');
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  openCreateTaskForm(): void {
    this.selectedTask = null;
    this.showTaskForm = true;
  }

  openEditTaskForm(task: Task): void {
    this.selectedTask = task;
    this.showTaskForm = true;
  }

  closeTaskForm(): void {
    this.showTaskForm = false;
    this.selectedTask = null;
  }

  onTaskSaved(task: Task): void {
    const isEdit = this.selectedTask !== null;
    this.closeTaskForm();
    this.loadTasks();

    if (isEdit) {
      this.toastService.success('Task updated successfully!');
    } else {
      this.toastService.success('Task created successfully!');
    }
  }

  onTaskDeleted(taskId: number): void {
    if (confirm('Are you sure you want to delete this task?')) {
      this.taskService.deleteTask(taskId).subscribe({
        next: () => {
          this.toastService.success('Task deleted successfully');
          this.loadTasks();
        },
        error: (error) => {
          console.error('Failed to delete task', error);
          this.toastService.error('Failed to delete task. Please try again.');
        }
      });
    }
  }

  onTaskStatusToggle(task: Task): void {
    const updatedStatus = task.status === TaskStatus.PENDING
      ? TaskStatus.COMPLETED
      : TaskStatus.PENDING;

    this.taskService.updateTask(task.id!, { status: updatedStatus }).subscribe({
      next: () => {
        const message = updatedStatus === TaskStatus.COMPLETED
          ? 'Task marked as completed!'
          : 'Task marked as pending!';
        this.toastService.success(message);
        this.loadTasks();
      },
      error: (error) => {
        console.error('Failed to update task status', error);
        this.toastService.error('Failed to update task status. Please try again.');
      }
    });
  }

  setFilter(status: 'ALL' | TaskStatus): void {
    this.filterStatus = status;
    this.applyFilter();
  }

  applyFilter(): void {
    if (this.filterStatus === 'ALL') {
      this.filteredTasks = this.tasks;
    } else {
      this.filteredTasks = this.tasks.filter(task => task.status === this.filterStatus);
    }

  }

  get pendingCount(): number {
    return this.tasks.filter(t => t.status === TaskStatus.PENDING).length;
  }

  get completedCount(): number {
    return this.tasks.filter(t => t.status === TaskStatus.COMPLETED).length;
  }
}

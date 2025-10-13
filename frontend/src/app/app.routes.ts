import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login';
import { RegisterComponent } from './features/auth/register/register';
import { TaskListComponent } from './features/tasks/task-list/task-list';
import { authGuard } from './core/guards/auth-guard';

export const routes: Routes = [
  {path: '', redirectTo: '/tasks', pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'tasks', component: TaskListComponent, canActivate: [authGuard]},
  {path: '**', redirectTo: '/tasks'}
];

import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './shared/components/navbar/navbar';
import { ToastContainerComponent } from './shared/components/toast-container/toast-container';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, ToastContainerComponent],
  template: `
  <app-navbar></app-navbar>
  <router-outlet></router-outlet>
  <app-toast-container></app-toast-container>
  `,
  styles: []
})
export class App {
  protected readonly title = signal('frontend');
}

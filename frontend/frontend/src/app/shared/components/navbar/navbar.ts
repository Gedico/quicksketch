import { Component, ElementRef, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss'
})
export class NavbarComponent {
  isDropdownOpen = false;

  constructor(
    public authService: AuthService,
    private router: Router,
    private eRef: ElementRef
  ) {}

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  closeDropdown(): void {
    this.isDropdownOpen = false;
  }

  goToGallery(): void {
    this.closeDropdown();
    if (this.router.url.split('#')[0] === '/') {
      document.getElementById('gallery-section')?.scrollIntoView({ behavior: 'smooth' });
    } else {
      this.router.navigate(['/'], { fragment: 'gallery-section' });
    }
  }

  onLogout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.closeDropdown();
        this.router.navigate(['/login']);
      },
      error: () => {
        this.authService.isAuthenticatedSignal.set(false);
        this.closeDropdown();
        this.router.navigate(['/login']);
      }
    });
  }

  @HostListener('document:click', ['$event'])
  clickOut(event: Event): void {
    if (!this.eRef.nativeElement.contains(event.target)) {
      this.closeDropdown();
    }
  }
}
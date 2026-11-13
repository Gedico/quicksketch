import { Component, ElementRef, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth';
import { RouterLink, RouterLinkActive } from '@angular/router';

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

  onLogout(): void {
    this.authService.logout();
    this.closeDropdown();
    this.router.navigate(['/login']);
  }

  // Chiude il menu a tendina se si clicca in un punto qualsiasi fuori dal componente
  @HostListener('document:click', ['$event'])
  clickOut(event: Event): void {
    if (!this.eRef.nativeElement.contains(event.target)) {
      this.closeDropdown();
    }
  }
}

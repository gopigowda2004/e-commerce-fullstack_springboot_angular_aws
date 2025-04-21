import { Component, OnInit, inject } from '@angular/core';
import { CartService } from '../../services/cart.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../services/order.service';

@Component({
  selector: 'app-order-summary',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './order-summary.component.html',
  styleUrls: ['./order-summary.component.css']
})
export class OrderSummaryComponent implements OnInit {
  private cartService = inject(CartService);
  private orderService = inject(OrderService);
  private router = inject(Router);

  cart: any = {};
  loading = true;
  address: string = '';
  addresses: any[] = [];
  selectedAddressId: number | null = null;
  totalPages = 0;
  currentPage = 0;
  pageSize = 10;

  paymentMethod = 'cash'; // default
  pgName = '';
  pgPaymentId = '';
  pgStatus = '';
  pgResponseMessage = '';


  constructor() {}

  ngOnInit(): void {
    this.loadAddresses();

    // Fetch cart
    this.cartService.getCart().subscribe({
      next: (res) => {
        this.cart = res;
        this.loading = false;
      },
      error: () => {
        alert('Failed to load cart.');
        this.loading = false;
      }
    });
  }
  

  placeOrder() {
    if (!this.selectedAddressId) {
      return alert('Please select an address.');
    }
  
    let orderData: any = {
      addressId: this.selectedAddressId,
      paymentMethod: this.paymentMethod
    };
  
    if (this.paymentMethod === 'online') {
      orderData.pgName = this.pgName;
      orderData.pgPaymentId = this.pgPaymentId;
      orderData.pgStatus = this.pgStatus;
      orderData.pgResponseMessage = this.pgResponseMessage;
    }
  
    this.orderService.placeOrder(this.paymentMethod, orderData).subscribe({
      next: () => {
        alert('✅ Order placed successfully!');
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.error('❌ Order failed:', err);
        alert(err.error?.message || 'Order placement failed.');
      }
    });
  }
  

  loadAddresses(page = 0) {
    this.orderService.getCurrentUserAddresses(page, this.pageSize).subscribe({
      next: (res: any) => {
        this.addresses = res.content;
        this.totalPages = res.totalPages;
        this.currentPage = res.pageNumber;
  
        if (this.addresses.length > 0) {
          this.selectedAddressId = this.addresses[0].addressId;
        }
      },
      error: (err) => {
        console.error('Failed to load addresses', err);
      }
    });
  }
  
  
}

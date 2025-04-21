import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { SignupComponent } from './auth/signup/signup.component';
import { ProductListComponent } from './products/product-list/product-list.component';
import { ProductCreateComponent } from './products/product-create/product-create.component';
import { CartListComponent } from './cart/cart-list/cart-list.component';
import { OrderSummaryComponent } from './orders/order-summary/order-summary.component';
import { AuthGuard } from './shared/auth.guard';
import { AppComponent } from './app.component';
import { MyOrdersComponent } from './orders/my-orders/my-orders.component';

export const  routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: 'login', component: LoginComponent },
    { path: 'signup', component: SignupComponent },
    { path: 'products', component: ProductListComponent },
    { path: 'products/create', component: ProductCreateComponent },
    { path: 'cart', component: CartListComponent },
    { path: 'orders', component: OrderSummaryComponent },
    { path: 'my-orders', component: MyOrdersComponent },
    // { path: 'products', component: ProductListComponent, canActivate: [AuthGuard] },

  ];
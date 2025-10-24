import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// Import des vues
import Home from '@/views/Home.vue'
import Login from '@/views/auth/Login.vue'
import Register from '@/views/auth/Register.vue'

// Layouts
import CustomerLayout from '@/views/customer/CustomerLayout.vue'
import SellerLayout from '@/views/seller/SellerLayout.vue'
import SupplierLayout from '@/views/supplier/SupplierLayout.vue'
import DeliveryLayout from '@/views/delivery/DeliveryLayout.vue'
import WarehouseLayout from '@/views/warehouse/WarehouseLayout.vue'
import AdminLayout from '@/views/admin/AdminLayout.vue'

// Vues client
import CustomerDashboard from '@/views/customer/Dashboard.vue'
import CustomerProducts from '@/views/customer/Products.vue'
import CustomerProductDetail from '@/views/customer/ProductDetail.vue'
import CustomerCart from '@/views/customer/Cart.vue'
import CustomerCheckout from '@/views/customer/Checkout.vue'
import CustomerOrders from '@/views/customer/Orders.vue'
import CustomerOrderDetail from '@/views/customer/OrderDetail.vue'
import CustomerProfile from '@/views/customer/Profile.vue'
import CustomerCategories from '@/views/customer/Categories.vue'
import CustomerFarmers from '@/views/customer/Farmers.vue'

// Vues vendeur
import SellerDashboard from '@/views/seller/Dashboard.vue'
import SellerProducts from '@/views/seller/Products.vue'
import SellerOrders from '@/views/seller/Orders.vue'
import SellerReports from '@/views/seller/Reports.vue'
import SellerProfile from '@/views/seller/Profile.vue'

// Vues fournisseur
import SupplierDashboard from '@/views/supplier/Dashboard.vue'
import SupplierProducts from '@/views/supplier/Products.vue'
import SupplierAddProduct from '@/views/supplier/AddProduct.vue'
import SupplierProductList from '@/views/supplier/SupplierProducts.vue'
import SupplierEditProduct from '@/views/supplier/EditProduct.vue'
import SupplierOrders from '@/views/supplier/Orders.vue'
import SupplierProfile from '@/views/supplier/Profile.vue'
import SupplierCollection from '@/views/supplier/Collection.vue'
import SupplierCollectionDetail from '@/views/supplier/CollectionDetail.vue'

// Vues livreur/agent
import DeliveryDashboard from '@/views/delivery/Dashboard.vue'
import AgentTasks from '@/views/delivery/AgentTasks.vue'
import DeliveryTasks from '@/views/delivery/DeliveryTasks.vue'
import DeliveryEarnings from '@/views/delivery/Earnings.vue'
import DeliveryProfile from '@/views/delivery/Profile.vue'

// Vues entrepôt
import WarehouseDashboard from '@/views/warehouse/Dashboard.vue'
import WarehouseInventory from '@/views/warehouse/Inventory.vue'
import WarehouseOrders from '@/views/warehouse/Orders.vue'
import WarehouseProfile from '@/views/warehouse/Profile.vue'

// Vues administrateur
import AdminDashboard from '@/views/admin/Dashboard.vue'
import AdminUsers from '@/views/admin/Users.vue'
import AdminAnalytics from '@/views/admin/Analytics.vue'
import AdminSettings from '@/views/admin/Settings.vue'
import AdminCategories from '@/views/admin/Categories.vue'
import AdminProductValidation from '@/views/admin/ProductValidation.vue'

const routes = [
  // Routes publiques
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  
  // Routes client
  {
    path: '/customer',
    component: CustomerLayout,
    meta: { requiresAuth: true, role: 'ROLE_CUSTOMER' },
    children: [
      {
        path: '',
        name: 'CustomerDashboard',
        component: CustomerDashboard
      },
      {
        path: 'products',
        name: 'CustomerProducts',
        component: CustomerProducts
      },
      {
        path: 'product/:id',
        name: 'CustomerProductDetail',
        component: CustomerProductDetail
      },
      {
        path: 'cart',
        name: 'CustomerCart',
        component: CustomerCart
      },
      {
        path: 'checkout',
        name: 'CustomerCheckout',
        component: CustomerCheckout
      },
      {
        path: 'orders',
        name: 'CustomerOrders',
        component: CustomerOrders
      },
      {
        path: 'order/:id',
        name: 'CustomerOrderDetail',
        component: CustomerOrderDetail
      },
      {
        path: 'profile',
        name: 'CustomerProfile',
        component: CustomerProfile
      },
      {
        path: 'categories',
        name: 'CustomerCategories',
        component: CustomerCategories
      },
      {
        path: 'farmers',
        name: 'CustomerFarmers',
        component: CustomerFarmers
      }
    ]
  },
  
  // Routes vendeur
  {
    path: '/seller',
    component: SellerLayout,
    meta: { requiresAuth: true, role: 'ROLE_SELLER' },
    children: [
      {
        path: '',
        name: 'SellerDashboard',
        component: SellerDashboard
      },
      {
        path: 'products',
        name: 'SellerProducts',
        component: SellerProducts
      },
      {
        path: 'orders',
        name: 'SellerOrders',
        component: SellerOrders
      },
      {
        path: 'reports',
        name: 'SellerReports',
        component: SellerReports
      },
      {
        path: 'profile',
        name: 'SellerProfile',
        component: SellerProfile
      }
    ]
  },
  
  // Routes fournisseur
  {
    path: '/supplier',
    component: SupplierLayout,
    meta: { requiresAuth: true, role: 'ROLE_SUPPLIER' },
    children: [
      {
        path: '',
        name: 'SupplierDashboard',
        component: SupplierDashboard
      },
      {
        path: 'products',
        name: 'SupplierProducts',
        component: SupplierProductList
      },
      {
        path: 'products/add',
        name: 'SupplierAddProduct',
        component: SupplierAddProduct
      },
      {
        path: 'products/edit/:id',
        name: 'SupplierEditProduct',
        component: SupplierEditProduct
      },
      {
        path: 'orders',
        name: 'SupplierOrders',
        component: SupplierOrders
      },
      {
        path: 'collection',
        name: 'SupplierCollection',
        component: SupplierCollection
      },
      {
        path: 'revenue',
        name: 'SupplierRevenue',
        component: () => import('@/views/supplier/Revenue.vue')
      },
      {
        path: 'collection/:id',
        name: 'SupplierCollectionDetail',
        component: SupplierCollectionDetail
      },
      {
        path: 'profile',
        name: 'SupplierProfile',
        component: SupplierProfile
      }
    ]
  },
  
  // Routes livreur/agent
  {
    path: '/delivery',
    component: DeliveryLayout,
    meta: { requiresAuth: true, role: 'ROLE_DELIVERY' },
    children: [
      {
        path: '',
        name: 'DeliveryDashboard',
        component: DeliveryDashboard
      },
      {
        path: 'agent-tasks',
        name: 'AgentTasks',
        component: AgentTasks
      },
      {
        path: 'delivery-tasks',
        name: 'DeliveryTasks',
        component: DeliveryTasks
      },
      {
        path: 'earnings',
        name: 'DeliveryEarnings',
        component: DeliveryEarnings
      },
      {
        path: 'profile',
        name: 'DeliveryProfile',
        component: DeliveryProfile
      }
    ]
  },
  
  // Routes entrepôt
  {
    path: '/warehouse',
    component: WarehouseLayout,
    meta: { requiresAuth: true, role: 'ROLE_WAREHOUSE' },
    children: [
      {
        path: '',
        name: 'WarehouseDashboard',
        component: WarehouseDashboard
      },
      {
        path: 'inventory',
        name: 'WarehouseInventory',
        component: WarehouseInventory
      },
      {
        path: 'orders',
        name: 'WarehouseOrders',
        component: WarehouseOrders
      },
      {
        path: 'receptions',
        name: 'WarehouseReceptions',
        component: () => import('@/views/warehouse/Receptions.vue')
      },
      {
        path: 'profile',
        name: 'WarehouseProfile',
        component: WarehouseProfile
      }
    ]
  },
  
  // Routes administrateur
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, role: 'ROLE_ADMIN' },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: AdminDashboard
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: AdminUsers
      },
      {
        path: 'analytics',
        name: 'AdminAnalytics',
        component: AdminAnalytics
      },
      {
        path: 'settings',
        name: 'AdminSettings',
        component: AdminSettings
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: AdminCategories
      },
      {
        path: 'validation',
        name: 'AdminProductValidation',
        component: AdminProductValidation
      },
      {
        path: 'pending',
        name: 'AdminPendingProducts',
        component: AdminProductValidation
      },
      {
        path: 'approved',
        name: 'AdminApprovedProducts',
        component: AdminProductValidation
      },
      {
        path: 'rejected',
        name: 'AdminRejectedProducts',
        component: AdminProductValidation
      }
    ]
  },
  
  // Route 404
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Garde de navigation
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // Si l'utilisateur est connecté et essaie d'accéder à la page d'accueil publique
  if (to.path === '/' && authStore.isAuthenticated) {
    // Rediriger vers le dashboard approprié selon le rôle
    const dashboardRoute = authStore.getDashboardRoute(authStore.user?.role)
    next(dashboardRoute)
    return
  }
  
  // Vérifier si l'authentification est requise
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
    return
  }
  
  // Vérifier le rôle requis
  if (to.meta.role && authStore.user?.role !== to.meta.role) {
    // Rediriger vers le dashboard approprié selon le rôle de l'utilisateur
    const dashboardRoute = authStore.getDashboardRoute(authStore.user?.role)
    next(dashboardRoute)
    return
  }
  
  // Redirection automatique pour les utilisateurs connectés qui accèdent à une route qui ne correspond pas à leur rôle
  if (authStore.isAuthenticated && authStore.user?.role) {
    const userRole = authStore.user.role
    const currentPath = to.path
    
    // Debug: afficher les informations de redirection
    console.log('🔍 Debug redirection:', {
      userRole,
      currentPath,
      isAuthenticated: authStore.isAuthenticated,
      user: authStore.user
    })
    
    // Vérifier si l'utilisateur accède à une route qui ne correspond pas à son rôle
    if (userRole === 'ROLE_SELLER' && currentPath.startsWith('/customer')) {
      console.log('🔄 Redirection vendeur vers /seller')
      next('/seller')
      return
    } else if (userRole === 'ROLE_CUSTOMER' && currentPath.startsWith('/seller')) {
      console.log('🔄 Redirection client vers /customer')
      next('/customer')
      return
    } else if (userRole === 'ROLE_SUPPLIER' && (currentPath.startsWith('/customer') || currentPath.startsWith('/seller'))) {
      console.log('🔄 Redirection fournisseur vers /supplier')
      next('/supplier')
      return
    } else if (userRole === 'ROLE_DELIVERY' && (currentPath.startsWith('/customer') || currentPath.startsWith('/seller') || currentPath.startsWith('/supplier'))) {
      console.log('🔄 Redirection livreur vers /delivery')
      next('/delivery')
      return
    } else if (userRole === 'ROLE_WAREHOUSE' && (currentPath.startsWith('/customer') || currentPath.startsWith('/seller') || currentPath.startsWith('/supplier') || currentPath.startsWith('/delivery'))) {
      console.log('🔄 Redirection entrepôt vers /warehouse')
      next('/warehouse')
      return
    } else if (userRole === 'ROLE_ADMIN' && !currentPath.startsWith('/admin')) {
      console.log('🔄 Redirection admin vers /admin')
      next('/admin')
      return
    }
  }
  
  next()
})

export default router

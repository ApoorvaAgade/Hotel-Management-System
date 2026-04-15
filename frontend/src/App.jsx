import { Link, Route, Routes } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import ProtectedRoute from './routes/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import HotelSearchPage from './pages/HotelSearchPage';
import HotelDetailPage from './pages/HotelDetailPage';
import RoomDetailPage from './pages/RoomDetailPage';
import BookingConfirmPage from './pages/BookingConfirmPage';
import MyBookingsPage from './pages/MyBookingsPage';
import AdminDashboardPage from './pages/AdminDashboardPage';
import AdminHotelsPage from './pages/AdminHotelsPage';
import AdminRoomsPage from './pages/AdminRoomsPage';
import AdminBookingsPage from './pages/AdminBookingsPage';
import AdminUsersPage from './pages/AdminUsersPage';

function App() {
  const { isAuthenticated, isAdmin, username, logout } = useAuth();

  return (
    <>
      <header className="app-header">
        <div className="app-brand">Hotel Booking</div>
        <nav className="app-nav">
          <Link to="/">Search</Link>
          {isAuthenticated && <Link to="/my-bookings">My Bookings</Link>}
          {isAdmin && <Link to="/admin">Admin</Link>}
          {!isAuthenticated && <Link to="/login">Login</Link>}
          {!isAuthenticated && <Link to="/register">Register</Link>}
          {isAuthenticated && (
            <button className="link-button" onClick={logout}>
              Logout ({username})
            </button>
          )}
        </nav>
      </header>

      <main className="container">
        <Routes>
          <Route path="/" element={<HotelSearchPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/hotels/:id" element={<HotelDetailPage />} />
          <Route path="/hotels/:hid/rooms/:rid" element={<RoomDetailPage />} />

          <Route
            path="/bookings/new"
            element={
              <ProtectedRoute roles={["CUSTOMER", "ADMIN"]}>
                <BookingConfirmPage />
              </ProtectedRoute>
            }
          />

          <Route
            path="/my-bookings"
            element={
              <ProtectedRoute roles={["CUSTOMER", "ADMIN"]}>
                <MyBookingsPage />
              </ProtectedRoute>
            }
          />

          <Route
            path="/admin"
            element={
              <ProtectedRoute roles={["ADMIN"]}>
                <AdminDashboardPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/hotels"
            element={
              <ProtectedRoute roles={["ADMIN"]}>
                <AdminHotelsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/hotels/:id/rooms"
            element={
              <ProtectedRoute roles={["ADMIN"]}>
                <AdminRoomsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/bookings"
            element={
              <ProtectedRoute roles={["ADMIN"]}>
                <AdminBookingsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/users"
            element={
              <ProtectedRoute roles={["ADMIN"]}>
                <AdminUsersPage />
              </ProtectedRoute>
            }
          />
        </Routes>
      </main>
    </>
  );
}

export default App;

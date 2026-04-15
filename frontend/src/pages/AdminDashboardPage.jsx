import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getAllBookingsApi } from '../api/bookingApi';
import { getHotelsApi } from '../api/hotelApi';
import { searchRoomsApi } from '../api/roomApi';

function AdminDashboardPage() {
  const [stats, setStats] = useState({ hotels: 0, rooms: 0, bookings: 0 });
  const [error, setError] = useState('');

  useEffect(() => {
    const load = async () => {
      try {
        const [h, r, b] = await Promise.all([
          getHotelsApi(),
          searchRoomsApi(),
          getAllBookingsApi()
        ]);
        setStats({ hotels: h.data.length, rooms: r.data.length, bookings: b.data.length });
      } catch (err) {
        setError(err?.response?.data?.message || 'Could not load dashboard');
      }
    };
    load();
  }, []);

  return (
    <section className="stack-lg">
      <article className="card">
        <h1>Admin Dashboard</h1>
        {error && <p className="error-msg">{error}</p>}
        <div className="stats-grid">
          <div className="stat">Hotels: {stats.hotels}</div>
          <div className="stat">Rooms: {stats.rooms}</div>
          <div className="stat">Bookings: {stats.bookings}</div>
        </div>
      </article>

      <article className="card">
        <h2>Quick Actions</h2>
        <div className="action-grid">
          <Link className="action-button" to="/admin/hotels">Create / Edit Hotel</Link>
          <Link className="action-button" to="/admin/hotels">Create / Edit Room</Link>
          <Link className="action-button" to="/admin/hotels">Delete Hotel / Room</Link>
          <Link className="action-button" to="/admin/bookings">View All Bookings</Link>
          <Link className="action-button" to="/admin/users">Manage Users</Link>
        </div>
        <p className="muted">Tip: Open hotels and click Manage Rooms for room-level create/edit/delete actions.</p>
      </article>
    </section>
  );
}

export default AdminDashboardPage;

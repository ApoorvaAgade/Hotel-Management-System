import { useEffect, useState } from 'react';
import { cancelBookingApi, getAllBookingsApi } from '../api/bookingApi';

function AdminBookingsPage() {
  const [bookings, setBookings] = useState([]);
  const [error, setError] = useState('');

  const load = async () => {
    try {
      const { data } = await getAllBookingsApi();
      setBookings(data);
    } catch (err) {
      setError(err?.response?.data?.message || 'Could not load bookings');
    }
  };

  useEffect(() => {
    load();
  }, []);

  const cancel = async (id) => {
    try {
      await cancelBookingApi(id);
      await load();
    } catch (err) {
      setError(err?.response?.data?.message || 'Could not cancel booking');
    }
  };

  return (
    <section className="card">
      <h1>All Bookings</h1>
      {error && <p className="error-msg">{error}</p>}
      <div className="grid-cards">
        {bookings.map((b) => (
          <div key={b.id} className="item-card">
            <h3>{b.userName} - {b.hotelName} / {b.roomNumber}</h3>
            <p>{b.checkInDate} to {b.checkOutDate}</p>
            <p>Total: Rs. {b.totalPrice}</p>
            <p>Status: {b.status}</p>
            {b.status === 'CONFIRMED' && <button onClick={() => cancel(b.id)}>Cancel</button>}
          </div>
        ))}
      </div>
    </section>
  );
}

export default AdminBookingsPage;

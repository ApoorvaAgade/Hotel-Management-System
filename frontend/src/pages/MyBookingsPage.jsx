import { useEffect, useState } from 'react';
import { cancelBookingApi, getMyBookingsApi } from '../api/bookingApi';

function MyBookingsPage() {
  const [bookings, setBookings] = useState([]);
  const [error, setError] = useState('');

  const load = async () => {
    try {
      const { data } = await getMyBookingsApi();
      setBookings(data);
    } catch (err) {
      setError(err?.response?.data?.message || 'Could not fetch your bookings');
    }
  };

  useEffect(() => {
    load();
  }, []);

  const cancelBooking = async (id) => {
    try {
      await cancelBookingApi(id);
      await load();
    } catch (err) {
      setError(err?.response?.data?.message || 'Could not cancel booking');
    }
  };

  return (
    <section className="card">
      <h1>My Bookings</h1>
      {error && <p className="error-msg">{error}</p>}
      <div className="grid-cards">
        {bookings.map((b) => (
          <div key={b.id} className="item-card">
            <h3>{b.hotelName} - {b.roomNumber}</h3>
            <p>{b.checkInDate} to {b.checkOutDate}</p>
            <p>Total: Rs. {b.totalPrice}</p>
            <p>Status: {b.status}</p>
            {b.status === 'CONFIRMED' && <button onClick={() => cancelBooking(b.id)}>Cancel</button>}
          </div>
        ))}
      </div>
    </section>
  );
}

export default MyBookingsPage;

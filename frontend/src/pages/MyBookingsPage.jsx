import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { cancelBookingApi, getMyBookingsApi } from '../api/bookingApi';

function MyBookingsPage() {
  const navigate = useNavigate();
  const [bookings, setBookings] = useState([]);
  const [error, setError] = useState('');

  const toIsoDate = (date) => date.toISOString().slice(0, 10);

  const getRebookDates = (booking) => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const originalCheckIn = new Date(booking.checkInDate);
    originalCheckIn.setHours(0, 0, 0, 0);

    const originalCheckOut = new Date(booking.checkOutDate);
    originalCheckOut.setHours(0, 0, 0, 0);

    if (originalCheckIn >= today && originalCheckOut > originalCheckIn) {
      return { checkIn: booking.checkInDate, checkOut: booking.checkOutDate };
    }

    const newCheckIn = new Date(today);
    newCheckIn.setDate(newCheckIn.getDate() + 1);

    const newCheckOut = new Date(newCheckIn);
    newCheckOut.setDate(newCheckOut.getDate() + 1);

    return { checkIn: toIsoDate(newCheckIn), checkOut: toIsoDate(newCheckOut) };
  };

  const quickRebook = (booking) => {
    const { checkIn, checkOut } = getRebookDates(booking);
    navigate(`/bookings/new?hotelId=${booking.hotelId}&roomId=${booking.roomId}&checkIn=${checkIn}&checkOut=${checkOut}`);
  };

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
            <div className="inline-actions">
              <button onClick={() => quickRebook(b)}>Rebook</button>
              {b.status === 'CONFIRMED' && <button onClick={() => cancelBooking(b.id)}>Cancel</button>}
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}

export default MyBookingsPage;

import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { createBookingApi } from '../api/bookingApi';
import { getRoomByIdApi } from '../api/roomApi';

function BookingConfirmPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const hotelId = searchParams.get('hotelId');
  const roomId = searchParams.get('roomId');

  const [checkInDate, setCheckInDate] = useState(searchParams.get('checkIn') || '');
  const [checkOutDate, setCheckOutDate] = useState(searchParams.get('checkOut') || '');
  const [room, setRoom] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const load = async () => {
      if (!hotelId || !roomId) {
        setError('Missing room selection');
        return;
      }
      try {
        const { data } = await getRoomByIdApi(hotelId, roomId, {
          checkIn: checkInDate || undefined,
          checkOut: checkOutDate || undefined
        });
        setRoom(data);
      } catch (err) {
        setError(err?.response?.data?.message || 'Could not fetch room details');
      }
    };
    load();
  }, [hotelId, roomId, checkInDate, checkOutDate]);

  const confirmBooking = async () => {
    setLoading(true);
    setError('');
    try {
      await createBookingApi({ roomId: Number(roomId), checkInDate, checkOutDate });
      navigate('/my-bookings');
    } catch (err) {
      setError(err?.response?.data?.message || 'Booking failed');
    } finally {
      setLoading(false);
    }
  };

  if (!room) return <p className="page-state">Loading booking summary...</p>;

  return (
    <section className="card small-card">
      <h1>Confirm Booking</h1>
      <p>Hotel: {room.hotelName}</p>
      <p>Room: {room.roomNumber} ({room.category})</p>
      <p>Price/Night: Rs. {room.pricePerNight}</p>

      <div className="form-grid">
        <label>
          Check-In
          <input type="date" value={checkInDate} onChange={(e) => setCheckInDate(e.target.value)} />
        </label>
        <label>
          Check-Out
          <input type="date" value={checkOutDate} onChange={(e) => setCheckOutDate(e.target.value)} />
        </label>
      </div>

      <button onClick={confirmBooking} disabled={loading || !checkInDate || !checkOutDate}>
        {loading ? 'Confirming...' : 'Confirm Booking'}
      </button>
      {error && <p className="error-msg">{error}</p>}
    </section>
  );
}

export default BookingConfirmPage;

import { useEffect, useState } from 'react';
import { Link, useParams, useSearchParams } from 'react-router-dom';
import { getRoomByIdApi } from '../api/roomApi';
import { getRoomImage } from '../utils/imageCatalog';

function RoomDetailPage() {
  const { hid, rid } = useParams();
  const [searchParams] = useSearchParams();
  const [room, setRoom] = useState(null);
  const [error, setError] = useState('');

  const checkIn = searchParams.get('checkIn') || '';
  const checkOut = searchParams.get('checkOut') || '';

  useEffect(() => {
    const load = async () => {
      try {
        const { data } = await getRoomByIdApi(hid, rid, {
          checkIn: checkIn || undefined,
          checkOut: checkOut || undefined
        });
        setRoom(data);
      } catch (err) {
        setError(err?.response?.data?.message || 'Failed to load room');
      }
    };
    load();
  }, [hid, rid, checkIn, checkOut]);

  if (error) return <p className="error-msg">{error}</p>;
  if (!room) return <p className="page-state">Loading room...</p>;

  return (
    <section className="card room-detail-wrap">
      <img className="room-detail-image" src={getRoomImage(room)} alt={`${room.category} room`} />
      <div className="room-detail-content">
        <h1>{room.hotelName} - Room {room.roomNumber}</h1>
        <p className="muted">{room.hotelLocation}</p>
        <p className="price-label">Rs. {room.pricePerNight} / night</p>
        <p>Category: {room.category}</p>
        <p>Capacity: {room.capacity} guests</p>
        <p>Amenities: {room.amenities}</p>
        <p className={room.available ? 'status-green' : 'status-red'}>
          {room.available ? 'Available for selected dates' : 'Not available for selected dates'}
        </p>

        {room.available ? (
          <Link to={`/bookings/new?hotelId=${hid}&roomId=${rid}&checkIn=${checkIn}&checkOut=${checkOut}`} className="action-link book-link">
            Book This Room
          </Link>
        ) : (
          <p className="muted">Please choose another date range.</p>
        )}
      </div>
    </section>
  );
}

export default RoomDetailPage;

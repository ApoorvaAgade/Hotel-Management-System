import { useEffect, useState } from 'react';
import { Link, useParams, useSearchParams } from 'react-router-dom';
import { getHotelByIdApi } from '../api/hotelApi';
import { getRoomsByHotelApi } from '../api/roomApi';
import { getHotelImage, getRoomImage } from '../utils/imageCatalog';

function HotelDetailPage() {
  const { id } = useParams();
  const [searchParams] = useSearchParams();
  const [hotel, setHotel] = useState(null);
  const [rooms, setRooms] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    const load = async () => {
      try {
        const checkIn = searchParams.get('checkIn') || undefined;
        const checkOut = searchParams.get('checkOut') || undefined;
        const [hotelRes, roomRes] = await Promise.all([
          getHotelByIdApi(id),
          getRoomsByHotelApi(id, { checkIn, checkOut })
        ]);
        setHotel(hotelRes.data);
        setRooms(roomRes.data);
      } catch (err) {
        setError(err?.response?.data?.message || 'Failed to load hotel details');
      }
    };
    load();
  }, [id, searchParams]);

  if (error) return <p className="error-msg">{error}</p>;
  if (!hotel) return <p className="page-state">Loading hotel...</p>;

  return (
    <section className="stack-lg">
      <article className="hotel-banner card">
        <img className="hotel-banner-image" src={getHotelImage(hotel)} alt={hotel.name} />
        <div className="hotel-banner-content">
          <h1>{hotel.name}</h1>
          <p className="muted">{hotel.location}</p>
          <p>{hotel.description}</p>
          <div className="chip-row">
            {hotel.amenities.split(',').map((item) => (
              <span className="chip" key={item.trim()}>{item.trim()}</span>
            ))}
          </div>
        </div>
      </article>

      <article className="card">
        <h2>Rooms</h2>
        <div className="room-grid">
          {rooms.map((room) => (
            <div key={room.id} className={`room-card ${room.available ? 'ok' : 'warn'}`}>
              <img className="room-image" src={getRoomImage(room)} alt={`${room.category} room`} />
              <div className="room-content">
                <h3>Room {room.roomNumber}</h3>
                <p className="muted">{room.category}</p>
                <p className="price-label">Rs. {room.pricePerNight} / night</p>
                <p>Capacity: {room.capacity} guests</p>
                <p className={room.available ? 'status-green' : 'status-red'}>
                  {room.available ? 'Available' : 'Booked'}
                </p>
                <Link
                  className="action-link"
                  to={`/hotels/${id}/rooms/${room.id}?checkIn=${searchParams.get('checkIn') || ''}&checkOut=${searchParams.get('checkOut') || ''}`}
                >
                  Room Details
                </Link>
              </div>
            </div>
          ))}
        </div>
      </article>
    </section>
  );
}

export default HotelDetailPage;

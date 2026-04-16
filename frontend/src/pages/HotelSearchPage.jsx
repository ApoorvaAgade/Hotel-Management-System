import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getHotelsApi } from '../api/hotelApi';
import { searchRoomsApi } from '../api/roomApi';
import { getHotelImage, getRoomImage } from '../utils/imageCatalog';
import { amenityOptions, locationOptions } from '../utils/searchOptions';

function HotelSearchPage() {
  const [hotels, setHotels] = useState([]);
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filters, setFilters] = useState({
    location: '',
    checkIn: '',
    checkOut: '',
    minPrice: '',
    maxPrice: '',
    amenity: '',
    category: ''
  });

  const fetchData = async () => {
    setLoading(true);
    setError('');

    if (filters.checkIn && filters.checkOut && new Date(filters.checkOut) <= new Date(filters.checkIn)) {
      setHotels([]);
      setRooms([]);
      setError('Check-out date must be after check-in date');
      setLoading(false);
      return;
    }

    try {
      const [hotelRes, roomRes] = await Promise.all([
        getHotelsApi({ location: filters.location, amenity: filters.amenity }),
        searchRoomsApi(filters)
      ]);
      setHotels(hotelRes.data);
      setRooms(roomRes.data);
    } catch (err) {
      setHotels([]);
      setRooms([]);
      setError(err?.response?.data?.message || 'Could not load hotels');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const onSearch = (e) => {
    e.preventDefault();
    fetchData();
  };

  return (
    <section className="stack-lg">
      <article className="landing-hero">
        <div className="landing-overlay">
          <p className="eyebrow">Plan. Compare. Book.</p>
          <h1>Find Your Stay</h1>
          <p className="hero-subtitle">
            Discover curated rooms with live availability and transparent pricing.
          </p>
        </div>
      </article>

      <article className="card search-card">
        <form className="filter-grid" onSubmit={onSearch}>
          <select value={filters.location} onChange={(e) => setFilters((p) => ({ ...p, location: e.target.value }))}>
            <option value="">All cities</option>
            {locationOptions.map((city) => (
              <option key={city} value={city}>{city}</option>
            ))}
          </select>
          <input type="date" value={filters.checkIn} onChange={(e) => setFilters((p) => ({ ...p, checkIn: e.target.value }))} />
          <input type="date" value={filters.checkOut} onChange={(e) => setFilters((p) => ({ ...p, checkOut: e.target.value }))} />
          <input type="number" min="0" step="0.01" placeholder="Min price" value={filters.minPrice} onChange={(e) => setFilters((p) => ({ ...p, minPrice: e.target.value }))} />
          <input type="number" min="0" step="0.01" placeholder="Max price" value={filters.maxPrice} onChange={(e) => setFilters((p) => ({ ...p, maxPrice: e.target.value }))} />
          <select value={filters.amenity} onChange={(e) => setFilters((p) => ({ ...p, amenity: e.target.value }))}>
            <option value="">All amenities</option>
            {amenityOptions.map((amenity) => (
              <option key={amenity.value} value={amenity.value}>{amenity.label}</option>
            ))}
          </select>
          <select value={filters.category} onChange={(e) => setFilters((p) => ({ ...p, category: e.target.value }))}>
            <option value="">All categories</option>
            <option value="SINGLE">Single</option>
            <option value="DOUBLE">Double</option>
            <option value="SUITE">Suite</option>
          </select>
          <button type="submit">Search Rooms</button>
        </form>
      </article>

      {error && <p className="error-msg">{error}</p>}
      {loading && <p className="page-state">Loading hotels and rooms...</p>}

      {!loading && !error && (
        <>
          <article className="card">
            <h2>Hotels</h2>
            <div className="hotel-grid">
              {hotels.map((hotel) => (
                <div key={hotel.id} className="showcase-card">
                  <img className="showcase-image" src={getHotelImage(hotel)} alt={hotel.name} />
                  <div className="showcase-content">
                    <h3>{hotel.name}</h3>
                    <p className="muted">{hotel.location}</p>
                    <p>{hotel.description}</p>
                    <div className="chip-row">
                      {hotel.amenities.split(',').slice(0, 4).map((item) => (
                        <span className="chip" key={item.trim()}>{item.trim()}</span>
                      ))}
                    </div>
                    <Link className="action-link" to={`/hotels/${hotel.id}`}>View Hotel</Link>
                  </div>
                </div>
              ))}
            </div>
          </article>

          <article className="card">
            <h2>Matching Rooms</h2>
            <div className="room-grid">
              {rooms.map((room) => (
                <div key={room.id} className={`room-card ${room.available ? 'ok' : 'warn'}`}>
                  <img className="room-image" src={getRoomImage(room)} alt={`${room.category} room`} />
                  <div className="room-content">
                    <h3>{room.hotelName} - Room {room.roomNumber}</h3>
                    <p className="muted">{room.category} | {room.hotelLocation}</p>
                    <p className="price-label">Rs. {room.pricePerNight} / night</p>
                    <p>Capacity: {room.capacity} guests</p>
                    <p className={room.available ? 'status-green' : 'status-red'}>
                      {room.available ? 'Available' : 'Booked in selected date range'}
                    </p>
                    <Link
                      className="action-link"
                      to={`/hotels/${room.hotelId}/rooms/${room.id}?checkIn=${filters.checkIn || ''}&checkOut=${filters.checkOut || ''}`}
                    >
                      View Room
                    </Link>
                  </div>
                </div>
              ))}
            </div>
          </article>
        </>
      )}
    </section>
  );
}

export default HotelSearchPage;

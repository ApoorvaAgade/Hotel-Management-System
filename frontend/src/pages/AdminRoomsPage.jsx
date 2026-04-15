import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { createRoomApi, deleteRoomApi, getRoomsByHotelApi, updateRoomApi } from '../api/roomApi';

const initialForm = {
  roomNumber: '',
  category: 'SINGLE',
  pricePerNight: '',
  capacity: '',
  amenities: ''
};

function AdminRoomsPage() {
  const { id: hotelId } = useParams();
  const [rooms, setRooms] = useState([]);
  const [form, setForm] = useState(initialForm);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState('');

  const load = async () => {
    try {
      const { data } = await getRoomsByHotelApi(hotelId);
      setRooms(data);
    } catch (err) {
      setError(err?.response?.data?.message || 'Could not load rooms');
    }
  };

  useEffect(() => {
    load();
  }, [hotelId]);

  const submit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...form,
        pricePerNight: Number(form.pricePerNight),
        capacity: Number(form.capacity)
      };
      if (editingId) {
        await updateRoomApi(hotelId, editingId, payload);
      } else {
        await createRoomApi(hotelId, payload);
      }
      setForm(initialForm);
      setEditingId(null);
      await load();
    } catch (err) {
      setError(err?.response?.data?.message || 'Could not save room');
    }
  };

  const edit = (room) => {
    setEditingId(room.id);
    setForm({
      roomNumber: room.roomNumber,
      category: room.category,
      pricePerNight: room.pricePerNight,
      capacity: room.capacity,
      amenities: room.amenities
    });
  };

  const remove = async (roomId) => {
    try {
      await deleteRoomApi(hotelId, roomId);
      await load();
    } catch (err) {
      setError(err?.response?.data?.message || 'Could not delete room');
    }
  };

  return (
    <section className="stack-lg">
      <article className="card">
        <h1>Admin Rooms (Hotel #{hotelId})</h1>
        <form className="form-grid" onSubmit={submit}>
          <input placeholder="Room number" value={form.roomNumber} onChange={(e) => setForm((p) => ({ ...p, roomNumber: e.target.value }))} required />
          <select value={form.category} onChange={(e) => setForm((p) => ({ ...p, category: e.target.value }))}>
            <option value="SINGLE">Single</option>
            <option value="DOUBLE">Double</option>
            <option value="SUITE">Suite</option>
          </select>
          <input type="number" step="0.01" min="0" placeholder="Price/night" value={form.pricePerNight} onChange={(e) => setForm((p) => ({ ...p, pricePerNight: e.target.value }))} required />
          <input type="number" min="1" placeholder="Capacity" value={form.capacity} onChange={(e) => setForm((p) => ({ ...p, capacity: e.target.value }))} required />
          <textarea placeholder="Amenities" value={form.amenities} onChange={(e) => setForm((p) => ({ ...p, amenities: e.target.value }))} required />
          <button type="submit">{editingId ? 'Update Room' : 'Create Room'}</button>
        </form>
      </article>

      {error && <p className="error-msg">{error}</p>}

      <article className="card">
        <div className="grid-cards">
          {rooms.map((room) => (
            <div key={room.id} className={`item-card ${room.available ? 'ok' : 'warn'}`}>
              <h3>Room {room.roomNumber}</h3>
              <p>{room.category} | Capacity {room.capacity}</p>
              <p>Price/Night: Rs. {room.pricePerNight}</p>
              <p className="muted">{room.amenities}</p>
              <p className={room.available ? 'status-green' : 'status-red'}>{room.available ? 'Available' : 'Booked'}</p>
              <div className="inline-actions">
                <button onClick={() => edit(room)}>Edit</button>
                <button onClick={() => remove(room.id)}>Delete</button>
              </div>
            </div>
          ))}
        </div>
      </article>
    </section>
  );
}

export default AdminRoomsPage;

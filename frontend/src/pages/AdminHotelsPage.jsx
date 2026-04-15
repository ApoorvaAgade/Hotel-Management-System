import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { createHotelApi, deleteHotelApi, getHotelsApi, updateHotelApi } from '../api/hotelApi';

const initialForm = { name: '', location: '', description: '', amenities: '' };

function AdminHotelsPage() {
  const [hotels, setHotels] = useState([]);
  const [form, setForm] = useState(initialForm);
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState('');

  const load = async () => {
    try {
      const { data } = await getHotelsApi();
      setHotels(data);
    } catch (err) {
      setError(err?.response?.data?.message || 'Could not load hotels');
    }
  };

  useEffect(() => {
    load();
  }, []);

  const submit = async (e) => {
    e.preventDefault();
    try {
      if (editingId) {
        await updateHotelApi(editingId, form);
      } else {
        await createHotelApi(form);
      }
      setForm(initialForm);
      setEditingId(null);
      await load();
    } catch (err) {
      setError(err?.response?.data?.message || 'Could not save hotel');
    }
  };

  const edit = (hotel) => {
    setEditingId(hotel.id);
    setForm({
      name: hotel.name,
      location: hotel.location,
      description: hotel.description,
      amenities: hotel.amenities
    });
  };

  const remove = async (id) => {
    try {
      await deleteHotelApi(id);
      await load();
    } catch (err) {
      setError(err?.response?.data?.message || 'Could not delete hotel');
    }
  };

  return (
    <section className="stack-lg">
      <article className="card">
        <h1>Admin Hotels</h1>
        <form className="form-grid" onSubmit={submit}>
          <input placeholder="Hotel name" value={form.name} onChange={(e) => setForm((p) => ({ ...p, name: e.target.value }))} required />
          <input placeholder="Location" value={form.location} onChange={(e) => setForm((p) => ({ ...p, location: e.target.value }))} required />
          <textarea placeholder="Description" value={form.description} onChange={(e) => setForm((p) => ({ ...p, description: e.target.value }))} required />
          <textarea placeholder="Amenities (comma-separated)" value={form.amenities} onChange={(e) => setForm((p) => ({ ...p, amenities: e.target.value }))} required />
          <button type="submit">{editingId ? 'Update Hotel' : 'Create Hotel'}</button>
        </form>
        {editingId && <button onClick={() => { setEditingId(null); setForm(initialForm); }}>Cancel Edit</button>}
      </article>

      {error && <p className="error-msg">{error}</p>}

      <article className="card">
        <div className="grid-cards">
          {hotels.map((hotel) => (
            <div className="item-card" key={hotel.id}>
              <h3>{hotel.name}</h3>
              <p>{hotel.location}</p>
              <p className="muted">{hotel.amenities}</p>
              <div className="inline-actions">
                <button onClick={() => edit(hotel)}>Edit</button>
                <button onClick={() => remove(hotel.id)}>Delete</button>
                <Link to={`/admin/hotels/${hotel.id}/rooms`}>Manage Rooms</Link>
              </div>
            </div>
          ))}
        </div>
      </article>
    </section>
  );
}

export default AdminHotelsPage;

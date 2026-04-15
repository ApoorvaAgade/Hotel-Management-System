import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { registerApi } from '../api/authApi';
import { getApiErrorMessage } from '../utils/apiError';

function RegisterPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: '', email: '', password: '', role: 'CUSTOMER' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const onSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await registerApi(form);
      navigate('/login');
    } catch (err) {
      setError(getApiErrorMessage(err, 'email'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="card small-card">
      <h1>Register</h1>
      <form onSubmit={onSubmit} className="form-grid">
        <input
          placeholder="Name"
          value={form.name}
          onChange={(e) => setForm((p) => ({ ...p, name: e.target.value }))}
          required
        />
        <input
          type="email"
          placeholder="Email"
          value={form.email}
          onChange={(e) => setForm((p) => ({ ...p, email: e.target.value }))}
          required
        />
        <input
          type="password"
          placeholder="Password (min 8 chars)"
          minLength={8}
          value={form.password}
          onChange={(e) => setForm((p) => ({ ...p, password: e.target.value }))}
          required
        />
        <button type="submit" disabled={loading}>
          {loading ? 'Creating account...' : 'Register'}
        </button>
      </form>
      {error && <p className="error-msg">{error}</p>}
      <p>
        Already registered? <Link to="/login">Login</Link>
      </p>
    </section>
  );
}

export default RegisterPage;

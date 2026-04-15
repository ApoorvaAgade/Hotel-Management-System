import { useEffect, useState } from 'react';
import { getAllUsersApi, updateUserStatusApi } from '../api/authApi';

function AdminUsersPage() {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState('');
  const [busyUserId, setBusyUserId] = useState(null);

  const load = async () => {
    try {
      const { data } = await getAllUsersApi();
      setUsers(data);
    } catch (err) {
      setError(err?.response?.data?.message || 'Could not load users');
    }
  };

  useEffect(() => {
    load();
  }, []);

  const toggleStatus = async (user) => {
    try {
      setBusyUserId(user.id);
      await updateUserStatusApi(user.id, !user.isActive);
      await load();
    } catch (err) {
      setError(err?.response?.data?.message || 'Could not update user status');
    } finally {
      setBusyUserId(null);
    }
  };

  return (
    <section className="stack-lg">
      <article className="card">
        <h1>Manage Users</h1>
        <p className="muted">Activate/deactivate customer and admin accounts.</p>
        {error && <p className="error-msg">{error}</p>}
      </article>

      <article className="card">
        <div className="grid-cards">
          {users.map((user) => (
            <div className="item-card" key={user.id}>
              <h3>{user.name}</h3>
              <p>{user.email}</p>
              <p>Role: <strong>{user.role}</strong></p>
              <p>
                Status:{' '}
                <span className={user.isActive ? 'status-green' : 'status-red'}>
                  {user.isActive ? 'ACTIVE' : 'INACTIVE'}
                </span>
              </p>
              <button
                type="button"
                onClick={() => toggleStatus(user)}
                disabled={busyUserId === user.id}
              >
                {busyUserId === user.id
                  ? 'Updating...'
                  : user.isActive
                    ? 'Deactivate User'
                    : 'Activate User'}
              </button>
            </div>
          ))}
        </div>
      </article>
    </section>
  );
}

export default AdminUsersPage;

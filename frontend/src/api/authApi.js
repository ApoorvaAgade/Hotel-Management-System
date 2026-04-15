import http from './http';

export const loginApi = (payload) => http.post('/auth/login', payload);
export const registerApi = (payload) => http.post('/auth/register', payload);
export const logoutApi = () => http.post('/auth/logout');
export const meApi = () => http.get('/auth/me');
export const getAllUsersApi = () => http.get('/auth/users');
export const updateUserStatusApi = (userId, isActive) =>
	http.put(`/auth/users/${userId}/status`, { isActive });

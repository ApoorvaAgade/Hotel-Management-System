import http from './http';

export const createBookingApi = (payload) => http.post('/bookings', payload);
export const getMyBookingsApi = () => http.get('/bookings/my');
export const getAllBookingsApi = () => http.get('/bookings');
export const cancelBookingApi = (bookingId) => http.delete(`/bookings/${bookingId}`);

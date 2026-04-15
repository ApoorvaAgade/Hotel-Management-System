import http from './http';

export const getHotelsApi = (params) => http.get('/hotels', { params });
export const getHotelByIdApi = (id) => http.get(`/hotels/${id}`);
export const createHotelApi = (payload) => http.post('/hotels', payload);
export const updateHotelApi = (id, payload) => http.put(`/hotels/${id}`, payload);
export const deleteHotelApi = (id) => http.delete(`/hotels/${id}`);

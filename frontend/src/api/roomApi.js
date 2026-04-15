import http from './http';

export const getRoomsByHotelApi = (hotelId, params) => http.get(`/hotels/${hotelId}/rooms`, { params });
export const getRoomByIdApi = (hotelId, roomId, params) => http.get(`/hotels/${hotelId}/rooms/${roomId}`, { params });
export const searchRoomsApi = (params) => http.get('/rooms/search', { params });
export const createRoomApi = (hotelId, payload) => http.post(`/hotels/${hotelId}/rooms`, payload);
export const updateRoomApi = (hotelId, roomId, payload) => http.put(`/hotels/${hotelId}/rooms/${roomId}`, payload);
export const deleteRoomApi = (hotelId, roomId) => http.delete(`/hotels/${hotelId}/rooms/${roomId}`);

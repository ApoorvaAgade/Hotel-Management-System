const hotelByName = {
  'grand vista': '/images/hotels/hotel-grand-vista.svg',
  'sea breeze': '/images/hotels/hotel-sea-breeze.svg'
};

const roomByCategory = {
  SINGLE: '/images/rooms/room-single.svg',
  DOUBLE: '/images/rooms/room-double.svg',
  SUITE: '/images/rooms/room-suite.svg'
};

export function getHotelImage(hotel) {
  const key = hotel?.name?.toLowerCase()?.trim();
  if (key && hotelByName[key]) {
    return hotelByName[key];
  }
  return '/images/hotels/hotel-default.svg';
}

export function getRoomImage(room) {
  const category = room?.category?.toUpperCase()?.trim();
  if (category && roomByCategory[category]) {
    return roomByCategory[category];
  }
  return '/images/rooms/room-default.svg';
}

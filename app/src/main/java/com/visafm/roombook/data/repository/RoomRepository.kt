package com.visafm.roombook.data.repository

import com.visafm.roombook.data.model.Room

interface RoomRepository {
    fun getRoomList(): List<Room>
}
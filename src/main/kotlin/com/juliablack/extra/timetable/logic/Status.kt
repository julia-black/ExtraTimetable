package com.juliablack.extra.timetable.logic

data class Status<T>(var state: StateGenetic,
                     var data: T?,
                     var exception: Exception?)
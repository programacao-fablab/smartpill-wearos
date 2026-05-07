package com.smartpillwearos.worker

class DummyNetworkManager(var isOnline: Boolean)

class DummyRoomDb {
    fun wasPillActionSaved(id: String): Boolean = false
}

class DummyWorkManager {
    fun wasSyncTaskScheduled(): Boolean = false
}

class PillActionHandler(
    val networkManager: DummyNetworkManager,
    val roomDb: DummyRoomDb,
    val workManager: DummyWorkManager
) {
    fun takePill(pillId: String) {
        // Empty for RED phase
    }
}

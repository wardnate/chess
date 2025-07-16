package service;

import dataaccess.*;

public class ClearService {
    private final DataAccess db;

    public ClearService(DataAccess db) {
        this.db = db;
    }

    public void clear() throws DataAccessException {
        db.clear();
    }
}


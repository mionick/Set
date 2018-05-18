package io.github.mionick.storage;

import android.arch.persistence.room.*;

import java.util.List;

@Dao
public interface RecordDao {
        @Query("SELECT * FROM record")
        List<Record> getAll();

        @Query("SELECT * FROM record WHERE id IN (:ids)")
        List<Record> loadAllByIds(int[] ids);

        @Insert
        void insertAll(Record... records);

        @Delete
        void delete(Record record);
    }

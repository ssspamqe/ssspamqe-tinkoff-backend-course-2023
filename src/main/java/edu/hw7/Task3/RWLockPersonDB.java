package edu.hw7.Task3;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.jetbrains.annotations.Nullable;

public class RWLockPersonDB extends AbstractPersonDB {

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public void add(Person newPerson) {
        rwLock.writeLock().lock();
        try {
            super.add(newPerson);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void delete(int id) {
        rwLock.writeLock().lock();
        try {
            super.delete(id);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public @Nullable List<Person> findByName(String name) {
        rwLock.readLock().lock();
        try {
            return super.findByName(name);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public @Nullable List<Person> findByAddress(String address) {
        rwLock.readLock().lock();
        try {
            return super.findByAddress(address);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public @Nullable Person findByPhone(String phone) {
        rwLock.readLock().lock();
        try {
            return super.findByPhone(phone);
        } finally {
            rwLock.readLock().unlock();
        }
    }
}

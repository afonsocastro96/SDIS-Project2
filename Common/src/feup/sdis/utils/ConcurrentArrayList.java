package feup.sdis.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Concurrent Array List
 * @param <T> type of the arraylist
 */
public class ConcurrentArrayList<T> {

    /**
     * Read lock
     */
    private final Lock readLock;

    /**
     * Write lock
     */
    private final Lock writeLock;

    /**
     * List with values
     */
    private final List<T> list;

    /**
     * Constructor of ConcurrentArrayList
     */
    public ConcurrentArrayList() {
        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        readLock = rwLock.readLock();
        writeLock = rwLock.writeLock();
        list = new ArrayList<>();
    }

    /**
     * Add a element to the list
     * @param e element to be added
     */
    public void add(T e) {
        writeLock.lock();
        try {
            list.add(e);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Remove a element from the list
     * @param e element to be removed
     */
    public void remove(T e) {
        writeLock.lock();
        try {
            list.remove(e);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Get a element from the list
     * @param index index of the element
     */
    public void get(int index) {
        readLock.lock();
        try {
            list.get(index);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Get the size of the list
     * @return size of the list
     */
    public int size() {
        return list.size();
    }

    /**
     * Iterator for the list
     * @return iterator for the list
     */
    public Iterator<T> iterator() {
        readLock.lock();
        try {
            return new ArrayList<>(list).iterator();
        } finally {
            readLock.unlock();
        }
    }
}

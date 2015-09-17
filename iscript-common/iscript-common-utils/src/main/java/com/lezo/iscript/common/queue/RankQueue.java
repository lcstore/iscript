package com.lezo.iscript.common.queue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class RankQueue<E> implements Queue<E> {
    private ConcurrentHashMap<Integer, Queue<E>> levelQueueMap = new ConcurrentHashMap<Integer, Queue<E>>();
    private List<Integer> levelDescList = new ArrayList<Integer>();
    private RankGetter rankGetter;
    private QueueFactory<E> queueFactory;
    private int capacity;

    public RankQueue(RankGetter rankGetter, int capacity) {
        this(rankGetter, new ArrayBlockingQueueFactory<E>(), capacity);
    }

    public RankQueue(RankGetter rankGetter, QueueFactory<E> queueFactory, int capacity) {
        super();
        this.rankGetter = rankGetter;
        this.queueFactory = queueFactory;
        this.capacity = capacity;
    }

    @Override
    public int size() {
        int total = 0;
        for (Entry<Integer, Queue<E>> entry : levelQueueMap.entrySet()) {
            total += entry.getValue().size();
        }
        return total;
    }

    public int size(int level) {
        Queue<E> hasQueue = levelQueueMap.get(level);
        return hasQueue == null ? 0 : hasQueue.size();
    }

    @Override
    public boolean isEmpty() {
        return size() < 1;
    }

    @Override
    public boolean addAll(Collection<? extends E> elements) {
        for (E e : elements) {
            if (!add(e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void clear() {
        for (Entry<Integer, Queue<E>> entry : levelQueueMap.entrySet()) {
            entry.getValue().clear();
        }
    }

    @Override
    public boolean add(E e) {
        int level = rankGetter.getRank(e);
        Queue<E> curQueue = getQueue(level, true);
        return curQueue.add(e);
    }

    @Override
    public boolean offer(E e) {
        int level = rankGetter.getRank(e);
        Queue<E> curQueue = getQueue(level, true);
        return curQueue.offer(e);
    }

    @Override
    public E remove() {
        for (Integer level : levelDescList) {
            Queue<E> hasQueue = getQueue(level, false);
            E element = hasQueue.remove();
            if (element != null) {
                return element;
            }
        }
        return null;
    }

    @Override
    public E poll() {
        for (Integer level : levelDescList) {
            Queue<E> hasQueue = getQueue(level, false);
            E element = hasQueue.poll();
            if (element != null) {
                return element;
            }
        }
        return null;
    }

    @Override
    public E element() {
        for (Integer level : levelDescList) {
            Queue<E> hasQueue = getQueue(level, false);
            E element = hasQueue.element();
            if (element != null) {
                return element;
            }
        }
        return null;
    }

    @Override
    public E peek() {
        for (Integer level : levelDescList) {
            Queue<E> hasQueue = getQueue(level, false);
            E element = hasQueue.peek();
            if (element != null) {
                return element;
            }
        }
        return null;
    }

    private Queue<E> getQueue(int level, boolean newIfAbsent) {
        Queue<E> hasQueue = levelQueueMap.get(level);
        if (hasQueue == null && newIfAbsent) {
            synchronized (levelQueueMap) {
                hasQueue = levelQueueMap.get(level);
                if (hasQueue == null) {
                    hasQueue = getQueueFactory().newQueue(getCapacity());
                    levelQueueMap.put(level, hasQueue);
                    // add new level and sort leveList desc
                    levelDescList.add(level);
                    Collections.sort(levelDescList, new Comparator<Integer>() {
                        @Override
                        public int compare(Integer o1, Integer o2) {
                            return o2.compareTo(o1);
                        }
                    });
                }
            }
        }
        return hasQueue;
    }

    public QueueFactory<E> getQueueFactory() {
        return queueFactory;
    }

    public int getCapacity() {
        return capacity;
    }

    // UnsupportedOperation
    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

}

package ru.mai.associative_container;

public interface AssociativeContainer<K extends Comparable<K>, V>  {

    void insert(K key, V value);

    V find(K key);

    void update(K key, V value);

    void delete(K key);

}

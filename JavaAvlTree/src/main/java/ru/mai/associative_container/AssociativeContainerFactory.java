package ru.mai.associative_container;

import ru.mai.associative_container.impl.AvlTreeFactor;
import ru.mai.associative_container.impl.AvlTreeHeight;

public class AssociativeContainerFactory {

    private AssociativeContainerFactory() {
    }

    public static <K extends Comparable<K>, V> AssociativeContainer<K, V> createAvlTreeHeight() {
        return new AvlTreeHeight<>();
    }

    public static <K extends Comparable<K>, V> AssociativeContainer<K, V> createAvlTreeFactor() {
        return new AvlTreeFactor<>();
    }

}

package ru.mai;

import ru.mai.associative_container.AssociativeContainer;
import ru.mai.associative_container.AssociativeContainerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    //JavaAvlTreeImplementations

    private static final Random random = new Random();

    public static void main(String[] args) {
        AssociativeContainer<Integer, Integer> avlTreeFactor = AssociativeContainerFactory.createAvlTreeFactor();
        testAvlTree();
    }

    public static void testAvlTree() {
        for (int i = 0; i < 1000; i++) {
            AssociativeContainer<Integer, Integer> avlTree = AssociativeContainerFactory.createAvlTreeFactor();
            List<Integer> keys = new ArrayList<>();

            try {
                for (int j = 0; j < 100000; j++) {
                    int key = getRandomKey();

                    if (avlTree.insert(key, 1)) {
                        keys.add(key);
                    }
                }

                for (int j = 0; j < keys.size(); j++) {
                    avlTree.delete(keys.get(j));
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.out.println(keys);
            }
        }
    }

    private static int getRandomKey() {
        return random.nextInt(100);
    }

}

/*
key = 4
key = 29
key = 86
key = 30
key = 80
key = 27
key = 16
key = 5
key = 37
key = 48
*/
package ru.mai;

import ru.mai.associative_container.AssociativeContainer;
import ru.mai.associative_container.impl.AvlTreeFactor;
import ru.mai.associative_container.impl.AvlTreeHeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    private static final Random random = new Random();

    public static void main(String[] args) {
//        AssociativeContainer<Integer, Integer> avlTree = new AvlTreeHeight<>();
//        avlTree.insert(1, 1);
//        avlTree.insert(2, 1);
//        avlTree.insert(4, 1);
//        avlTree.insert(5, 1);
//        avlTree.insert(3, 1);

        testAvlTree();
    }

    public static void testAvlTree() {
        for (int i = 0; i < 1000; i++) {
            AssociativeContainer<Integer, Integer> avlTree = new AvlTreeHeight<>();
            List<Integer> keys = new ArrayList<>();

            try {
                for (int j = 0; j < 100000; j++) {
                    int key = getRandomKey();

                    if (avlTree.insert(key, 1)) {
                        keys.add(key);
                    }
                }

//                for (int j = 0; j < keys.size(); j++) {
//                    avlTree.delete(keys.get(j));
//                }
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
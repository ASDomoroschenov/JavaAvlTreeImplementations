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
        AssociativeContainer<Integer, Integer> avlTreeHeight = AssociativeContainerFactory.createAvlTreeHeight();
        AssociativeContainer<Integer, Integer> avlTreeFactor = AssociativeContainerFactory.createAvlTreeFactor();
        System.out.print("AvlTreeHeight test - ");
        testAvlTree(avlTreeHeight);
        System.out.print("AvlTreeFactor test - ");
        testAvlTree(avlTreeFactor);
    }

    public static void testAvlTree(AssociativeContainer<Integer, Integer> avlTree) {
        try {
            List<Integer> keys = new ArrayList<>();

            for (int i = 0; i < 10000; i++) {
                int key = random.nextInt(100000);
                keys.add(key);
                avlTree.insert(key, 1);
            }

            for (Integer key : keys) {
                avlTree.delete(key);
            }
        } catch (Exception ex) {
            System.out.println("FAIL");
        }

        System.out.println("SUCCESS");
    }

}
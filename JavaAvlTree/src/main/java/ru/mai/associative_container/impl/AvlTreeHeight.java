package ru.mai.associative_container.impl;

import ru.mai.associative_container.AssociativeContainer;

import java.util.Objects;

public class AvlTreeHeight<K extends Comparable<K>, V> implements AssociativeContainer<K, V> {

    public static class NodeHeight<K extends Comparable<K>, V> {

        private K key;
        private V value;
        private int height;
        private NodeHeight<K, V> parent;
        private NodeHeight<K, V> leftSubtree;
        private NodeHeight<K, V> rightSubtree;

        public NodeHeight(K key, V value, int height) {
            this.key = key;
            this.value = value;
            this.height = height;
            this.parent = null;
            this.leftSubtree = null;
            this.rightSubtree = null;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public NodeHeight<K, V> getParent() {
            return parent;
        }

        public void setParent(NodeHeight<K, V> parent) {
            this.parent = parent;
        }

        public NodeHeight<K, V> getLeftSubtree() {
            return leftSubtree;
        }

        public void setLeftSubtree(NodeHeight<K, V> leftSubtree) {
            this.leftSubtree = leftSubtree;
        }

        public NodeHeight<K, V> getRightSubtree() {
            return rightSubtree;
        }

        public void setRightSubtree(NodeHeight<K, V> rightSubtree) {
            this.rightSubtree = rightSubtree;
        }

        @Override
        public boolean equals(Object target) {
            if (this == target) {
                return true;
            }

            if (target == null || getClass() != target.getClass()) {
                return false;
            }

            NodeHeight<?, ?> that = (NodeHeight<?, ?>) target;

            return height == that.height && Objects.equals(key, that.key) && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value, height);
        }
    }

    private NodeHeight<K, V> root;

    @Override
    public boolean insert(K key, V value) {
        if (this.root == null) {
            this.root = new NodeHeight<>(key, value, 0);
            return true;
        }

        int compare;
        NodeHeight<K, V> currentSubtree = this.root;
        NodeHeight<K, V> parentCurrentSubtree = null;
        NodeHeight<K, V> nodeToInsert = new NodeHeight<>(key, value, 0);

        while (currentSubtree != null) {
            parentCurrentSubtree = currentSubtree;
            compare = currentSubtree.getKey().compareTo(key);

            if (compare == 0) {
                return false;
            } else if (compare > 0) {
                currentSubtree = currentSubtree.getLeftSubtree();
            } else {
                currentSubtree = currentSubtree.getRightSubtree();
            }
        }

        compare = parentCurrentSubtree.getKey().compareTo(key);
        nodeToInsert.setParent(parentCurrentSubtree);

        if (compare > 0) {
            parentCurrentSubtree.setLeftSubtree(nodeToInsert);
        } else {
            parentCurrentSubtree.setRightSubtree(nodeToInsert);
        }

        balance(nodeToInsert);

        return true;
    }

    @Override
    public V find(K key) {
        NodeHeight<K, V> currentSubtree = this.root;

        while (currentSubtree != null) {
            int compare = key.compareTo(currentSubtree.getKey());

            if (compare == 0) {
                return currentSubtree.getValue();
            } else if (compare < 0) {
                currentSubtree = currentSubtree.getLeftSubtree();
            } else {
                currentSubtree = currentSubtree.getRightSubtree();
            }
        }

        return null;
    }

    @Override
    public void update(K key, V value) {
        NodeHeight<K, V> currentSubtree = this.root;

        while (currentSubtree != null) {
            int compare = key.compareTo(currentSubtree.getKey());

            if (compare == 0) {
                currentSubtree.setValue(value);
                return;
            } else if (compare < 0) {
                currentSubtree = currentSubtree.getLeftSubtree();
            } else {
                currentSubtree = currentSubtree.getRightSubtree();
            }
        }
    }

    @Override
    public void delete(K key) {
        NodeHeight<K, V> currentNode = this.root;

        while (currentNode.getKey().compareTo(key) != 0) {
            int compare = key.compareTo(currentNode.getKey());

            if (compare > 0) {
                currentNode = currentNode.getRightSubtree();
            } else {
                currentNode = currentNode.getLeftSubtree();
            }
        }

        if (currentNode.getRightSubtree() == null && currentNode.getLeftSubtree() == null) {
            deleteLeaf(currentNode);
        } else {
            NodeHeight<K, V> nodeToSwap = currentNode;

            if (currentNode.getRightSubtree() == null && currentNode.getLeftSubtree() != null) {
                currentNode = currentNode.getLeftSubtree();

                while (currentNode.getRightSubtree() != null) {
                    currentNode = currentNode.getRightSubtree();
                }
            } else {
                currentNode = currentNode.getRightSubtree();

                while (currentNode.getLeftSubtree() != null) {
                    currentNode = currentNode.getLeftSubtree();
                }
            }

            K currentNodeKey = currentNode.getKey();
            V currentNodeValue = currentNode.getValue();

            delete(currentNode.getKey());

            nodeToSwap.setKey(currentNodeKey);
            nodeToSwap.setValue(currentNodeValue);
        }
    }

    private void deleteLeaf(NodeHeight<K, V> leafToDelete) {
        NodeHeight<K, V> leafToDeleteParent = leafToDelete.getParent();

        if (leafToDeleteParent != null) {
            if (leafToDeleteParent.getLeftSubtree() == leafToDelete) {
                leafToDeleteParent.setLeftSubtree(null);
            } else {
                leafToDeleteParent.setRightSubtree(null);
            }

            balance(leafToDeleteParent);
        } else {
            this.root = null;
        }
    }

    private void balance(NodeHeight<K, V> subtree) {
        if (subtree == null) {
            return;
        }

        int heightBefore = subtree.getHeight();

        fixHeight(subtree);

        NodeHeight<K, V> parent = subtree.getParent();
        boolean isLeftSubtree = parent != null && parent.getLeftSubtree() == subtree;
        int balanceFactor = balanceFactor(subtree);

        if (balanceFactor == 2) {
            if (balanceFactor(subtree.getLeftSubtree()) < 0) {
                NodeHeight<K, V> leftRotate = leftRotate(subtree.getLeftSubtree());
                fixHeight(leftRotate.getLeftSubtree());
                fixHeight(leftRotate);
                subtree.setLeftSubtree(leftRotate);
            }

            subtree = rightRotate(subtree);
            fixHeight(subtree.getRightSubtree());
            fixHeight(subtree);
        }

        if (balanceFactor == -2) {
            if (balanceFactor(subtree.getRightSubtree()) > 0) {
                NodeHeight<K, V> rightRotate = rightRotate(subtree.getRightSubtree());
                fixHeight(rightRotate.getRightSubtree());
                fixHeight(rightRotate);
                subtree.setRightSubtree(rightRotate);
            }

            subtree = leftRotate(subtree);
            fixHeight(subtree.getLeftSubtree());
            fixHeight(subtree);
        }

        if (parent != null) {
            if (isLeftSubtree) {
                parent.setLeftSubtree(subtree);
            } else {
                parent.setRightSubtree(subtree);
            }
        } else {
            this.root = subtree;
        }

        if (heightBefore != subtree.getHeight()) {
            balance(subtree.getParent());
        }
    }

    private NodeHeight<K, V> leftRotate(NodeHeight<K, V> subtree) {
        NodeHeight<K, V> parentSubtree = subtree.getParent();
        NodeHeight<K, V> rightSubtree = subtree.getRightSubtree();

        if (parentSubtree != null) {
            if (parentSubtree.getLeftSubtree() == subtree) {
                parentSubtree.setLeftSubtree(rightSubtree);
            } else {
                parentSubtree.setRightSubtree(rightSubtree);
            }
        } else {
            this.root = rightSubtree;
        }

        rightSubtree.setParent(parentSubtree);

        if (rightSubtree.getLeftSubtree() != null) {
            rightSubtree.getLeftSubtree().setParent(subtree);
        }

        subtree.setRightSubtree(rightSubtree.getLeftSubtree());
        rightSubtree.setLeftSubtree(subtree);
        subtree.setParent(rightSubtree);

        return rightSubtree;
    }

    private NodeHeight<K, V> rightRotate(NodeHeight<K, V> subtree) {
        NodeHeight<K, V> parentSubtree = subtree.getParent();
        NodeHeight<K, V> leftSubtree = subtree.getLeftSubtree();

        if (parentSubtree != null) {
            if (parentSubtree.getLeftSubtree() == subtree) {
                parentSubtree.setLeftSubtree(leftSubtree);
            } else {
                parentSubtree.setRightSubtree(leftSubtree);
            }
        } else {
            this.root = leftSubtree;
        }

        leftSubtree.setParent(parentSubtree);

        if (leftSubtree.getRightSubtree() != null) {
            leftSubtree.getRightSubtree().setParent(subtree);
        }

        subtree.setLeftSubtree(leftSubtree.getRightSubtree());
        leftSubtree.setRightSubtree(subtree);
        subtree.setParent(leftSubtree);

        return leftSubtree;
    }

    private void fixHeight(NodeHeight<K, V> subtree) {
        if (subtree != null) {
            subtree.setHeight(1 + Integer.max(getHeight(subtree.getLeftSubtree()), getHeight(subtree.getRightSubtree())));
        }
    }

    private int getHeight(NodeHeight<K, V> subtree) {
        if (subtree != null) {
            return subtree.getHeight();
        }

        return 0;
    }

    private int balanceFactor(NodeHeight<K, V> subtree) {
        if (subtree != null) {
            return getHeight(subtree.getLeftSubtree()) - getHeight(subtree.getRightSubtree());
        }

        return 0;
    }
}

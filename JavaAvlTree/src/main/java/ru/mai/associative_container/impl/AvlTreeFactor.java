package ru.mai.associative_container.impl;

import org.apache.commons.lang3.tuple.Pair;
import ru.mai.associative_container.AssociativeContainer;

import java.util.ArrayDeque;
import java.util.Objects;

public class AvlTreeFactor<K extends Comparable<K>, V> implements AssociativeContainer<K, V> {

    public static class NodeFactor<K extends Comparable<K>, V> {

        private int factor;
        private K key;
        private V value;
        private NodeFactor<K, V> parent;
        private NodeFactor<K, V> leftSubtree;
        private NodeFactor<K, V> rightSubtree;

        public NodeFactor(K key, V value) {
            this.factor = 0;
            this.key = key;
            this.value = value;
            this.leftSubtree = null;
            this.rightSubtree = null;
        }

        public int getFactor() {
            return factor;
        }

        public void setFactor(int factor) {
            this.factor = factor;
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

        public NodeFactor<K, V> getParent() {
            return parent;
        }

        public void setParent(NodeFactor<K, V> parent) {
            this.parent = parent;
        }

        public NodeFactor<K, V> getLeftSubtree() {
            return leftSubtree;
        }

        public void setLeftSubtree(NodeFactor<K, V> leftSubtree) {
            this.leftSubtree = leftSubtree;
        }

        public NodeFactor<K, V> getRightSubtree() {
            return rightSubtree;
        }

        public void setRightSubtree(NodeFactor<K, V> rightSubtree) {
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

            NodeFactor<?, ?> that = (NodeFactor<?, ?>) target;

            return factor == that.factor && Objects.equals(key, that.key) && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(factor, key, value);
        }
    }

    private NodeFactor<K, V> root;


    @Override
    public boolean insert(K key, V value) {
        if (this.root == null) {
            this.root = new NodeFactor<>(key, value);
            return true;
        }

        NodeFactor<K, V> p = this.root;
        NodeFactor<K, V> s = this.root;
        NodeFactor<K, V> q = null;
        boolean insert = false;

        while (!insert) {
            int compare = key.compareTo(p.getKey());

            if (compare == 0) {
                return false;
            }

            q = (compare < 0) ? p.getLeftSubtree() : p.getRightSubtree();

            if (q == null) {
                q = new NodeFactor<>(key, value);
                q.setParent(p);

                if (compare < 0) {
                    p.setLeftSubtree(q);
                } else {
                    p.setRightSubtree(q);
                }

                insert = true;
            } else {
                if (q.getFactor() != 0) {
                    s = q;
                }

                p = q;
            }
        }

        balanceAfterInsert(key, s, q);

        return true;
    }

    @Override
    public V find(K key) {
        NodeFactor<K, V> currentSubtree = this.root;

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
        NodeFactor<K, V> currentSubtree = this.root;

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
        ArrayDeque<Pair<Integer, NodeFactor<K, V>>> path = new ArrayDeque<>();
        NodeFactor<K, V> currentSubtree = this.root;

        while (currentSubtree.getKey().compareTo(key) != 0) {
            int compare = key.compareTo(currentSubtree.getKey());

            path.push(Pair.of(compare, currentSubtree));

            if (compare > 0) {
                currentSubtree = currentSubtree.getRightSubtree();
            } else {
                currentSubtree = currentSubtree.getLeftSubtree();
            }
        }

        if (currentSubtree.getRightSubtree() == null && currentSubtree.getLeftSubtree() == null) {
            deleteLeaf(path, currentSubtree);
        } else {
            NodeFactor<K, V> nodeToSwap = currentSubtree;

            if (currentSubtree.getRightSubtree() == null && currentSubtree.getLeftSubtree() != null) {
                currentSubtree = currentSubtree.getLeftSubtree();

                while (currentSubtree.getRightSubtree() != null) {
                    currentSubtree = currentSubtree.getRightSubtree();
                }
            } else {
                currentSubtree = currentSubtree.getRightSubtree();

                while (currentSubtree.getLeftSubtree() != null) {
                    currentSubtree = currentSubtree.getLeftSubtree();
                }
            }

            K currentNodeKey = currentSubtree.getKey();
            V currentNodeValue = currentSubtree.getValue();

            delete(currentSubtree.getKey());

            nodeToSwap.setKey(currentNodeKey);
            nodeToSwap.setValue(currentNodeValue);
        }
    }

    private void balanceAfterInsert(K key, NodeFactor<K, V> s, NodeFactor<K, V> q) {
        if (key.compareTo(s.getKey()) < 0) {
            balanceAfterInsertInLeftSubtree(key, s, q);
        } else {
            balanceAfterInsertInRightSubtree(key, s, q);
        }
    }

    private void setFactorAfterInsert(K key, NodeFactor<K, V> p, NodeFactor<K, V> q) {
        while (!p.getKey().equals(q.getKey())) {
            if (key.compareTo(p.getKey()) < 0) {
                p.setFactor(-1);
                p = p.getLeftSubtree();
            } else {
                p.setFactor(1);
                p = p.getRightSubtree();
            }
        }
    }

    private void balanceAfterInsertInRightSubtree(K key, NodeFactor<K, V> s, NodeFactor<K, V> q) {
        NodeFactor<K, V> p;
        NodeFactor<K, V> r = s.getRightSubtree();
        p = s.getRightSubtree();

        setFactorAfterInsert(key, p, q);

        if (s.getFactor() == 0) {
            s.setFactor(1);
        } else if (s.getFactor() == -1) {
            s.setFactor(0);
        } else if (s.getFactor() == 1) {
            if (r.getFactor() == 1) {
                NodeFactor<K, V> rotate = leftRotate(s);
                rotate.setFactor(0);
                rotate.getLeftSubtree().setFactor(0);
            } else {
                if (r.getLeftSubtree().getFactor() == -1) {
                    NodeFactor<K, V> rightRotate = rightRotate(r);
                    rightRotate.setFactor(1);
                    rightRotate.getRightSubtree().setFactor(1);

                    NodeFactor<K, V> leftRotate = leftRotate(s);
                    leftRotate.setFactor(0);
                    leftRotate.getLeftSubtree().setFactor(0);
                } else if (r.getLeftSubtree().getFactor() == 1) {
                    NodeFactor<K, V> rightRotate = rightRotate(r);
                    rightRotate.setFactor(1);
                    rightRotate.getRightSubtree().setFactor(0);

                    NodeFactor<K, V> leftRotate = leftRotate(s);
                    leftRotate.setFactor(0);
                    leftRotate.getLeftSubtree().setFactor(-1);
                } else if (r.getLeftSubtree().getFactor() == 0) {
                    NodeFactor<K, V> rightRotate = rightRotate(r);
                    rightRotate.setFactor(1);
                    rightRotate.getRightSubtree().setFactor(0);

                    NodeFactor<K, V> leftRotate = leftRotate(s);
                    leftRotate.setFactor(0);
                    leftRotate.getLeftSubtree().setFactor(0);
                }
            }
        }
    }

    private void balanceAfterInsertInLeftSubtree(K key, NodeFactor<K, V> s, NodeFactor<K, V> q) {
        NodeFactor<K, V> p;
        NodeFactor<K, V> r = s.getLeftSubtree();
        p = s.getLeftSubtree();

        setFactorAfterInsert(key, p, q);

        if (s.getFactor() == 0) {
            s.setFactor(-1);
        } else if (s.getFactor() == 1) {
            s.setFactor(0);
        } else if (s.getFactor() == -1) {
            if (r.getFactor() == -1) {
                NodeFactor<K, V> rightRotate = rightRotate(s);
                rightRotate.setFactor(0);
                rightRotate.getRightSubtree().setFactor(0);
            } else if (r.getFactor() == 1) {
                if (r.getRightSubtree().getFactor() == 1) {
                    NodeFactor<K, V> leftRotate = leftRotate(r);
                    leftRotate.setFactor(-1);
                    leftRotate.getLeftSubtree().setFactor(-1);

                    NodeFactor<K, V> rightRotate = rightRotate(s);
                    rightRotate.setFactor(0);
                    rightRotate.getRightSubtree().setFactor(0);
                } else if (r.getRightSubtree().getFactor() == -1) {
                    NodeFactor<K, V> leftRotate = leftRotate(r);
                    leftRotate.setFactor(-1);
                    leftRotate.getLeftSubtree().setFactor(0);

                    NodeFactor<K, V> rightRotate = rightRotate(s);
                    rightRotate.setFactor(0);
                    rightRotate.getRightSubtree().setFactor(1);
                } else if (r.getRightSubtree().getFactor() == 0) {
                    NodeFactor<K, V> leftRotate = leftRotate(r);
                    leftRotate.setFactor(-1);
                    leftRotate.getLeftSubtree().setFactor(0);

                    NodeFactor<K, V> rightRotate = rightRotate(s);
                    rightRotate.setFactor(0);
                    rightRotate.getRightSubtree().setFactor(0);
                }
            }
        }
    }

    private void deleteLeaf(ArrayDeque<Pair<Integer, NodeFactor<K, V>>> path, NodeFactor<K, V> leafToDelete) {
        NodeFactor<K, V> leafToDeleteParent = leafToDelete.getParent();

        if (leafToDeleteParent != null) {
            if (leafToDeleteParent.getLeftSubtree() == leafToDelete) {
                leafToDeleteParent.setLeftSubtree(null);
            } else {
                leafToDeleteParent.setRightSubtree(null);
            }

            balanceAfterDeleteLeaf(path);
        } else {
            this.root = null;
        }
    }

    private void balanceAfterDeleteLeaf(ArrayDeque<Pair<Integer, NodeFactor<K, V>>> path) {
        if (path.isEmpty()) {
            return;
        }

        Pair<Integer, NodeFactor<K, V>> pathTop = path.pop();
        NodeFactor<K, V> currentSubtree = pathTop.getValue();
        int direction = pathTop.getLeft();

        if (direction == -1) {
            balanceAfterDeleteInLeftSubtree(path, currentSubtree);
        } else {
            balanceAfterDeleteInRightSubtree(path, currentSubtree);
        }
    }

    private void balanceAfterDeleteInRightSubtree(ArrayDeque<Pair<Integer, NodeFactor<K, V>>> path, NodeFactor<K, V> currentSubtree) {
        if (currentSubtree.getFactor() == 0) {
            currentSubtree.setFactor(-1);
        } else if (currentSubtree.getFactor() == 1) {
            currentSubtree.setFactor(0);
            balanceAfterDeleteLeaf(path);
        } else if (currentSubtree.getFactor() == -1) {
            NodeFactor<K, V> leftSubtree = currentSubtree.getLeftSubtree();

            if (leftSubtree.getFactor() == -1) {
                NodeFactor<K, V> rightRotate = rightRotate(currentSubtree);
                rightRotate.setFactor(0);
                rightRotate.getRightSubtree().setFactor(0);
                balanceAfterDeleteLeaf(path);
            } else if (leftSubtree.getFactor() == 1) {
                int leftSubtreeRightSubtreeFactor = leftSubtree.getRightSubtree().getFactor();
                NodeFactor<K, V> leftRotate = leftRotate(leftSubtree);
                leftRotate.setFactor(0);

                if (leftSubtreeRightSubtreeFactor == 1) {
                    leftRotate.getLeftSubtree().setFactor(-1);
                    NodeFactor<K, V> rightRotate = rightRotate(currentSubtree);
                    rightRotate.setFactor(0);
                    rightRotate.getRightSubtree().setFactor(0);
                } else if (leftSubtreeRightSubtreeFactor == -1) {
                    leftRotate.getLeftSubtree().setFactor(0);
                    NodeFactor<K, V> rightRotate = rightRotate(currentSubtree);
                    rightRotate.setFactor(0);
                    rightRotate.getRightSubtree().setFactor(1);
                } else if (leftSubtreeRightSubtreeFactor == 0) {
                    leftRotate.getLeftSubtree().setFactor(0);
                    NodeFactor<K, V> rightRotate = rightRotate(currentSubtree);
                    rightRotate.setFactor(0);
                    rightRotate.getRightSubtree().setFactor(0);
                }

                balanceAfterDeleteLeaf(path);
            } else if (leftSubtree.getFactor() == 0) {
                NodeFactor<K, V> rightRotate = rightRotate(currentSubtree);
                rightRotate.setFactor(1);
                rightRotate.getRightSubtree().setFactor(-1);
            }
        }
    }

    private void balanceAfterDeleteInLeftSubtree(ArrayDeque<Pair<Integer, NodeFactor<K, V>>> path, NodeFactor<K, V> currentSubtree) {
        if (currentSubtree.getFactor() == 0) {
            currentSubtree.setFactor(1);
        } else if (currentSubtree.getFactor() == -1) {
            currentSubtree.setFactor(0);
            balanceAfterDeleteLeaf(path);
        } else if (currentSubtree.getFactor() == 1) {
            NodeFactor<K, V> rightSubtree = currentSubtree.getRightSubtree();

            if (rightSubtree.getFactor() == 1) {
                NodeFactor<K, V> leftRotate = leftRotate(currentSubtree);
                leftRotate.setFactor(0);
                leftRotate.getLeftSubtree().setFactor(0);
                balanceAfterDeleteLeaf(path);
            } else if (rightSubtree.getFactor() == -1) {
                int rightSubtreeLeftSubtreeFactor = rightSubtree.getLeftSubtree().getFactor();
                NodeFactor<K, V> rightRotate = rightRotate(rightSubtree);
                rightRotate.setFactor(0);

                if (rightSubtreeLeftSubtreeFactor == -1) {
                    rightRotate.getRightSubtree().setFactor(1);
                    NodeFactor<K, V> leftRotate = leftRotate(currentSubtree);
                    leftRotate.setFactor(0);
                    leftRotate.getLeftSubtree().setFactor(0);
                } else if (rightSubtreeLeftSubtreeFactor == 1) {
                    rightRotate.getRightSubtree().setFactor(0);
                    NodeFactor<K, V> leftRotate = leftRotate(currentSubtree);
                    leftRotate.setFactor(0);
                    leftRotate.getLeftSubtree().setFactor(-1);
                } else if (rightSubtreeLeftSubtreeFactor == 0) {
                    rightRotate.getRightSubtree().setFactor(0);
                    NodeFactor<K, V> leftRotate = leftRotate(currentSubtree);
                    leftRotate.setFactor(0);
                    leftRotate.getLeftSubtree().setFactor(0);
                }

                balanceAfterDeleteLeaf(path);
            } else if (rightSubtree.getFactor() == 0) {
                NodeFactor<K, V> leftRotate = leftRotate(currentSubtree);
                leftRotate.setFactor(-1);
                leftRotate.getLeftSubtree().setFactor(1);
            }
        }
    }

    private NodeFactor<K, V> leftRotate(NodeFactor<K, V> subtree) {
        NodeFactor<K, V> parentSubtree = subtree.getParent();
        NodeFactor<K, V> rightSubtree = subtree.getRightSubtree();

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

    private NodeFactor<K, V> rightRotate(NodeFactor<K, V> subtree) {
        NodeFactor<K, V> parentSubtree = subtree.getParent();
        NodeFactor<K, V> leftSubtree = subtree.getLeftSubtree();

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
}

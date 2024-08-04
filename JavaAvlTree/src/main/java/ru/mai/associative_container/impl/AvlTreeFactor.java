package ru.mai.associative_container.impl;

import org.apache.commons.lang3.tuple.Pair;
import ru.mai.associative_container.AssociativeContainer;

import java.util.ArrayDeque;
import java.util.Deque;
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
    public void insert(K key, V value) {
        if (this.root == null) {
            this.root = new NodeFactor<>(key, value);
            return;
        }

        NodeFactor<K, V> q = null;
        NodeFactor<K, V> s = this.root;
        NodeFactor<K, V> p = this.root;
        boolean isInserted = false;
        boolean insertInLeftSubtree = false;

        while (!isInserted) {
            if (key.compareTo(p.getKey()) == 0) {
                return;
            }

            if (key.compareTo(p.getKey()) < 0) {
                q = p.getLeftSubtree();

                if (q == null) {
                    q = new NodeFactor<>(key, value);
                    q.setParent(p);
                    p.setLeftSubtree(q);
                    isInserted = true;
                    insertInLeftSubtree = true;
                } else {
                    if (p.getFactor() != 0) {
                        s = p;
                    }
                    p = q;
                }
            } else {
                q = p.getRightSubtree();

                if (q == null) {
                    q = new NodeFactor<>(key, value);
                    q.setParent(p);
                    p.setRightSubtree(q);
                    isInserted = true;
                } else {
                    if (p.getFactor() != 0) {
                        s = p;
                    }
                    p = q;
                }
            }
        }

        balance(key, s, q, insertInLeftSubtree);
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
        Deque<Pair<Integer, NodeFactor<K, V>>> path = new ArrayDeque<>();
        NodeFactor<K, V> currentSubtree = this.root;

        currentSubtree = getNodeWithPath(key, path, currentSubtree);

        if (currentSubtree == null) {
            return;
        }

        if (currentSubtree.getLeftSubtree() == null && currentSubtree.getRightSubtree() == null) {
            NodeFactor<K, V> parent = currentSubtree.getParent();

            if (parent != null) {
                if (parent.getLeftSubtree() == currentSubtree) {
                    parent.setLeftSubtree(null);
                } else {
                    parent.setRightSubtree(null);
                }
            }

            balanceAfterDeleteLeaf(path);
        } else {
            if (currentSubtree.getRightSubtree() == null) {
                NodeFactor<K, V> leftSubtree = currentSubtree.getLeftSubtree();
                deleteLeaf(path, currentSubtree, leftSubtree);
            } else if (currentSubtree.getLeftSubtree() == null) {
                NodeFactor<K, V> rightSubtree = currentSubtree.getRightSubtree();
                deleteLeaf(path, currentSubtree, rightSubtree);
            } else {
                NodeFactor<K, V> successor = currentSubtree.getRightSubtree();

                while (successor.getLeftSubtree() != null) {
                    successor = successor.getLeftSubtree();
                }

                deleteLeaf(path, currentSubtree, successor);
            }
        }
    }

    private void deleteLeaf(Deque<Pair<Integer, NodeFactor<K, V>>> path, NodeFactor<K, V> currentSubtree, NodeFactor<K, V> leftSubtree) {
        K leftSubtreeKey = leftSubtree.getKey();
        V leftSubtreeValue = leftSubtree.getValue();

        leftSubtree.setKey(currentSubtree.getKey());
        leftSubtree.setValue(currentSubtree.getValue());

        currentSubtree.setKey(leftSubtreeKey);
        currentSubtree.setValue(leftSubtreeValue);

        deleteLeafInner(leftSubtree.getKey(), path, currentSubtree);
    }

    private void deleteLeafInner(K key, Deque<Pair<Integer, NodeFactor<K, V>>> path, NodeFactor<K, V> subtree) {
        NodeFactor<K, V> currentSubtree = subtree;

        currentSubtree = getNodeWithPath(key, path, currentSubtree);
        if (currentSubtree == null) return;

        NodeFactor<K, V> parent = currentSubtree.getParent();

        if (parent != null) {
            if (parent.getLeftSubtree() == currentSubtree) {
                parent.setLeftSubtree(null);
            } else {
                parent.setRightSubtree(null);
            }
        }

        balanceAfterDeleteLeaf(path);
    }

    private NodeFactor<K, V> getNodeWithPath(K key, Deque<Pair<Integer, NodeFactor<K, V>>> path, NodeFactor<K, V> currentSubtree) {
        while (currentSubtree != null && !currentSubtree.getKey().equals(key)) {
            if (key.compareTo(currentSubtree.getKey()) < 0) {
                path.push(Pair.of(-1, currentSubtree));
                currentSubtree = currentSubtree.getLeftSubtree();
            } else {
                path.push(Pair.of(1, currentSubtree));
                currentSubtree = currentSubtree.getRightSubtree();
            }
        }

        return currentSubtree;
    }

    private void balanceAfterDeleteLeaf(Deque<Pair<Integer, NodeFactor<K, V>>> path) {
        if (path.isEmpty()) {
            return;
        }

        Pair<Integer, NodeFactor<K, V>> topPath = path.pop();

        if (topPath.getLeft() == topPath.getRight().getFactor()) {
            topPath.getRight().setFactor(0);
            balanceAfterDeleteLeaf(path);
        } else if (topPath.getRight().getFactor() == 0) {
            topPath.getRight().setFactor(-topPath.getLeft());
        } else if (topPath.getLeft() == -topPath.getRight().getFactor()) {
            if (topPath.getRight().getFactor() == -1) {
                NodeFactor<K, V> leftSubtree = topPath.getRight().getLeftSubtree();

                if (leftSubtree.getFactor() == 0) {
                    NodeFactor<K, V> parent = topPath.getRight().getParent();
                    boolean inLeftSubtree = parent != null && parent.getLeftSubtree() == topPath.getRight();
                    NodeFactor<K, V> rotate = rightRotate(topPath.getRight());

                    rotate.setFactor(1);
                    rotate.getRightSubtree().setFactor(-1);

                    if (parent != null) {
                        if (inLeftSubtree) {
                            parent.setLeftSubtree(rotate);
                        } else {
                            parent.setRightSubtree(rotate);
                        }
                    }
                } else {
                    NodeFactor<K, V> subtree = leftSubtree.getLeftSubtree();

                    if (subtree == null) {
                        subtree = leftSubtree.getRightSubtree();
                        balance(subtree.getKey(), topPath.getRight(), subtree, false);
                    } else {
                        balance(subtree.getKey(), topPath.getRight(), subtree, true);
                    }
                }
            } else {
                NodeFactor<K, V> rightSubtree = topPath.getRight().getRightSubtree();

                if (rightSubtree.getFactor() == 0) {
                    NodeFactor<K, V> parent = topPath.getRight().getParent();
                    boolean inLeftSubtree = parent != null && parent.getLeftSubtree() == topPath.getRight();
                    NodeFactor<K, V> rotate = leftRotate(topPath.getRight());

                    rotate.setFactor(-1);
                    rotate.getLeftSubtree().setFactor(1);

                    if (parent != null) {
                        if (inLeftSubtree) {
                            parent.setLeftSubtree(rotate);
                        } else {
                            parent.setRightSubtree(rotate);
                        }
                    }
                } else {
                    NodeFactor<K, V> subtree = rightSubtree.getRightSubtree();

                    if (subtree == null) {
                        subtree = rightSubtree.getLeftSubtree();
                        balance(subtree.getKey(), topPath.getRight(), subtree, true);
                    } else {
                        balance(subtree.getKey(), topPath.getRight(), subtree, false);
                    }
                }
            }
        }
    }

    private void balance(K key, NodeFactor<K, V> s, NodeFactor<K, V> q, boolean insertInLeftSubtree) {
        NodeFactor<K, V> p;
        if (key.compareTo(s.getKey()) < 0) {
            NodeFactor<K, V> r = s.getLeftSubtree();
            p = s.getLeftSubtree();

            while (p != q) {
                if (key.compareTo(p.getKey()) < 0) {
                    p.setFactor(-1);
                    p = p.getLeftSubtree();
                } else {
                    p.setFactor(1);
                    p = p.getRightSubtree();
                }
            }

            if (s.getFactor() == 1) {
                s.setFactor(0);
            } else {
                if (r.getFactor() == -1) {
                    rightRotateAndBalance(s);
                } else if (r.getFactor() == 1) {
                    NodeFactor<K, V> rotateR = leftRotate(r);

                    if (insertInLeftSubtree) {
                        rotateR.getLeftSubtree().setFactor(0);
                    } else {
                        rotateR.getLeftSubtree().setFactor(-1);
                    }

                    s.setLeftSubtree(rotateR);

                    rightRotateAndBalance(s);
                }
            }
        } else {
            NodeFactor<K, V> r = s.getRightSubtree();
            p = s.getRightSubtree();

            while (p != q) {
                if (key.compareTo(p.getKey()) < 0) {
                    p.setFactor(-1);
                    p = p.getLeftSubtree();
                } else {
                    p.setFactor(1);
                    p = p.getRightSubtree();
                }
            }

            if (s.getFactor() == -1) {
                s.setFactor(0);
            } else {
                if (r.getFactor() == 1) {
                    leftRotateAndBalance(s);
                } else if (r.getFactor() == -1) {
                    NodeFactor<K, V> rotateR = rightRotate(r);

                    if (!insertInLeftSubtree) {
                        rotateR.getRightSubtree().setFactor(0);
                    } else {
                        rotateR.getRightSubtree().setFactor(1);
                    }

                    s.setRightSubtree(rotateR);

                    leftRotateAndBalance(s);
                }
            }
        }
    }

    private void rightRotateAndBalance(NodeFactor<K, V> s) {
        NodeFactor<K, V> parent = s.getParent();
        boolean isLeftSubtree = parent != null && parent.getLeftSubtree() == s;

        NodeFactor<K, V> rotate = rightRotate(s);
        rotate.setFactor(0);
        rotate.getRightSubtree().setFactor(0);

        if (parent != null) {
            if (isLeftSubtree) {
                parent.setLeftSubtree(rotate);
            } else {
                parent.setRightSubtree(rotate);
            }
        }
    }

    private void leftRotateAndBalance(NodeFactor<K, V> s) {
        NodeFactor<K, V> parent = s.getParent();
        boolean isRightSubtree = parent != null && parent.getRightSubtree() == s;

        NodeFactor<K, V> rotate = leftRotate(s);
        rotate.setFactor(0);
        rotate.getLeftSubtree().setFactor(0);

        if (parent != null) {
            if (isRightSubtree) {
                parent.setRightSubtree(rotate);
            } else {
                parent.setLeftSubtree(rotate);
            }
        }
    }

    private NodeFactor<K, V> leftRotate(NodeFactor<K, V> subtree) {
        NodeFactor<K, V> rightSubtree = subtree.getRightSubtree();
        subtree.setRightSubtree(rightSubtree.getLeftSubtree());

        if (rightSubtree.getLeftSubtree() != null) {
            rightSubtree.getLeftSubtree().setParent(subtree);
        }

        rightSubtree.setLeftSubtree(subtree);
        rightSubtree.setParent(subtree.getParent());
        subtree.setParent(rightSubtree);

        return rightSubtree;
    }

    private NodeFactor<K, V> rightRotate(NodeFactor<K, V> subtree) {
        NodeFactor<K, V> leftSubtree = subtree.getLeftSubtree();
        subtree.setLeftSubtree(leftSubtree.getRightSubtree());

        if (leftSubtree.getRightSubtree() != null) {
            leftSubtree.getRightSubtree().setParent(subtree);
        }

        leftSubtree.setRightSubtree(subtree);
        leftSubtree.setParent(subtree.getParent());
        subtree.setParent(leftSubtree);

        return leftSubtree;
    }
}

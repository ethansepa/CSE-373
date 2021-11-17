package disjointsets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;

/**
 * A quick-union-by-size data structure with path compression.
 * @see DisjointSets for more documentation.
 */
public class UnionBySizeCompressingDisjointSets<T> implements DisjointSets<T> {
    // Do NOT rename or delete this field. We will be inspecting it directly in our private tests.
    List<Integer> pointers;
    Map<T, Integer> itemToParent;
    /*
    However, feel free to add more fields and private helper methods. You will probably need to
    add one or two more fields in order to successfully implement this class.
    */

    public UnionBySizeCompressingDisjointSets() {
        pointers = new ArrayList<>();
        itemToParent = new HashMap<>();
    }

    @Override
    public void makeSet(T item) {
        pointers.add(-1);
        int index = pointers.size() - 1;
        itemToParent.put(item, index);
    }

    @Override
    public int findSet(T item) throws IllegalArgumentException {
        if (!itemToParent.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        Set<Integer> pathComp = new HashSet<>();
        int index = itemToParent.get(item);
        int nextIndex = pointers.get(index);
        while (nextIndex >= 0) {
            pathComp.add(index);
            index = nextIndex;
            nextIndex = pointers.get(nextIndex);
        }
        for (Integer point : pathComp) {
            pointers.set(point, index);
        }
        return index;
    }

    @Override
    public boolean union(T item1, T item2) {
        int root1 = findSet(item1);
        int root2 = findSet(item2);
        if (root1 == root2) {
            return false;
        }
        int size1 = pointers.get(root1);
        int size2 = pointers.get(root2);
        if (size1 <= size2) {
            pointers.set(root2, root1);
            pointers.set(root1, size1 + size2);
        } else {
            pointers.set(root1, root2);
            pointers.set(root2, size1 + size2);
        }
        return true;
    }
}

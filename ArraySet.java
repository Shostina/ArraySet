import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

public class ArraySet<T> extends AbstractSet<T> implements SortedSet<T> {

    private final List<T> sortedData;
    private final Comparator<? super T> comparator;

    private ArraySet(List<T> list, Comparator<? super T> comp) {
        sortedData = list;
        comparator = comp;
    }

    public ArraySet() {
        this(Collections.emptyList(), null);
    }

    public ArraySet(Collection<? extends T> collection, Comparator<? super T> comp) {
        comparator = comp;
        TreeSet<T> set = new TreeSet<>(comp);
        set.addAll(Objects.requireNonNull(collection));
        sortedData = new ArrayList<>(set);
    }

    public ArraySet(Collection<? extends T> collection) {
        this(collection, null);
    }

    public ArraySet(ArraySet<T> other) {
        sortedData = other.sortedData;
        comparator = other.comparator;
    }

    private static final String setIsEmptyMessage = "ArraySet is empty, cannot get ";

    public T first() {
        if (!sortedData.isEmpty()) {
            return sortedData.get(0);
        }
        throw new NoSuchElementException(setIsEmptyMessage + "first element");
    }

    public T last() {
        if (!sortedData.isEmpty()) {
            return sortedData.get(sortedData.size() - 1);
        }
        throw new NoSuchElementException(setIsEmptyMessage + "last element");
    }

    public int size() {
        return sortedData.size();
    }

    @Override
    public boolean contains(Object o) {
        return Collections.binarySearch(sortedData, (T) o, comparator) >= 0;
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableList(sortedData).iterator();
    }

    private int searchForPosition(T element, int addIfFound, int addIfNotFound) {
        int index = Collections.binarySearch(sortedData, element, comparator);
        if (index >= 0) {
            return index + addIfFound;
        }
        index = -(index + 1);
        return index + addIfNotFound;
    }

    private SortedSet<T> subSet(T fromElement, T toElement, boolean toInclusive) {
        int addIfFound = 0;
        int addIfNotFound = 0;
        int leftBorder = searchForPosition(fromElement, addIfFound, addIfNotFound);
        if(toInclusive){
            addIfFound = 0;
        } else {
            addIfFound = -1;
        }
        addIfNotFound = -1;
        int rightBorder = searchForPosition(toElement, addIfFound, addIfNotFound) + 1;

        if (leftBorder - rightBorder >= 0) {
            return Collections.emptySortedSet();
        }
        return new ArraySet<T>(sortedData.subList(leftBorder, rightBorder), comparator);
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return subSet(fromElement, toElement, false);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        if (sortedData.isEmpty()) {
            return Collections.emptyNavigableSet();
        }
        return subSet(fromElement,  last(), true);
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        if (sortedData.isEmpty()) {
            return Collections.emptyNavigableSet();
        }
        return subSet(first(),  toElement, false);
    }

    @Override
    public Comparator<? super T> comparator() {
        return comparator;
    }
}
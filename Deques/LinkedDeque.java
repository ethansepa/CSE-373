package deques;

public class LinkedDeque<T> extends AbstractDeque<T> {
    private int size;
    // IMPORTANT: Do not rename these fields or change their visibility.
    // We access these during grading to test your code.
    Node<T> front;
    Node<T> back;
    // Feel free to add any additional fields you may need, though.
    public LinkedDeque() {
        size = 0;
        front = new Node<>(null, null, null);
        back = new Node<>(null, front, null);
        front.next = back;
    }

    public void addFirst(T item) {
        size += 1;
        front.next = new Node<>(item, front, front.next);
        front.next.next.prev = front.next;
    }

    public void addLast(T item) {
        size += 1;
        back.prev = new Node<>(item, back.prev, back);
        back.prev.prev.next = back.prev;
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        size -= 1;

        Node<T> retval = front.next;
        front.next = front.next.next;
        front.next.prev = front;

        return retval.value;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        size -= 1;

        Node<T> retval = back.prev;
        back.prev = back.prev.prev;
        back.prev.next = back;

        return retval.value;
    }

    public T get(int index) {
        if ((index >= size) || (index < 0)) {
            return null;
        }

        Node<T> curr;
        int currIndex = 0;
        if (index < size / 2) { //iterate from front
            curr = front.next;
            while (currIndex != index) {
                curr = curr.next;
                currIndex++;
            }
        } else { //iterate from back
            curr = back.prev;
            currIndex = size - 1;
            while (currIndex != index) {
                curr = curr.prev;
                currIndex--;
            }
        }
        return curr.value;
    }

    public int size() {
        return size;
    }
}

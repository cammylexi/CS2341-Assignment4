import java.util.LinkedList;
import java.util.Queue;

public class LinearProbingHashST<Key, Value> {
    private static final int INIT_CAPACITY = 20000;
    private int n;           // number of key-value pairs
    private int m;           // size of linear probing table
    private Key[] keys;      // the keys
    private Value[] vals;    // the values
    private int comparisons; // track number of comparisons

    // Constructor with default capacity
    public LinearProbingHashST() {
        this(INIT_CAPACITY);
    }

    // Constructor with specified capacity
    public LinearProbingHashST(int capacity) {
        m = capacity;
        n = 0;
        keys = (Key[]) new Object[m];
        vals = (Value[]) new Object[m];
    }

    public int size() {
        return n;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean contains(Key key, int hashType) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        return get(key, hashType) != null;
    }

    // Old Hashcode is HashType 1
    private int hashType1(Key key) {
        int hash = 0;
        int skip = Math.max(1, key.toString().length() / 8);
        for (int i = 0; i < key.toString().length(); i += skip) {
            hash = (hash * 37) + key.toString().charAt(i);
        }
        return (hash & 0x7fffffff) % m;
    }

    // New Hashcode is HashType 2
    private int hashType2(Key key) {
        int hash = 0;
        for (int i = 0; i < key.toString().length(); i++) {
            hash = (hash * 31) + key.toString().charAt(i);
        }
        return (hash & 0x7fffffff) % m;
    }

    // Resize the hash table
    private void resize(int capacity, int hashType) {
        LinearProbingHashST<Key, Value> temp = new LinearProbingHashST<>(capacity);
        for (int i = 0; i < m; i++) {
            if (keys[i] != null) {
                temp.put(keys[i], vals[i], hashType);
            }
        }
        keys = temp.keys;
        vals = temp.vals;
        m = temp.m;
    }

    // Get method with cost tracking
    public Value get(Key key, int hashType) {
        comparisons = 0; // Reset comparison counter
        int hash = (hashType == 1) ? hashType1(key) : hashType2(key);
        for (int i = hash; keys[i] != null; i = (i + 1) % m) {
            comparisons++;
            if (keys[i].equals(key)) {
                return vals[i];
            }
        }
        return null;
    }

    public int getComparisons() {
        return comparisons;
    }

    // Insert key-value pair
    public void put(Key key, Value val, int hashType) {
        if (n >= m / 2) resize(2 * m, hashType); // Resize if table is half full
        int hash = (hashType == 1) ? hashType1(key) : hashType2(key);
        int i;
        for (i = hash; keys[i] != null; i = (i + 1) % m) {
            if (keys[i].equals(key)) {
                vals[i] = val;
                return;
            }
        }
        keys[i] = key;
        vals[i] = val;
        n++;
    }

    // Delete a key
    public void delete(Key key, int hashType) {
        if (key == null) throw new IllegalArgumentException("argument to delete() is null");
        if (!contains(key, hashType)) return;

        // Find position of key
        int hash = (hashType == 1) ? hashType1(key) : hashType2(key);
        int i = hash;
        while (!key.equals(keys[i])) {
            i = (i + 1) % m;
        }

        // Delete key and associated value
        keys[i] = null;
        vals[i] = null;

        // Rehash all keys in the same cluster
        i = (i + 1) % m;
        while (keys[i] != null) {
            Key keyToRehash = keys[i];
            Value valToRehash = vals[i];
            keys[i] = null;
            vals[i] = null;
            n--;
            put(keyToRehash, valToRehash, hashType);
            i = (i + 1) % m;
        }

        n--;

        // Halve size of array if it's 12.5% full or less
        if (n > 0 && n <= m / 8) resize(m / 2, hashType);
    }

    public Iterable<Key> keys() {
        Queue<Key> queue = new LinkedList<>();
        for (int i = 0; i < m; i++) {
            if (keys[i] != null) queue.add(keys[i]);
        }
        return queue;
    }
}

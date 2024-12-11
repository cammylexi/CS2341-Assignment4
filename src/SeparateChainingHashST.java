import java.util.LinkedList;
import java.util.Queue;
class SeparateChainingHashST<Key, Value> {
    private int M; // hash table size
    private SequentialSearchST<Key, Value>[] st; // array of linked lists
    private int comparisons; // track comparisons

    public SeparateChainingHashST(int M) {
        this.M = M;
        st = (SequentialSearchST<Key, Value>[]) new SequentialSearchST[M];
        for (int i = 0; i < M; i++)
            st[i] = new SequentialSearchST<>();
    }

    // Old HashCode
    private int hashType1(Key key) {
        int hash = 0;
        int skip = Math.max(1, key.toString().length() / 8);
        for (int i = 0; i < key.toString().length(); i += skip)
            hash = (hash * 37) + key.toString().charAt(i);
        return (hash & 0x7fffffff) % M;
    }

    //New HashCode
    private int hashType2(Key key) {
        int hash = 0;
        for (int i = 0; i < key.toString().length(); i++)
            hash = (hash * 31) + key.toString().charAt(i);
        return (hash & 0x7fffffff) % M;
    }

    public Value get(Key key, int hashType) {
        comparisons = 0; // Reset comparison counter
        int bucket = hashType == 1 ? hashType1(key) : hashType2(key);
        for (Key k : st[bucket].keys()) {
            comparisons++;
            if (k.equals(key))
                return st[bucket].get(key);
        }
        return null;
    }

    public void put(Key key, Value val, int hashType) {
        int bucket = hashType == 1 ? hashType1(key) : hashType2(key);
        st[bucket].put(key, val);
    }

    public int getComparisons() {
        return comparisons;
    }
}
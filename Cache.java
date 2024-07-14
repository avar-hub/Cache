import java.util.HashMap;
import java.util.Map;

public class Cache {

    private final Storage storage;
    private final EvictionPolicy evictionPolicy;

    public Cache(EvictionPolicy evictionPolicy , Storage storage) {
        this.evictionPolicy = evictionPolicy;
        this.storage = storage;
    }

    public void put(String key , String value) {
        if(storage.containsKey(key)) {
            storage.updateKey(key , value);
            evictionPolicy.keyAccessed(key , value);
        }
        else {
            if(storage.isFull()) {
                String leastUsedKey = evictionPolicy.evict();
                storage.remove(leastUsedKey);
            }
            storage.add(key , value);
            evictionPolicy.keyAccessed(key , value);
        }
    }

    public String get(String key) {

        if(storage.containsKey(key)) {
            evictionPolicy.keyAccessed(key , storage.getKey(key));
            return storage.getKey(key);
        }
        return null;
    }

    public static void main(String[] args) {

        Storage storage1 = new Storage(2);
        EvictionPolicy evictionPolicy1 = new EvictionPolicy();
        Cache cache = new Cache(evictionPolicy1 , storage1 );

        cache.put("1", "one");
        cache.put("2", "two");
        System.out.println(cache.get("1"));
        cache.put("3", "three");
        System.out.println(cache.get("2"));
        System.out.println(cache.get("3"));
    }

}

class Storage {

    Map<String , String> storage;
    private final Integer capacity;

    public Storage(int capacity) {
        this.capacity = capacity;
        this. storage = new HashMap<>();
    }

    public void add(String key , String value) {
        storage.put(key,value);
        System.out.println("Data added: " + key + " " + value);
    }

    public void remove(String key) {
        storage.remove(key);
        System.out.println("Data removed: " + key);
    }

    public String getKey(String key) {
        return storage.get(key);
    }

    public void updateKey(String key , String value) {
        storage.put(key , value);
    }

    public boolean isFull() {
        return capacity <= storage.size();
    }

    public boolean containsKey(String key) {
        return storage.containsKey(key);
    }
}

class EvictionPolicy {

    private final Map<String, Node> nodeMap;
    private final DoubleLinkedList dll;

    public EvictionPolicy(){
        this.nodeMap = new HashMap<>();
        this.dll = new DoubleLinkedList();
    }

    public void keyAccessed(String key , String value) {
        Node node = nodeMap.get(key);
        if(node != null) {
            dll.moveToHead(node);
        }
        else {
            Node newNode = new Node(key,value);
            dll.addToHead(newNode);
            nodeMap.put(key,newNode);
        }
    }

    public String evict() {
        Node node = dll.removeTail();
        if(node != null) {
            nodeMap.remove(node.key);
            return node.key;
        }
        return null;
    }

}

class Node {

    String key;
    String value;
    Node next;
    Node prev;
    public Node(String key, String value) {
        this.key = key;
        this.value = value;
    }
}

class DoubleLinkedList {
    private Node head;
    private Node tail;

    public DoubleLinkedList() {
        this.head = new Node(null,null);
        this.tail = new Node(null, null);
        head.next = tail;
        tail.prev = head;
    }

    public void addToHead(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    public void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }
    public Node removeTail() {
        Node node = tail.prev;
        removeNode(node);
        return node;
    }

    public void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
}

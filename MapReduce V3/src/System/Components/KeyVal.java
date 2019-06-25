package System.Components;

import java.io.Serializable;

public class KeyVal<T,V> implements Serializable {

    private final T Key;
    private V Value;


    public KeyVal(T Key, V Val){
        this.Key = Key;
        this.Value = Val;
    }

    public T getKey() {
        return Key;
    }

    public V getValue() {
        return Value;
    }

    public void setValue(V value) {
        Value = value;
    }


}

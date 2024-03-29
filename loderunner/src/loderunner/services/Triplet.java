package loderunner.services;

public class Triplet<T, U, V> {

    private T first;
    private  U second;
    private  V third;

    public Triplet(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T getFirst() { return first; }
    public U getSecond() { return second; }
    public V getThird() { return third; }
    public void setFirst(T e) { first = e; }
    public void setSecond(U e) {  second = e; }
    public void setThird(V e) {  third = e ; }
}
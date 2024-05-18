import java.util.Comparator;

public class Pair<T1, T2> {
    private T1 first;
    private T2 second;
    Pair(T1 first, T2 second){
        this.first = first;
        this.second = second;
    }

    public T2 getSecond() {
        return second;
    }

    public void setSecond(T2 finish) {
        this.second = finish;
    }

    public T1 getFirst() {
        return first;
    }

    public void setFirst(T1 start) {
        this.first = start;
    }
    @Override
    public String toString(){
        return this.first+" "+this.second;
    }
}
class SortBySecond implements Comparator<Pair<Integer,Integer>> {

    public int compare(Pair<Integer,Integer> first, Pair<Integer,Integer> second) {
        return first.getSecond()-second.getSecond();
    }
}
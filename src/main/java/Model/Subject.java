package Model;

public interface Subject {
     void register(Observer o);
     void unregister(Observer o);
     void notifyAllObservers(int s);
}

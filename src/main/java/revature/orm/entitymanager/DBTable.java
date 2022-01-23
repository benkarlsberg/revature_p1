package revature.orm.entitymanager;

import java.util.List;
import java.util.function.Predicate;

public class DBTable<E> {
    Class clazz;
    public boolean createTable(){
        return false;
    }
    public E insertInto(E entity){
        return entity;
    }

    public E update(int primaryKey,E entity){
        return entity;
    }

    public E delete(int primaryKey){
        return null;
    }
    public E get(int primaryKey){
        return null;
    }
    public List<E> get(Predicate... predicate){
        return null;
    }
}

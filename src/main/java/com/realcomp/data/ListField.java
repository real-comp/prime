package com.realcomp.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author krenfro
 */
public class ListField extends Field<List<Field>> implements List<Field> {

    protected List<Field> wrapped;
    protected String name;

    public ListField(){
        wrapped = new ArrayList<Field>();
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public void setName(String name){
        this.name = name;
    }

    @Override
    public int size() {
        return wrapped.size();
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return wrapped.contains(o);
    }

    @Override
    public Iterator<Field> iterator() {
        return wrapped.iterator();
    }

    @Override
    public Object[] toArray() {
        return wrapped.toArray();
    }

    @Override
    public <Field> Field[] toArray(Field[] a) {
        return wrapped.toArray(a);
    }

    @Override
    public boolean add(Field e) {
        return wrapped.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return wrapped.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return wrapped.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Field> c) {
        return wrapped.addAll(c);
    }

    @Override
    public boolean addAll(int index,
                          Collection<? extends Field> c) {
        return wrapped.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return wrapped.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return wrapped.retainAll(c);
    }

    @Override
    public void clear() {
        wrapped.clear();
    }

    @Override
    public Field get(int index) {
        return wrapped.get(index);
    }

    @Override
    public Field set(int index, Field element) {
        return wrapped.set(index, element);
    }

    @Override
    public void add(int index, Field element) {
        wrapped.add(index, element);
    }

    @Override
    public Field remove(int index) {
        return wrapped.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return wrapped.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return wrapped.lastIndexOf(o);
    }

    @Override
    public ListIterator<Field> listIterator() {
        return wrapped.listIterator();
    }

    @Override
    public ListIterator<Field> listIterator(int index) {
        return wrapped.listIterator(index);
    }

    @Override
    public List<Field> subList(int fromIndex, int toIndex) {
        return wrapped.subList(fromIndex, toIndex);
    }

    @Override
    public DataType getType() {
        return DataType.LIST;
    }

    @Override
    public List<Field> getValue() {
        return wrapped;
    }

    @Override
    public void setValue(List<Field> value) {
        if (value == null)
            throw new IllegalArgumentException("value is null");
        wrapped = value;
    }

    @Override
    public Field get(String key) {
        try{
            return wrapped.get(Integer.valueOf(key));
        }
        catch(NumberFormatException e){
            return new NullField();
        }
    }

}

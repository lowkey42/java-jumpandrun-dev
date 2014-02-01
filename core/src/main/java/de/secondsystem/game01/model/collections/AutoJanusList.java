package de.secondsystem.game01.model.collections;

import java.util.ArrayList;
import java.util.List;

public final class AutoJanusList<E> {

	public interface EntryFactory<E> {
		E create(int index);
	}
	
	private final List<E> pList;
	private final List<E> nList;
	private final EntryFactory<E> factory;
	
	public AutoJanusList(EntryFactory<E> factory) {
		this.factory = factory;
		pList = new ArrayList<>();
		nList = new ArrayList<>();
	}
	
	public AutoJanusList(EntryFactory<E> factory, int estimatedMin, int estimatedMax) {
		this.factory = factory;
		pList = new ArrayList<>(estimatedMax);
		nList = new ArrayList<>(-estimatedMin);
	}

	public E get(int index, boolean create) {
		return index<0 ? getN(index, create) : getP(index, create);
	}
	protected E getP(int index, boolean create) {
		if( index<pList.size() )
			return pList.get(index);

		if( !create )
			return null;
		
		E e = null;
		for(int i=pList.size(); i<=index; ++i)
			pList.add(e=factory.create(i));
		
		return e;
	}
	protected E getN(int index, boolean create) {
		if( -index<nList.size() )
			return nList.get(-index);

		if( !create )
			return null;

		E e = null;
		for(int i=nList.size(); i<=-index; ++i)
			nList.add(e=factory.create(-i));
		
		return e;
	}
	
}

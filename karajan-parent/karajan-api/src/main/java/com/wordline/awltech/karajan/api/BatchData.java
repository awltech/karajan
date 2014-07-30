package com.wordline.awltech.karajan.api;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
/**
 * This is the unit of process exchanged between steps. It allow streaming in batch
 * mode. Each batch has a id that is used to identify it. So when error occurs during 
 * the processing of the Batch data, the runtime can recover it.
 * @author Thierno Saidou Barry
 *
 * @param <T> 
 * 			type of element that are contained in the BatchData
 */
public class BatchData<T> implements Serializable{

	
	private static final long serialVersionUID = 1676791114394276987L;
	/**
	 * Id used to identify the BatchData
	 */
	private  String id;
	private List<T> data;
	
	public BatchData(){
		this.id= UUID.randomUUID().toString();
		data=new LinkedList<T>();
	}
	
	public BatchData(List<T> data) {
		this.id= UUID.randomUUID().toString();
		this.data=data;
	}
	/**
	 * We need to clone only the id
	 * @param batch
	 */
	public BatchData(BatchData<?> batch){
		data=new LinkedList<T>();
		this.id=batch.getId();
	}
	
	/**
	 * 
	 * @return Iterator<T> 
	 * 					to iterate through BatchData
	 */
	public Iterator<T> getDataIterator(){
		return this.data.iterator();
	}
	/**
	 * 
	 * @return int 
	 * 			that represents the number of element contained in the BatchDta
	 */
	public int getSize(){
		return this.data.size();
	}
	
	/**
	 * 
	 * @return List<T>
	 * 				return data
	 */
	public List<T> getData(){
		return this.data;
	}
	/**
	 * 
	 * @param data
	 */
	public void setData(List<T> data) {
		this.data = data;
	}

	/**
	 * Delete the content of the BatchData
	 */
	public void clear(){
		this.data.clear();
	}
	/**
	 * 
	 * @param data
	 */
	public void cloneId(BatchData<?> data){
		this.id=data.id;
	}

	public String getId() {
		return id;
	}
	
	
	
	
	

}

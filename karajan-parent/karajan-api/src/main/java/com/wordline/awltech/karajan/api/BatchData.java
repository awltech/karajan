package com.wordline.awltech.karajan.api;

import java.io.Serializable;
import java.util.Iterator;
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
	public final static String Id = UUID.randomUUID().toString();
	private List<T> data;
	
	public BatchData(List<T> data) {
		this.data=data;
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
	
	
	
	
	
	

}

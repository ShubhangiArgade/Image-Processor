package com.image.processing.model;
/**
 * 
 * @author Shubhangi
 *
 */
public class RGB {
	public int redSumArray[][];
	public int greenSumArray[][];
	public int blueSumArray[][];
	
	public RGB(int redSumArray[][],int greenSumArray[][],int blueSumArray[][]){
		this.redSumArray=redSumArray;
		this.greenSumArray=greenSumArray;
		this.blueSumArray = blueSumArray;
	}
}

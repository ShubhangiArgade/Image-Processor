package com.image.processing.service;


import java.io.IOException;
/**
 * 
 * @author Shubhangi Argade
 *
 */

public interface ImageProcessingService {
	public  String processImageUsingOpenCV(String path) throws IOException;
	public String processImageUsingForLoop(String path) throws IOException;
	public String processImageUsingThread(String path) throws IOException;
}

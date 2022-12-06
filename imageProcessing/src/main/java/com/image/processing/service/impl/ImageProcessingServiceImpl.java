package com.image.processing.service.impl;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.image.processing.model.RGB;
import com.image.processing.service.ImageProcessingService;
import com.image.processing.utility.AverageCalculator;
/**
 * 
 * @author Shubhangi Argade
 * 
 */

@Service
public class ImageProcessingServiceImpl implements ImageProcessingService {
	private  Vector<Mat> average = new Vector<>();
	private  Vector<Mat> tempAverage = new Vector<>();
	 private  int count = 0;
	 private String newPath = "";
	 private static final Logger logger = LoggerFactory.getLogger(ImageProcessingServiceImpl.class);
	   
	 
		public String processImageUsingOpenCV(String path) throws IOException {
			newPath = path;
			
			/*
			 * File f = new File(path);
			 * ImageInputStream input = ImageIO.createImageInputStream(f); ImageReader
			 * tiffReader = ImageIO.getImageReaders(input).next();
			 * tiffReader.setInput(input); tiffReader.setInput(input);
			 */
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			logger.info("Started image processing");
			Imgcodecs.imreadmulti(path, tempAverage);
			for (int i = 0; i < tempAverage.size(); i++) {
				add(tempAverage.get(i));
			}
			String convertedFilePath = getAverage();
			//System.out.println("After average");
		
			return convertedFilePath;

		}


		public void add(Mat img) {
			count++;
			Vector<Mat> splitImg = new Vector<>();
			Mat convertedImg = img.clone();
			convertedImg.convertTo(convertedImg, CvType.CV_32FC1);
			Core.split(img.clone(), splitImg);

			if (average.isEmpty()) {
				average = splitImg;
			} else {
				for (int i = 0; i < average.size(); i++) {
					Core.multiply(average.get(i), new Scalar((count - 1) / ((double) count)), average.get(i));
					Mat temp = new Mat();
					// Core.divide(count, splitImg.get(i), temp);
					Core.multiply(splitImg.get(i), new Scalar(1.0 / count), temp);
					Core.add(average.get(i), temp, average.get(i));
				}
			}
		}

		public String getAverage() {
			Mat convertedAverage = new Mat();
			Core.merge(average, convertedAverage);
			convertedAverage.convertTo(convertedAverage.clone(), CvType.CV_8UC3);
			String fileName = newPath.substring(0, newPath.lastIndexOf('\\'));
			//String fileName = newPath.substring(0, lastInd-1);
			String convertedFilePath = fileName + "\\genrated-file-after-average.tiff";
			Imgcodecs.imwrite(convertedFilePath, convertedAverage);
			System.out.println(convertedFilePath);
			return convertedFilePath;
		}
		

		public String processImageUsingForLoop(String path) throws IOException {
			File f = new File(path);
			newPath = path;
			ImageReader reader = (ImageReader) ImageIO.getImageReadersByFormatName("tiff").next();
			ImageInputStream ciis = ImageIO.createImageInputStream(f);
			reader.setInput(ciis, false);

			int noi = reader.getNumImages(true);
			
			BufferedImage image = reader.read(0);
			int height = image.getHeight();
			int width = image.getWidth();
			
			int redSumArray[][] = new int[width][height];
			int greenSumArray[][] = new int[width][height];
			int blueSumArray[][] = new int[width][height];

			for (int iFrameIndex = 0; iFrameIndex < noi; iFrameIndex++) {

				BufferedImage currentImage = reader.read(iFrameIndex);
				for (int xPosition = 0; xPosition < width; xPosition++)
					for (int yPosition = 0; yPosition < height; yPosition++) {

						int currentRGB = currentImage.getRGB(xPosition, yPosition);

						int currentRed = (currentRGB & 0xff0000) >> 16;
						int currentGreen = (currentRGB & 0xff00) >> 8;
						int currentBlue = currentRGB & 0xff;

						redSumArray[xPosition][yPosition] = redSumArray[xPosition][yPosition] + currentRed;
						greenSumArray[xPosition][yPosition] = greenSumArray[xPosition][yPosition] + currentGreen;
						blueSumArray[xPosition][yPosition] = blueSumArray[xPosition][yPosition] + currentBlue;

					}
			}

			BufferedImage output = new BufferedImage(width, height, image.getType());
			
			for (int xPosition = 0; xPosition < width; xPosition++) {
				for (int yPosition = 0; yPosition < height; yPosition++) {
					
					int avreageRed = redSumArray[xPosition][yPosition] / noi;
					int averageGreen = greenSumArray[xPosition][yPosition] / noi;
					int averageBlue = blueSumArray[xPosition][yPosition] / noi;

					Color avereagedColor = new Color(avreageRed, averageGreen, averageBlue);
					output.setRGB(xPosition, yPosition, avereagedColor.getRGB());
				}
			}

			
			String fileName = newPath.substring(0, newPath.lastIndexOf('\\'));
			String convertedFilePath = fileName + "\\genrated-file-after-average.tiff";
			File file = new File(convertedFilePath);
			ImageIO.write(output, "TIFF", file);
			//System.out.println("For Loop Count = " + forLoopCOunt);
			//String endTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			//System.out.println("End Time: " + endTime);
			return convertedFilePath;
		}


		@Override
		public String processImageUsingThread(String path) throws IOException {
			logger.info("Started image processing : processImageUsingThread()");
			newPath = path;
			String convertedFilePath = "";
			ExecutorService executorService = Executors.newFixedThreadPool(2);

			File f = new File(path);
			ImageReader reader = (ImageReader) ImageIO.getImageReadersByFormatName("tiff").next();
			ImageInputStream ciis = ImageIO.createImageInputStream(f);
			reader.setInput(ciis, false);

			try {
				int numberOfImages = reader.getNumImages(true);
				logger.info("Number of images to process : " + numberOfImages);
				int halfImagesCount = numberOfImages / 2;

				

				BufferedImage firstImage = reader.read(0);
				int height = firstImage.getHeight();
				int width = firstImage.getWidth();
				
				logger.info("first image height : "+height);
				logger.info("first image width : "+width);
				List<BufferedImage> list1 = new ArrayList<BufferedImage>();
				for (int iFrameIndex = 0; iFrameIndex < halfImagesCount; iFrameIndex++) {
					BufferedImage currentImage = reader.read(iFrameIndex);
					list1.add(currentImage);
				}
				logger.info("First image list created..");

				List<BufferedImage> list2 = new ArrayList<BufferedImage>();
				for (int iFrameIndex = halfImagesCount; iFrameIndex < numberOfImages; iFrameIndex++) {
					BufferedImage currentImage = reader.read(iFrameIndex);
					list2.add(currentImage);
				}

				logger.info("Second image list created..");
			
				List<AverageCalculator> calculatorList = Arrays.asList(new AverageCalculator(height, width, list1),
						new AverageCalculator(height, width, list2));

				List<Future<RGB>> results = executorService.invokeAll(calculatorList);

				int redSumArray[][] = new int[width][height];
				int greenSumArray[][] = new int[width][height];
				int blueSumArray[][] = new int[width][height];

				int iResultCounter = 1;
				for (Future<RGB> currentRGB : results) {

					logger.info("Result counter " +iResultCounter);
					
					iResultCounter++;

					RGB currentValue = currentRGB.get();
					int currentRedSumArray[][] = currentValue.redSumArray;
					int currentGreenSumArray[][] = currentValue.greenSumArray;
					int currentBlueSumArray[][] = currentValue.blueSumArray;

					for (int xPosition = 0; xPosition < width; xPosition++) {
						for (int yPosition = 0; yPosition < height; yPosition++) {
							redSumArray[xPosition][yPosition] = redSumArray[xPosition][yPosition]
									+ currentRedSumArray[xPosition][yPosition];
							greenSumArray[xPosition][yPosition] = greenSumArray[xPosition][yPosition]
									+ currentGreenSumArray[xPosition][yPosition];
							blueSumArray[xPosition][yPosition] = blueSumArray[xPosition][yPosition]
									+ currentBlueSumArray[xPosition][yPosition];
						}
					}
				}

				BufferedImage output = new BufferedImage(width, height, firstImage.getType());

				int calulationImageCount = halfImagesCount * 2;
				
				for (int iWidth = 0; iWidth < width; iWidth++) {
					for (int j = 0; j < height; j++) {

						int redSum1 = redSumArray[iWidth][j] / calulationImageCount;
						int greemSum1 = greenSumArray[iWidth][j] / calulationImageCount;
						int bluSum1 = blueSumArray[iWidth][j] / calulationImageCount;

						Color avereagedColor = new Color(redSum1, greemSum1, bluSum1);
						output.setRGB(iWidth, j, avereagedColor.getRGB());
					}
				}

				String fileName = newPath.substring(0, newPath.lastIndexOf('\\'));
				 convertedFilePath = fileName + "\\genrated-file-after-average.tiff";
				File file = new File(convertedFilePath);
				ImageIO.write(output, "TIFF", file);
				logger.info("Average file created : " +convertedFilePath);
				executorService.shutdown();

			} catch (IOException e) {
				//e.printStackTrace();
				logger.error("IOException Occured " +e);
			} catch (InterruptedException e) {
				logger.error("InterruptedException Occured " +e);
				//e.printStackTrace();
			} catch (ExecutionException e) {
				logger.error("ExecutionException Occured " +e);
				//e.printStackTrace();
			}

			return convertedFilePath;
		}

}

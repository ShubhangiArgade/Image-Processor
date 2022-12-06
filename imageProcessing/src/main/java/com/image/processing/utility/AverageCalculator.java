package com.image.processing.utility;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.image.processing.model.RGB;
import com.image.processing.service.impl.ImageProcessingServiceImpl;

public class AverageCalculator implements Callable<RGB> {
	 private static final Logger logger = LoggerFactory.getLogger(ImageProcessingServiceImpl.class);
		
	int height;
	int width;
	List<BufferedImage> imageList;

	public AverageCalculator(int height, int width, List<BufferedImage> imageList) {
		this.height = height;
		this.width = width;
		this.imageList = imageList;
	}

	@Override
	public RGB call() throws Exception {
		logger.info("Started calculator to calculate RGB sum");
		int redSumArray[][] = new int[width][height];
		int greenSumArray[][] = new int[width][height];
		int blueSumArray[][] = new int[width][height];

		for (BufferedImage currentImage : imageList) {
			for (int widthIndex = 0; widthIndex < width; widthIndex++)
				for (int heightIndex = 0; heightIndex < height; heightIndex++) {

					int currentRGB = currentImage.getRGB(widthIndex, heightIndex);
					int currentRed = (currentRGB & 0xff0000) >> 16;
					int currentGreen = (currentRGB & 0xff00) >> 8;
					int currentBlue = currentRGB & 0xff;

					redSumArray[widthIndex][heightIndex] = redSumArray[widthIndex][heightIndex] + currentRed;
					greenSumArray[widthIndex][heightIndex] = greenSumArray[widthIndex][heightIndex] + currentGreen;
					blueSumArray[widthIndex][heightIndex] = blueSumArray[widthIndex][heightIndex] + currentBlue;

				}
		}

		RGB rgb = new RGB(redSumArray, greenSumArray, blueSumArray);
		logger.info("Calculate the average RGB value");
		logger.info("Ended image Caculator");
		return rgb;
	}

}

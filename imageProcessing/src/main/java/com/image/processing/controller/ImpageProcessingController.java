package com.image.processing.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.image.processing.service.ImageProcessingService;
import com.image.processing.service.impl.ImageProcessingServiceImpl;

/**
 * 
 * @author Shubhangi Argade Calculate average for tiff images
 */
@RestController
@RequestMapping("/api/process")
public class ImpageProcessingController {
	private static final Logger logger = LoggerFactory.getLogger(ImageProcessingServiceImpl.class);

	@Autowired
	private ImageProcessingService imageProcessingService;

	@GetMapping(path = "/welcome")
	public ModelAndView getWelcomePage() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("imageProcessing.html");
		modelAndView.setStatus(HttpStatus.OK);
		return modelAndView;
	}

	@PostMapping("/get/average/file")
	public ModelAndView imageProcessor(@RequestParam String path) {

		ModelAndView modelAndView = new ModelAndView();
		logger.info("Started image processor.");
		logger.info("input file path : " + path);
		if (null == path || path.equalsIgnoreCase("")) {
			logger.error("Please check if input is correct.");
			modelAndView.setViewName("error.html");
		}
		String outputFilePath = null;
		try {
			outputFilePath = imageProcessingService.processImageUsingThread(path);
			// outputFilePath = imageProcessingService.processImageUsingForLoop(path);
			// outputFilePath = imageProcessingService.processImageUsingOpenCV(path);
		} catch (IOException e) {
			logger.error("Error ocuured : " + e);
			// e.printStackTrace();
		}

		if (null == outputFilePath || outputFilePath.equalsIgnoreCase("")) {
			logger.error("Error ocuured ");
			modelAndView.setViewName("error.html");

		}

		modelAndView.setViewName("output.html");
		modelAndView.setStatus(HttpStatus.OK);
		modelAndView.addObject("outputFile", outputFilePath);
		return modelAndView;
	}
}

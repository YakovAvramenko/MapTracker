package com.mdvit.services.root;

import ij.ImagePlus;

import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mdvit.Configuration;
import com.mdvit.services.rest.MdService;
import com.mdvit.surf.IntegralImage;
import com.mdvit.surf.InterestPoint;
import com.mdvit.util.IJFacade;

public class RootMap {
	private static final Logger log = LoggerFactory.getLogger(RootMap.class);
	public static final String ROOT_FILE_PATH = "/WEB-INF/rootMap.png";
	public static File root;

	// algoritm
	private IntegralImage intImg;
	private List<InterestPoint> points;
	private ImagePlus rootPlus;

	private static RootMap INSTANCE = null;

	private RootMap(ServletContext context) {
		root = new File(context.getRealPath(RootMap.ROOT_FILE_PATH));
		try {
			rootPlus = new ImagePlus("", ImageIO.read(root));

			intImg = new IntegralImage(rootPlus.getProcessor(), true);

			points = IJFacade.detectAndDescribeInterestPoints(intImg,
					Configuration.newParams());

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static synchronized RootMap getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RootMap(null);
		}
		return INSTANCE;
	}
	
	public static synchronized RootMap getInstance(ServletContext context) {
		if (INSTANCE == null) {
			INSTANCE = new RootMap(context);
		}
		return INSTANCE;
	}

	public static File getRoot() {
		return root;
	}

	public static void setRoot(File root) {
		RootMap.root = root;
	}

	public IntegralImage getIntImg() {
		return intImg;
	}

	public void setIntImg(IntegralImage intImg) {
		this.intImg = intImg;
	}

	public List<InterestPoint> getPoints() {
		return points;
	}

	public void setPoints(List<InterestPoint> points) {
		this.points = points;
	}

	public ImagePlus getRootPlus() {
		return rootPlus;
	}

	public void setRootPlus(ImagePlus rootPlus) {
		this.rootPlus = rootPlus;
	}
	
	

}

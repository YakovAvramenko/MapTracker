package com.mdvit.services.handler;

import ij.ImagePlus;

import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.ws.rs.core.MultivaluedMap;

import com.mdvit.exports.ClusterMapRefresh;
import com.mdvit.exports.Statictics;
import com.mdvit.services.root.RootMap;

public class PostHandler {

	public static final PostHandler INSTANCE = new PostHandler();
	
	private ClusterMapRefresh handler;

	private PostHandler() {
		handler = new ClusterMapRefresh(RootMap.getInstance().getRootPlus(), 130);
	}

	public Statictics handleInput(InputStream stream,
			MultivaluedMap<String, String> map) throws Exception {
		ImagePlus img2 = new ImagePlus("", ImageIO.read(stream));
		return handler.refresh(img2);
	}

}

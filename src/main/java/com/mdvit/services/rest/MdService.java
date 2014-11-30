package com.mdvit.services.rest;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mdvit.exports.Statictics;
import com.mdvit.services.handler.PostHandler;
import com.mdvit.services.root.RootMap;

public class MdService {

	private static final Logger log = LoggerFactory.getLogger(MdService.class);

	@Context
	private ServletContext context;

	public void init() {
		if (context != null) {
			RootMap.getInstance(context);
		}
	}

	@GET
	@Path("/downloadFile")
	@Produces("image/png")
	public Response downloadFile() throws Exception {
		init();

		BufferedImage buffImage = RootMap.getInstance().getRootPlus()
				.getBufferedImage();

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(buffImage, "png", os);
		InputStream is = new ByteArrayInputStream(os.toByteArray());

		ResponseBuilder response = Response.ok(is);
		response.header("Content-Disposition", "attachment; filename=root.png");
		return response.build();
	}

	@POST
	@Path("/uploadFile")
	@Produces({"application/xml", "application/json"}) 
	public Response uploadFile(List<Attachment> attachments,
			@Context HttpServletRequest request) {
		Date start = new Date();
		init();
		Statictics stat = null;

		for (Attachment attachment : attachments) {
			try {
				InputStream stream = attachment.getDataHandler().getInputStream();
				MultivaluedMap<String, String> map = attachment.getHeaders();

				// handle
				stat = PostHandler.INSTANCE.handleInput(stream, map);

				stream.close();
			} catch (Exception e) {
				System.out.println(e);
				
				stat = new Statictics();
				stat.setException(e);
			}
		}

		System.out.println("upload handled in mlsc = " + (new Date().getTime() - start.getTime()));
		stat.setTotalTime(new Date().getTime() - start.getTime());
		
		System.out.println("Statictics = " + stat);
		return Response.ok(stat).build();
	}

}

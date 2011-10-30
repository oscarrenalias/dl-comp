package com.accenture.demo.material;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.GET;

@Produces("application/xml")
@Path("materials")

public class MaterialResource {

	private ArrayList<Material> materials = new ArrayList<Material>();

	public MaterialResource() {
		 Material material = new Material();
		 material.setId("0001");
		 material.setName("First material");
		 material.setPrice("150");
		 materials.add(material);

		 Material material2 = new Material();
		 material2.setId("0002");
		 material2.setName("Second material");
		 material2.setPrice("200");
		 materials.add(material);
	}

	@GET
	public List<Material> getMaterials() {
		return materials;
	}
}
package tn.esprit.spring.Controller.Produit;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Utils.AppConstants;
import tn.esprit.spring.Model.Produit.ImageProduit;
import tn.esprit.spring.Model.Produit.Produit;
import tn.esprit.spring.Model.Produit.SsCategorie;
import tn.esprit.spring.Service.Produit.FileStorageServiceImpl;
import tn.esprit.spring.Service.Produit.IImageProduitService;
import tn.esprit.spring.Service.Produit.IProduitService;
import tn.esprit.spring.Service.Produit.ISousSousCategorieService;

@RestController
@RequestMapping("/produit")
public class RestControllerProduit {
	@Autowired
	IProduitService iproduitService;
	@Autowired
	ISousSousCategorieService isousSousCategorieService;
	@Autowired
	FileStorageServiceImpl fileStorageServiceImpl;
	@Autowired
	IImageProduitService iImagesProduitService;

	ObjectMapper objectMapper = new ObjectMapper();

	@PostMapping(value = "/ajouter/{idSsCategorie}")
	public ResponseEntity<String> AjouterProduit(@PathVariable(value = "idSsCategorie") Long idSsCategorie,
			@RequestParam(value = "produit", required = true) String ProduitJson,
			@RequestParam(required = true, value = AppConstants.EMPLOYEE_FILE_PARAM) List<MultipartFile> file)
			throws JsonParseException, JsonMappingException, IOException {
		Produit p = objectMapper.readValue(ProduitJson, Produit.class);
		SsCategorie ssc = isousSousCategorieService.findOne(idSsCategorie);
		if (!p.BarcodeIsvalid(p.getBarcode())) {
			return ResponseEntity.ok("Ce n'est pas un produit tunsien désolé");
		}
		p.setIdSsCategorie(ssc);
		iproduitService.save(p);

		for (MultipartFile i : file) {
			String fileName = fileStorageServiceImpl.storeFile(i);
			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
					.path(AppConstants.DOWNLOAD_PATH).path(fileName).toUriString();

			ImageProduit image = new ImageProduit();
			image.setImage(fileDownloadUri);
			image.setIdproduit(p);
			iImagesProduitService.save(image);
		}
		return ResponseEntity.ok("Produit Ajouter avec succes");
	}

	@GetMapping("/afficher")
	public List<Produit> getAllProduit() {
		return iproduitService.findAll();
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> DeleteProduit(@PathVariable(value = "id") Long idProduit) {
		Produit p = iproduitService.findOne(idProduit);
		if (p == null) {
			return ResponseEntity.ok("Veuillez choisir un Produit");
		}
		iproduitService.Delete(p);
		return ResponseEntity.ok("Produit Supprimer avec succes");
	}

	@PutMapping("/edit/{id}/{idSsCategorie}")
	public ResponseEntity<Produit> EditProduit(@PathVariable(value = "idSsCategorie") Long idSsCategorie,
			@PathVariable(value = "id") Long idProduit, @Valid @RequestBody Produit p) {
		Produit p2 = iproduitService.findOne(idProduit);
		SsCategorie ssc = isousSousCategorieService.findOne(idSsCategorie);
		if (p == null || !p2.BarcodeIsvalid(p.getBarcode())) {
			return ResponseEntity.notFound().build();
		}
		p2.setNomProduit(p.getNomProduit());
		p2.setPrix(p.getPrix());
		p2.setDescription(p.getDescription());
		p2.setBarcode(p.getBarcode());
		p2.setPoids(p.getPoids());
		p2.setPrixAchat(p.getPrixAchat());
		p2.setIdSsCategorie(ssc);
		iproduitService.save(p2);
		return ResponseEntity.ok().build();

	}

	@GetMapping("/recherche/{nom}")
	public List<Produit> findLikeNameM(@PathVariable(value = "nom") String name) {
		return iproduitService.findLikeName(name);
	}

	@GetMapping("/ssCategorie/{idSsCategorie}")
	public List<Produit> findProduitSsCategorie(@PathVariable(value = "idSsCategorie") Long idSsCategorie) {
		return iproduitService.findProduitSsCategorie(idSsCategorie);
	}

	@GetMapping("/sCategorie/{idSCategorie}")
	public List<Produit> findProduitSCategorie(@PathVariable(value = "idSCategorie") Long idSCategorie) {
		return iproduitService.findProduitSCategorie(idSCategorie);
	}

	@GetMapping("/Categorie/{idSCategorie}")
	public List<Produit> findProduitCategorie(@PathVariable(value = "idSCategorie") Long idCategorie) {
		return iproduitService.findProduitCategorie(idCategorie);
	}

}

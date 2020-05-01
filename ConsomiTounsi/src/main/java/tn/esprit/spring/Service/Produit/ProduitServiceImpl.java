package tn.esprit.spring.Service.Produit;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Utils.AppConstants;
import tn.esprit.spring.Model.Produit.ImageProduit;
import tn.esprit.spring.Model.Produit.Produit;
import tn.esprit.spring.Model.Produit.SsCategorie;
import tn.esprit.spring.Repository.Produit.ProduitRepository;


@Service
public class ProduitServiceImpl implements IProduitService {
	@Autowired
	ProduitRepository produitRepository;
	@Autowired
	ISousSousCategorieService isousSousCategorieRepository;
	@Autowired
	FileStorageServiceImpl fileStorageServiceImpl;
	@Autowired
	IImageProduitService iImagesProduitService;

	ObjectMapper objectMapper = new ObjectMapper();

	public Produit findOne(Long id) {
		return produitRepository.getOne(id);
	}

	public List<Produit> findAll() {
		return produitRepository.findAll();
	}

	public Produit Update(Long idproduit, Long idSsCategorie,String ProduitJson,List<MultipartFile> file) 
			throws JsonMappingException, JsonProcessingException, IOException {
		Produit p2 = findOne(idproduit);
		Produit p = objectMapper.readValue(ProduitJson, Produit.class);
		SsCategorie ssc = isousSousCategorieRepository.findOne(idSsCategorie);
		if (p.BarcodeIsvalid(p.getBarcode())) {
			p2.setNomProduit(p.getNomProduit());
			p2.setPrix(p.getPrix());
			p2.setDescription(p.getDescription());
			p2.setBarcode(p.getBarcode());
			p2.setPoids(p.getPoids());
			p2.setPrixAchat(p.getPrixAchat());
			p2.setIdSsCategorie(ssc);
			produitRepository.save(p2);
		}
		return p2;
	}

	public void Delete(Long id) {
		Produit p = findOne(id);
		produitRepository.delete(p);
	}

	public Produit Add(String ProduitJson, Long idSsCategorie, List<MultipartFile> file)
			throws JsonMappingException, JsonProcessingException, IOException {
		Produit p = objectMapper.readValue(ProduitJson, Produit.class);
		SsCategorie ssc = isousSousCategorieRepository.findOne(idSsCategorie);
		if (!p.BarcodeIsvalid(p.getBarcode())) {
			return null;
		}
		p.setIdSsCategorie(ssc);
		produitRepository.save(p);
		for (MultipartFile i : file) {
			String fileName = fileStorageServiceImpl.storeFile(i);
			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
					.path(AppConstants.DOWNLOAD_PATH).path(fileName).toUriString();
			ImageProduit image = new ImageProduit();
			image.setImage(fileDownloadUri);
			image.setIdproduit(p);
			iImagesProduitService.save(image);
		}
		
		return p;
	}

	public List<Produit> findLikeName(String name) {
		return produitRepository.findLikeName(name);

	}

	public List<Produit> findProduitSsCategorie(Long idSsCategorie) {
		return produitRepository.findProduitSsCategorie(idSsCategorie);
	}

	public List<Produit> findProduitSCategorie(Long idSCategorie) {
		return produitRepository.findProduitSCategorie(idSCategorie);
	}

	public List<Produit> findProduitCategorie(Long idCategorie) {
		return produitRepository.findProduitCategorie(idCategorie);
	}

}

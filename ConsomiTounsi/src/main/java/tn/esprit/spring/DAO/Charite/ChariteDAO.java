package tn.esprit.spring.DAO.Charite;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import tn.esprit.spring.Model.Commande;
import tn.esprit.spring.Model.Charite.Charite;

public interface ChariteDAO {
	boolean saveCharit(Charite Charite);
	List<Charite> getAllChariteList();
	public int saveCharite(Long idevents,Long iduser,Charite Charite);
	Charite findOne(Long id);
	public int saveCharite1(Long idevents,Long iduser,Charite Charite);
	public int saveCharitee(Long idevents,Long iduser,Long idcommande,Charite Charite);
	public List<Commande> getCommande(Long idCommande);
	public List<Charite> getCharite(Long iduser);
	Charite findOnes(Long id);

	
}

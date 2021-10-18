package it.manytomanyjpamaven.dao;

import java.util.List;

import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.Utente;

public interface UtenteDAO extends IBaseDAO<Utente> {
	
	public List<Utente> findAllByRuolo(Ruolo ruoloInput);
	public Utente findByIdFetchingRuoli(Long id);
	public List<Utente> findUtentiCreatiAGiugno2021();
	public Long countAdmin();
	public List<Utente> findByLunghezzaPasswordMinDi8();
	public boolean foundTraUtentiDisabilitatiCeUnAdmin();

}

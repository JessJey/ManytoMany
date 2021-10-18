package it.manytomanyjpamaven.service;

import java.util.List;

import it.manytomanyjpamaven.dao.UtenteDAO;
import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.Utente;

public interface UtenteService {

	public List<Utente> listAll() throws Exception;

	public Utente caricaSingoloElemento(Long id) throws Exception;

	public void aggiorna(Utente utenteInstance) throws Exception;

	public void inserisciNuovo(Utente utenteInstance) throws Exception;

	public void rimuovi(Utente utenteInstance) throws Exception;

	public void aggiungiRuolo(Utente utenteEsistente, Ruolo ruoloInstance) throws Exception;

	public Utente caricaUtenteSingoloConRuoli(Long id) throws Exception;

	public void rimuoviRuoloDaUtente(Utente utenteEsistente, Ruolo ruoloInstance) throws Exception;
	
	public List<Utente> cercaUtentiCreatiAGiugno2021() throws Exception;
	
	public Long contaAdmin() throws Exception;
	
	public List<Utente> findByLunghezzaPasswordMinDi8() throws Exception;
	
	public boolean trovaTraUtentiDisabilitatiSeCeUnAdmin() throws Exception;
	
	

	// per injection
	public void setUtenteDAO(UtenteDAO utenteDAO);

}

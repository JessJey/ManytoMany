package it.manytomanyjpamaven.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import it.manytomanyjpamaven.dao.EntityManagerUtil;
import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.StatoUtente;
import it.manytomanyjpamaven.model.Utente;
import it.manytomanyjpamaven.service.MyServiceFactory;
import it.manytomanyjpamaven.service.RuoloService;
import it.manytomanyjpamaven.service.UtenteService;

public class ManyToManyTest {

	public static void main(String[] args) {
		UtenteService utenteServiceInstance = MyServiceFactory.getUtenteServiceInstance();
		RuoloService ruoloServiceInstance = MyServiceFactory.getRuoloServiceInstance();

		// ora passo alle operazioni CRUD
		try {

			// inizializzo i ruoli sul db
			initRuoli(ruoloServiceInstance);

			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testInserisciNuovoUtente(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testCollegaUtenteARuoloEsistente(ruoloServiceInstance, utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testModificaStatoUtente(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testDeleteRuolo(utenteServiceInstance, ruoloServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			initRuoli(ruoloServiceInstance);

			testDissociaRuolo(utenteServiceInstance, ruoloServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testUtentiCreatiAGiugno2021(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testContaAdmin(utenteServiceInstance, ruoloServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");

			testTrovaUtentiPasswordMinDi8(utenteServiceInstance);
			System.out.println("In tabella Utente ci sono " + utenteServiceInstance.listAll().size() + " elementi.");
			
			testTrovaTraUtentiDisabilitatiSeCeUnAdmin(utenteServiceInstance,ruoloServiceInstance);
			
			testListaDescrizioneRuoloConUtenti(ruoloServiceInstance);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// questa è necessaria per chiudere tutte le connessioni quindi rilasciare il
			// main
			EntityManagerUtil.shutdown();
		}

	}

	private static void initRuoli(RuoloService ruoloServiceInstance) throws Exception {
		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", "ROLE_ADMIN") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Administrator", "ROLE_ADMIN"));
		}

		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Classic User", "ROLE_CLASSIC_USER") == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Classic User", "ROLE_CLASSIC_USER"));
		}
	}

	private static void testInserisciNuovoUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testInserisciNuovoUtente inizio.............");

		Utente utenteNuovo = new Utente("pippo.rossi", "xxx", "pippo", "rossi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito ");

		System.out.println(".......testInserisciNuovoUtente fine: PASSED.............");
	}

	private static void testCollegaUtenteARuoloEsistente(RuoloService ruoloServiceInstance,
			UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testCollegaUtenteARuoloEsistente inizio.............");

		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.caricaSingoloElemento(1L);
		if (ruoloEsistenteSuDb == null)
			throw new RuntimeException("testCollegaUtenteARuoloEsistente fallito: ruolo inesistente ");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("mario.bianchi", "JJJ", "mario", "bianchi", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testInserisciNuovoUtente fallito: utente non inserito ");

		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);
		// per fare il test ricarico interamente l'oggetto e la relazione
		Utente utenteReloaded = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		if (utenteReloaded.getRuoli().size() != 1)
			throw new RuntimeException("testInserisciNuovoUtente fallito: ruoli non aggiunti ");

		System.out.println(".......testCollegaUtenteARuoloEsistente fine: PASSED.............");
	}

	private static void testModificaStatoUtente(UtenteService utenteServiceInstance) throws Exception {
		System.out.println(".......testModificaStatoUtente inizio.............");

		// mi creo un utente inserendolo direttamente su db
		Utente utenteNuovo = new Utente("mario1.bianchi1", "JJJ", "mario1", "bianchi1", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testModificaStatoUtente fallito: utente non inserito ");

		// proviamo a passarlo nello stato ATTIVO ma salviamoci il vecchio stato
		StatoUtente vecchioStato = utenteNuovo.getStato();
		utenteNuovo.setStato(StatoUtente.ATTIVO);
		utenteServiceInstance.aggiorna(utenteNuovo);

		if (utenteNuovo.getStato().equals(vecchioStato))
			throw new RuntimeException("testModificaStatoUtente fallito: modifica non avvenuta correttamente ");

		System.out.println(".......testModificaStatoUtente fine: PASSED.............");
	}

	private static void testDeleteRuolo(UtenteService utenteServiceInstance, RuoloService ruoloServiceInstance)
			throws Exception {
		System.out.println(".......testDELETEruolo inizio.............");
		// creazione nuovo utente
		Utente utenteNuovo = new Utente("Jessi.pet1", "JJJ", "jessi12", "pet12", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testDELETE fallito: utente non inserito ");
		// richiamo ruolo
		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.caricaSingoloElemento(3L);
		if (ruoloEsistenteSuDb == null)
			throw new RuntimeException("TEST DELETE caricamento ruolo fallito: ruolo inesistente ");

		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);
		try {
			ruoloServiceInstance.rimuovi(ruoloEsistenteSuDb);
			throw new RuntimeException("TEST DELETE caricamento ruolo fallito: cancellazione avvenuta ugualmente ");
		} catch (Exception e) {
		}
	}

	private static void testDissociaRuolo(UtenteService utenteServiceInstance, RuoloService ruoloServiceInstance)
			throws Exception {
		System.out.println(".......test DISSOCIA RUOLO inizio.............");
		// creazione nuovo utente
		Utente utenteNuovo = new Utente("Jessi.pet123", "JJJ", "jessi123", "pet123", new Date());
		utenteServiceInstance.inserisciNuovo(utenteNuovo);
		if (utenteNuovo.getId() == null)
			throw new RuntimeException("testDELETE fallito: utente non inserito ");
		// richiamo ruolo
		Ruolo ruoloEsistenteSuDb = ruoloServiceInstance.caricaSingoloElemento(3L);
		if (ruoloEsistenteSuDb == null)
			throw new RuntimeException("TEST DISSOCIA RUOLO caricamento ruolo fallito: ruolo inesistente ");

		utenteServiceInstance.aggiungiRuolo(utenteNuovo, ruoloEsistenteSuDb);
		// devi rimuovere l'associazione utente ruolo e riprovare la rimuovi
		Utente utenteNuovo2 = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		utenteServiceInstance.rimuoviRuoloDaUtente(utenteNuovo2, ruoloEsistenteSuDb);

		utenteNuovo2 = utenteServiceInstance.caricaUtenteSingoloConRuoli(utenteNuovo.getId());
		if (!utenteNuovo2.getRuoli().isEmpty())
			throw new RuntimeException("TEST DISSOCIA RUOLO fallito: ruolo ancora associato ");

		Ruolo ruoloReloaded = ruoloServiceInstance.caricaSingoloElemento(3L);
		try {
			ruoloServiceInstance.rimuovi(ruoloReloaded);
			throw new RuntimeException(
					"TEST DISSOCIA RUOLO caricamento ruolo fallito: cancellazione avvenuta ugualmente ");
		} catch (Exception e) {
		}
		System.out.println(".......TEST DISSOCIA RUOLO fine: PASSED.............");

	}

	private static void testUtentiCreatiAGiugno2021(UtenteService utenteServiceInstance) throws Exception {

		System.out.println("TEST UTENTI CREATI A GIUGNO 2021 INIZIO....");
		String dataDaCuiPartire = "2021-06-2";
		Date dateCreatedInput = new SimpleDateFormat("yyyy-MM-dd").parse(dataDaCuiPartire);
		Utente utenteTestGiugno = new Utente("Laura", "JJJ", "Lalla", "la123", dateCreatedInput);
		utenteServiceInstance.inserisciNuovo(utenteTestGiugno);
		Utente utenteTestGiugno2 = new Utente("Paola", "JJJ", "paola222", "pa123", new Date("2021/06/15"));
		utenteServiceInstance.inserisciNuovo(utenteTestGiugno2);

		if (utenteServiceInstance.cercaUtentiCreatiAGiugno2021().size() != 2) {
			throw new RuntimeException("TEST UTENTI CREATI A GIUGNO FALLITO");
		}

		utenteServiceInstance.rimuovi(utenteTestGiugno2);
		utenteServiceInstance.rimuovi(utenteTestGiugno);
		System.out.println("TEST UTENTI CREATI A GIUGNO PASSATO.....");
	}

	private static void testContaAdmin(UtenteService utenteServiceInstance, RuoloService ruoloServiceInstance) throws Exception {
		
		System.out.println(".....TEST CONTA ADMIN.....");
		Utente utenteTestConta = new Utente("Mario", "JJJ", "Mariu", "MAM1", new Date("2021/01/01"));
		utenteServiceInstance.inserisciNuovo(utenteTestConta);
		Utente utenteTestConta2 = new Utente("Lorenzo", "JJJ", "Lollo", "Lor23", new Date("2021/08/15"));
		utenteServiceInstance.inserisciNuovo(utenteTestConta2);
		Ruolo ruoloEsistente = ruoloServiceInstance.caricaSingoloElemento(1L);
		utenteServiceInstance.aggiungiRuolo(utenteTestConta, ruoloEsistente);
		utenteServiceInstance.aggiungiRuolo(utenteTestConta2, ruoloEsistente);
		
		if(utenteServiceInstance.contaAdmin() == null) {
			throw new RuntimeException("TEST CONTA ADMIN FALLITO");
		}
		
		System.out.println("UTENTI ADMIN = " + utenteServiceInstance.contaAdmin());
		
		System.out.println("TEST CONTA ADMIN PASSATO.....");
		
	}
	
	private static void testTrovaUtentiPasswordMinDi8(UtenteService utenteServiceInstance) throws Exception {
		
		System.out.println("TEST TROVA UTENTI PASSWORD INIZIO.....");
		if(utenteServiceInstance.findByLunghezzaPasswordMinDi8() == null) {
			throw new RuntimeException("TEST CONTA ADMIN FALLITO");
		}
		System.out.println(utenteServiceInstance.findByLunghezzaPasswordMinDi8().size());
		
		System.out.println("TEST TROVA UTENTI PASSWORD FINITO......");
	}
	
	private static void testTrovaTraUtentiDisabilitatiSeCeUnAdmin(UtenteService utenteServiceInstance, RuoloService ruoloServiceInstance) throws Exception{
		
		System.out.println("TEST TROVA DISABILITATI ADMIN.....");
		Utente utenteTestTrova = new Utente("Cristian", "JJJ", "cris", "CRIC", new Date("2021/01/01"));
		utenteServiceInstance.inserisciNuovo(utenteTestTrova);
		utenteTestTrova.setStato(StatoUtente.DISABILITATO);
		utenteServiceInstance.aggiorna(utenteTestTrova);
		Ruolo ruoloEsistente = ruoloServiceInstance.caricaSingoloElemento(1L);
		utenteServiceInstance.aggiungiRuolo(utenteTestTrova, ruoloEsistente);
		
		if(utenteServiceInstance.trovaTraUtentiDisabilitatiSeCeUnAdmin()) {
			System.out.println("TEST TROVA DISABILITATI ADMIN PASSED");
		}
		System.out.println("TEST FALLITO");
		
	}
	
	private static void testListaDescrizioneRuoloConUtenti(RuoloService ruoloServiceInstance) throws Exception{
		
		System.out.println("TEST LISTA DESCRIZIONE RUOLO.....");
		
		for (Ruolo ruoloItem : ruoloServiceInstance.listDescrizioneRuoloUtenti()) {
			System.out.println(ruoloItem);
		}
		
		System.out.println("TEST LISTA DESCRIZIONE RUOLO finito.....");
		
	}

}

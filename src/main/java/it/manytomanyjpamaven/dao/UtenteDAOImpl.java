package it.manytomanyjpamaven.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.Utente;

public class UtenteDAOImpl implements UtenteDAO {

	private EntityManager entityManager;

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<Utente> list() throws Exception {
		// dopo la from bisogna specificare il nome dell'oggetto (lettera maiuscola) e
		// non la tabella
		return entityManager.createQuery("from Utente", Utente.class).getResultList();
	}

	@Override
	public Utente get(Long id) throws Exception {
		return entityManager.find(Utente.class, id);
	}

	@Override
	public void update(Utente utenteInstance) throws Exception {
		if (utenteInstance == null) {
			throw new Exception("Problema valore in input");
		}
		utenteInstance = entityManager.merge(utenteInstance);
	}

	@Override
	public void insert(Utente utenteInstance) throws Exception {
		if (utenteInstance == null) {
			throw new Exception("Problema valore in input");
		}

		entityManager.persist(utenteInstance);
	}

	@Override
	public void delete(Utente utenteInstance) throws Exception {
		if (utenteInstance == null) {
			throw new Exception("Problema valore in input");
		}
		entityManager.remove(entityManager.merge(utenteInstance));
	}

	// questo metodo ci torna utile per capire se possiamo rimuovere un ruolo non
	// essendo collegato ad un utente
	public List<Utente> findAllByRuolo(Ruolo ruoloInput) {
		TypedQuery<Utente> query = entityManager.createQuery("select u FROM Utente u join u.ruoli r where r = :ruolo",
				Utente.class);
		query.setParameter("ruolo", ruoloInput);
		return query.getResultList();
	}

	@Override
	public Utente findByIdFetchingRuoli(Long id) {
		TypedQuery<Utente> query = entityManager
				.createQuery("select u FROM Utente u left join fetch u.ruoli r where u.id = :idUtente", Utente.class);
		query.setParameter("idUtente", id);
		return query.getResultList().stream().findFirst().orElse(null);
	}

	public List<Utente> findUtentiCreatiAGiugno2021() {
		TypedQuery<Utente> query = entityManager.createQuery(
				"select u FROM Utente u where u.dateCreated between '2021/06/01' and '2021/06/30' ", Utente.class);
		return query.getResultList();
	}

	public Long countAdmin() {

		TypedQuery<Long> query = entityManager.createQuery(
				"select count(distinct u.id) from Utente u join u.ruoli r where r.codice = 'ROLE_ADMIN' ", Long.class);

		return query.getSingleResult();
	}

	public List<Utente> findByLunghezzaPasswordMinDi8() {
		TypedQuery<Utente> query = entityManager.createQuery("select u FROM Utente u where length(u.password)<8 ",
				Utente.class);
		return query.getResultList();
	}

	public boolean foundTraUtentiDisabilitatiCeUnAdmin() {
		TypedQuery<Long> query = entityManager.createQuery(
				"select count(distinct u.id) from Utente u join u.ruoli r where r.codice = 'ROLE_ADMIN' and u.stato = 'DISABILITATO' ",
				Long.class);
		return query.getSingleResult() != null;
	}
}

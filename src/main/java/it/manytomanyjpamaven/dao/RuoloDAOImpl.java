package it.manytomanyjpamaven.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import it.manytomanyjpamaven.model.Ruolo;
import it.manytomanyjpamaven.model.Utente;

public class RuoloDAOImpl implements RuoloDAO {

	private EntityManager entityManager;

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<Ruolo> list() throws Exception {
		return entityManager.createQuery("from Ruolo", Ruolo.class).getResultList();
	}

	@Override
	public Ruolo get(Long id) throws Exception {
		return entityManager.find(Ruolo.class, id);
	}

	@Override
	public void update(Ruolo ruoloInstance) throws Exception {
		if (ruoloInstance == null) {
			throw new Exception("Problema valore in input");
		}
		ruoloInstance = entityManager.merge(ruoloInstance);

	}

	@Override
	public void insert(Ruolo ruoloInstance) throws Exception {
		if (ruoloInstance == null) {
			throw new Exception("Problema valore in input");
		}

		entityManager.persist(ruoloInstance);

	}

	@Override
	public void delete(Ruolo ruoloInstance) throws Exception {
		if (ruoloInstance == null) {
			throw new Exception("Problema valore in input");
		}
		entityManager.remove(entityManager.merge(ruoloInstance));

	}

	@Override
	public Ruolo findByDescrizioneAndCodice(String descrizione, String codice) throws Exception {
		TypedQuery<Ruolo> query = entityManager
				.createQuery("select r from Ruolo r where r.descrizione=?1 and r.codice=?2", Ruolo.class)
				.setParameter(1, descrizione).setParameter(2, codice);

		return query.getResultStream().findFirst().orElse(null);
	}

	@Override
	public List<Ruolo> listaDescrizioneRuoliConUtenti() throws Exception {
		TypedQuery<Ruolo> query = entityManager
				.createQuery("select distinct r.descrizione from Utente u join u.ruoli", Ruolo.class);
		return query.getResultList();
	}

}

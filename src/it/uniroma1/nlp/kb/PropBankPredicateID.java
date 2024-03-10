package it.uniroma1.nlp.kb;
/**
 * la classe rappresenta l'id di un predicato di PropBank
 * @author Federica Magliocca
 *
 */
public class PropBankPredicateID implements ResourceID{
	/**
	 * id del PropBankPredicate
	 */
	private String id;
	/**
	 * 
	 * @param id
	 */
	public PropBankPredicateID(String id) {
		this.id=id;
	}
	/**
	 * 
	 * @return l'id del PropBankPredicate
	 */
	@Override
	public String getId() {
		return id;
	}
	/**
	 * compara questo oggetto con quello fornito in input
	 * @param id
	 * @return true se sono uguali, false altrimenti
	 */
	@Override
	public boolean equals(ResourceID id) {
		return this.id.equals(id.getId());
	}
	/**
	 * 
	 * @return l'hashcode dell'oggetto
	 */
	@Override
	public int hashCode() {
		return this.id.hashCode();
		
	}
	

}

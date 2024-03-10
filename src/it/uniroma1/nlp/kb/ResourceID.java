package it.uniroma1.nlp.kb;
/**
 * interfaccia degli id dei frame
 * @author Federica Magliocca
 *
 */
public interface ResourceID {
	/**
	 * 
	 * @return l'id
	 */
	String getId();
	/**
	 *
	 * @return l'hashcode dell'oggetto 
	 */
	int hashCode();
	/**
	 * compara questo oggetto con quello fornito in input
	 * @param id
	 * @return true se sono uguali, false altrimenti
	 */
	boolean equals(ResourceID id);

}

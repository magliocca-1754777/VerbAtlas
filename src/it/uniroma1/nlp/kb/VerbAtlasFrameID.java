package it.uniroma1.nlp.kb;
/**
 * la classe rappresenta l'id del frame
 * @author Federica Magliocca
 *
 */
public class VerbAtlasFrameID implements ResourceID{
	/**
	 * id del frame
	 */
	private String id;
	/**
	 * 
	 * @param id
	 */
	public VerbAtlasFrameID(String id) {
		this.id=id;
	}
	/**
	 * 
	 * @return l'id del frame
	 */
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

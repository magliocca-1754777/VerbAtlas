package it.uniroma1.nlp.kb;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import it.uniroma1.nlp.verbatlas.ApriFile;
/**
 * la classe rappresente l'id del synset di WordNet
 * @author Federica Magliocca
 *
 */
public class WordNetSynsetID implements ResourceID {
	/**
	 * id del WordNetSynset
	 */
	private String id;
	/**
	 * nomi del WordNetSynset
	 */
	private List<String> nome;
	/**
	 * 
	 * @param id
	 */
	public WordNetSynsetID(String id){
		this.id=id;
		HashMap<String,String> nomiWN=ApriFile.file2Map("VerbAtlas-1.0.3/VerbAtlas-1.0.3/wn2lemma.tsv");
		nome= Arrays.asList(nomiWN.get(id).split(" "));
	}
	/**
	 * 
	 * @return l'id del WordNetSynset
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
	/**
	 * 
	 * @return il nome del WordNetSynset
	 */
	public List<String> getName() {
		return nome;
	}

}

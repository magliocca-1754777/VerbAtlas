package it.uniroma1.nlp.kb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.uniroma1.nlp.verbatlas.ApriFile;
/**
 * la classe rappresenta l'id di un synset di BabelNet
 * @author Federica Magliocca
 *
 */
public class BabelNetSynsetID implements ResourceID {
	/**
	 * id del BabelNetSynset
	 */
	private String id;
	/**
	 * nomi del BabelNetSynset
	 */
	private List<String> nome=new ArrayList<>();
	/**
	 * 
	 * @param id
	 */
	public BabelNetSynsetID(String id) {
		this.id=id;
		
		if(id.endsWith("v")) {
			HashMap<String, String> bn2wn = ApriFile.file2Map("VerbAtlas-1.0.3/VerbAtlas-1.0.3/bn2wn.tsv");
			nome= new WordNetSynsetID(bn2wn.get(id)).getName();
		}
	    else {
			List<String> sel = ApriFile.file2List("VerbAtlas-1.0.3/VerbAtlas-1.0.3/VA_preference_ids.tsv");
		    for (int i=0;i<sel.size();i++) {
				String[] selArray=sel.get(i).split("\t+");
				if(selArray[1].equals(id)) {
					nome.add(selArray[2]);
				}
			}
		}
	}
	/**
	 *  
	 * @return l'id del BabelNetSynset
	 */
	@Override
	public String getId() {
		return id;
	}
	/**
	 * compara questo oggetto con quello fornito in input
	 * @param id
	 * @return un booleano
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
	 * @return il nome del BabelNetSynset
	 */
	public List<String> getName() {
		return nome;
	}
	public String toString() {
		return id;
	}
}

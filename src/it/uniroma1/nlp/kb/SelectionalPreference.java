package it.uniroma1.nlp.kb;

import java.util.HashMap;
import java.util.List;
import it.uniroma1.nlp.verbatlas.ApriFile;
/**
 * la classe rappresenta le preferenze di selezione di un ruolo
 * @author Federica Magliocca
 *
 */
public class SelectionalPreference{
	/**
	 * nome della preferenza di selezione
	 */
	private String nome;
	/**
	 * id del frame
	 */
	private VerbAtlasFrameID ID;
	/**
	 * id della preferenza di selezione
	 */
	private BabelNetSynsetID bn;
	/**
	 * 
	 * @param id
	 */
	public SelectionalPreference(String id){
		HashMap<String,String> sel=ApriFile.file2Map("VerbAtlas-1.0.3/VerbAtlas-1.0.3/VA_preference_ids.tsv");
		List<String> sel2=ApriFile.file2List("VerbAtlas-1.0.3/VerbAtlas-1.0.3/VA_preference_ids.tsv");
		if (id.equals("entity")) {
			nome="entity";
			for (int i=0;i<sel2.size();i++) {
				String[] selArray=sel2.get(i).split("\t+");
				if(selArray[2].equals(nome)) {
					 bn=new BabelNetSynsetID(selArray[1]);
					 ID=new VerbAtlasFrameID(selArray[0]);
				}
			}
		}
		else {
			String[] selArray=sel.get(id).split("\t+");
		    bn=new BabelNetSynsetID(selArray[0]);
			nome=selArray[1];
		    ID=new VerbAtlasFrameID(id);
		}
	}
	/**
	 * 
	 * @param bn
	 */
	public SelectionalPreference(BabelNetSynsetID bn){
		List<String> sel=ApriFile.file2List("VerbAtlas-1.0.3/VerbAtlas-1.0.3/VA_preference_ids.tsv");
		for (int i=0;i<sel.size();i++) {
			String[] selArray=sel.get(i).split("\t+");
			if(selArray[1].equals(bn.getId())) {
                nome=selArray[2];
				ID=new VerbAtlasFrameID(selArray[0]);
			}
		}
		this.bn=bn;
	}
	/**
	 *
	 * @return l'id del frame
	 */
	public VerbAtlasFrameID getId() {
		return ID;
	}
	/**
	 * 
	 * @return il nome della prefernza di selezione
	 */
	public String getName() {
		return nome;
	}
	/**
	 * 
	 * @return l'id della preferenza di selezione
	 */
	public BabelNetSynsetID getBabelSynsetID() {
		return bn;
	}
	
}


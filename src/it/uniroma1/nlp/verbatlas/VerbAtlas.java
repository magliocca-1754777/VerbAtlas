package it.uniroma1.nlp.verbatlas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import it.uniroma1.nlp.kb.BabelNetSynsetID;
import it.uniroma1.nlp.kb.Frame;
import it.uniroma1.nlp.kb.PropBankPredicateID;
import it.uniroma1.nlp.kb.ResourceID;
import it.uniroma1.nlp.kb.SelectionalPreference;
import it.uniroma1.nlp.kb.VerbAtlasFrameID;
import it.uniroma1.nlp.kb.WordNetSynsetID;
import it.uniroma1.nlp.verbatlas.VerbAtlas.VerbAtlasFrame.Role;
/**
 * VerbAtlas è una base di conoscenza per la comprensione 
 * del linguaggio naturale organizzata in frame verbali 
 * @author Federica Magliocca
 *
 */

public class VerbAtlas implements Iterable<VerbAtlas.VerbAtlasFrame>{
	/**
	 * unica istanza di VerbAtlas
	 */
	private static VerbAtlas verb;
	/**
	 * dizionario dei frame
	 * key=nome del frame
	 * value=frame
	 */
	private HashMap<String,VerbAtlasFrame> mapNome=new HashMap<>();
	/**
	 * dizionario dei frame
	 * key=id del frame
	 * value=frame
	 */
	private HashMap<String,VerbAtlasFrame> mapId=new HashMap<>();
	/**
	 * lista di Id dei frame
	 */
	private ArrayList<String> chiavi=new ArrayList<>();
	/**
	 * crea tutti i VerbAtlasFrame
	 */
	private VerbAtlas(){
		HashMap<String,String> frame=ApriFile.file2Map("VerbAtlas-1.0.3/VerbAtlas-1.0.3/VA_frame_ids.tsv");
		HashMap<String,String> roles=ApriFile.file2Map("VerbAtlas-1.0.3/VerbAtlas-1.0.3/VA_va2pas.tsv");
		HashMap<String,String> bnSynset=ApriFile.file2MapReverse("VerbAtlas-1.0.3/VerbAtlas-1.0.3/VA_bn2va.tsv");
		Set<String> chiaviFrame=frame.keySet();
	
		for (String c:chiaviFrame) {
			String nome=frame.get(c);
			VerbAtlasFrameID id=new VerbAtlasFrameID(c);
			List<String> listSynset=new ArrayList<>();
			if(bnSynset.get(c)!=null) {
				String[] synset=bnSynset.get(c).split(" ");
			    for (String s:synset) {
			    	listSynset.add(s);
				}
			}
			List<String> listRoles=new ArrayList<>();
			if(roles.get(c)!=null) {
				String[] ruoli=roles.get(c).split("\t+");
				for (String r:ruoli) {
					listRoles.add(r);
		        }
			}
			chiavi.add(c);
			VerbAtlasFrame v=VerbAtlasFrame.builder().addId(id)
							.addNome(nome).addRoles(listRoles)
							.addSynset(listSynset).build();
			mapNome.merge(nome,v,(x,y)->x);
			mapId.put(c,v);
		}
	}
	/**
	 * 
	 * @return una versione della risorsa di tipo VerbAtlasVersion
	 */
	public VerbAtlasVersion getVersion() {
		return new VerbAtlasVersion();
	}
	/**
	 * 
	 * @return l'unica istanza di VerbAtlas
	 */
	public static VerbAtlas getInstance(){
		if(verb==null) verb=new VerbAtlas();
		return verb;
	}
	
	@Override
	/** 
	 * @return l'iteratore sui VerbAtlasFrame
	 */
	public Iterator<VerbAtlasFrame> iterator() {
		return new VerbAtlasIterator(this);
	}
	/**
	 * 
	 * @param nomeFrame
	 * @return il frame corrispondente al nome in input
	 */
	public VerbAtlasFrame getFrame(String nomeFrame) {
		return mapNome.get(nomeFrame.toUpperCase());
	}
	/**
	 * 
	 * @param id
	 * @return  il frame corrispondente all'id in input
	 * @throws FrameNonTrovatoException
	 */
	public VerbAtlasFrame getFrame(ResourceID id) throws FrameNonTrovatoException {
		VerbAtlasFrame v=null;
		if (id instanceof VerbAtlasFrameID) v=mapId.get(id.getId());
		if (id instanceof BabelNetSynsetID) {
			HashMap<String,String> bnSynset=ApriFile.file2Map("VerbAtlas-1.0.3/VerbAtlas-1.0.3/VA_bn2va.tsv");
			String vaId=bnSynset.get(id.getId());
			v=mapId.get(vaId).toSynsetFrame((BabelNetSynsetID)id);
		}
		if (id instanceof PropBankPredicateID) {
			String vaId=null;
			HashMap<String,String> pbPredicate=ApriFile.file2Map("VerbAtlas-1.0.3/VerbAtlas-1.0.3/pb2va.tsv");
			Set<String> chiavi=pbPredicate.keySet();
			for (String s:chiavi) {
				String[] arrayChiavi=s.split(">");
				if (id.getId().equals(arrayChiavi[0])) vaId=arrayChiavi[1];
			}
			v=mapId.get(vaId);
		}
		if (id instanceof WordNetSynsetID ) {
			HashMap<String, String> wn2bn = ApriFile.file2MapReverse("VerbAtlas-1.0.3/VerbAtlas-1.0.3/bn2wn.tsv");
			String bn= new BabelNetSynsetID(wn2bn.get(id.getId())).getId();
			HashMap<String,String> bnSynset=ApriFile.file2Map("VerbAtlas-1.0.3/VerbAtlas-1.0.3/VA_bn2va.tsv");
			String vaId=bnSynset.get(bn);
			if (vaId==null) {
				throw new FrameNonTrovatoException(bn);}
			v=mapId.get(vaId).toSynsetFrame((WordNetSynsetID)id);
		}
		return v;
	}
	/**
	 * 
	 * @param s
	 * @return tutti i frame contenenti il verbo fornito in input
	 */
	public HashSet<VerbAtlasFrame> getFramesByVerb(String s){
		s=s.toLowerCase();
		HashSet<VerbAtlasFrame> verbs=new HashSet<>();
		HashMap<String,String> nomiWN=ApriFile.file2MapReverse("VerbAtlas-1.0.3/VerbAtlas-1.0.3/wn2lemma.tsv");
		Set<String> chiavi=nomiWN.keySet();
		Set<String> nomi=new HashSet<>();
		for (String c:chiavi) {
			if (c.equals(s)) { 
				String[] arrayNomi=nomiWN.get(c).split(" ");
				for (String n:arrayNomi) {
					VerbAtlasFrame v;
					try {
						v = getFrame(new WordNetSynsetID(n));
						if (!nomi.contains(v.getName())) {
							nomi.add(v.getName());
							verbs.add(v);
						}
					} catch (FrameNonTrovatoException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return verbs;
	}
	/**
	 * La classe rappresenta un frame di VerbAtlas
	 * @author Federica Magliocca
	 *
	 */
	public static class VerbAtlasFrame implements Frame,Iterable<BabelNetSynsetID>{
		/**
		 * nome del frame
		 */
		private String nome;
		/**
		 * insieme dei ruoli semantici degli argomenti 
		 * coinvolti nello scenario del frame
		 */
		private TreeSet<Role> ruoli=new TreeSet<>();
		/**
		 * lista dei nomi dei ruoli del frame
		 */
		private List<String> roles=new ArrayList<>();
		/**
		 * id del frame
		 */
		private VerbAtlasFrameID ID;
		/**
		 * lista di synset del frame
		 */
		private List<BabelNetSynsetID> listSynset=new ArrayList<>();
		/**
		 * 
		 * @param nome
		 * @param roles
		 * @param ID
		 */
		
		private VerbAtlasFrame(String nome,List<String> roles,VerbAtlasFrameID ID){
			this.nome=nome;
			this.ID=ID;
			this.roles=roles;
			for (int i=0;i<roles.size();i++) {
				this.ruoli.add(new Role(roles.get(i),ID));
			}
		}
		/**
		 * 
		 * @param nome
		 * @param roles
		 * @param ID
		 * @param synset
		 */
		private VerbAtlasFrame(String nome,List<String> roles,VerbAtlasFrameID ID,List<String> synset) {
			this.nome=nome;
			this.ID=ID;
			this.roles=roles;
			for (int i=0;i<roles.size();i++) {
			     this.ruoli.add(new Role(roles.get(i),ID));
			}
			for (int i=0;i<synset.size();i++) {
				listSynset.add(new BabelNetSynsetID(synset.get(i).trim()));
			}
		}
		/**
		 *
		 * @param id
		 * @return un frame focalizzato sul synset fornito in input
		 */
		public VerbAtlasSynsetFrame toSynsetFrame(BabelNetSynsetID id){
			VerbAtlasSynsetFrame v=null;
			for (BabelNetSynsetID b:listSynset) {
				if (b.equals(id)) v=new VerbAtlasSynsetFrame(nome,roles,ID,b);
			}
			return v;
		}
		/**
		 * 
		 * @param id
		 * @return un frame focalizzato sul synset fornito in input
		 */
		public VerbAtlasSynsetFrame toSynsetFrame(WordNetSynsetID id) {
			return new VerbAtlasSynsetFrame(nome,roles,ID,new WordNetSynsetID(id.getId()));
		}
		/**
		 * compara questo oggetto con quello fornito in input
		 * @param v
		 * @return true se sono uguali, false altrimenti
		 */
		public boolean equals(VerbAtlasFrame v) {
			return this.nome.equals(v.nome);
		}
		@Override
		/**
		 * 
		 * @return l'hashcode dell'oggetto
		 */
		public int hashCode() {
			return nome.hashCode();
		}
		@Override
		/**
		 * 
		 * @return il nome del frame
		 */
		public String getName() {
			return nome;
		}
		@Override
		/**
		 * 
		 * @return i ruoli del frame 
		 */
		public TreeSet<VerbAtlasFrame.Role> getRoles() {
			return ruoli;
		}
		@Override
		/**
		 *@return l'id del frame
		 */
		public VerbAtlasFrameID getId() {
			return ID;
		}
		/**
		 * 
		 * @return il builder di VerbAtlasFrame
		 */
		private static VerbAtlasFrameBuilder builder() {
			return new VerbAtlasFrameBuilder();
		}
		@Override
		/**
		 * 
		 * @return l'iteratore sui BabelNetSynsetID
		 */
		public Iterator<BabelNetSynsetID> iterator() {
			return new VerbAtlasFrameIterator(this);
		}
		/**
		 * la classe rappresenta il ruolo di alcuni argomenti
		 * coinvolti nel frame
		 * @author Federica Magliocca
		 *
		 */
		public static class Role implements Comparable<Role>{
			/**
			 * nome del ruolo
			 */
			private String nome;
			/**
			 * tipo di ruolo
			 */
			private Type tipo;
			/**
			 * lista delle preferenze di selezione di un ruolo
			 */
			private ArrayList<SelectionalPreference> preference=new ArrayList<>();
			/**
			 * l'enumerazione rappresenta i tipi di ruoli possibili
			 * @author Federica Magliocca
			 *
			 */
			/**
			 * insieme degli argomenti impliciti del frame
			 */
			private Set<BabelNetSynsetID> impliciti=new HashSet<>();
			/**
			 * insieme degli argomenti ombra del frame
			 */
			private Set<BabelNetSynsetID> ombra=new HashSet<>();
			public enum Type{
				AGENT,ASSET,ATTRIBUTE,BENEFICIARY,CAUSE,CO_AGENT,CO_PATIENT,
				CO_THEME,DESTINATION,EXPERIENCER,EXTENT,GOAL,IDIOM,INSTRUMENT,
				LOCATION,MATERIAL,PATIENT,PRODUCT,PURPOSE,RECIPIENT,RESULT,SOURCE,
				STIMULUS,THEME,TIME,TOPIC,VALUE;
			}
			/**
			 * 
			 * @param nome
			 * @param frameID
			 */
			public Role(String nome,ResourceID frameID){
				this.nome=nome;
				HashMap<String,String> sp=ApriFile.file2Map("VerbAtlas-1.0.3/VerbAtlas-1.0.3/VA_va2sp.tsv");
			    HashMap<String,String> spB=ApriFile.file2Map("VerbAtlas-1.0.3/VerbAtlas-1.0.3/VA_bn2sp.tsv");
	
			    String[] selPref=null;
			    if (frameID instanceof BabelNetSynsetID) {
			    	String[] spArray=spB.get(frameID.getId()).split("\t+");
					    if(spArray.length>1)  
					            for (int j=0;j<spArray.length;j+=2) {
					            	if (this.nome.equals(spArray[j])) 
									selPref=spArray[j+1].split("\\|");
								}
				}
					
		        if (!(frameID instanceof BabelNetSynsetID)) {
		        	
		        	String[] spArray=sp.get(frameID.getId()).split("\t+");
				    if(spArray.length>1)
				    	for (int j=0;j<spArray.length;j+=2) {
				    		if (this.nome.equals(spArray[j])) 
							selPref=spArray[j+1].split("\\|");
					    }
				}
				if (selPref==null) preference.add(new SelectionalPreference("entity"));
				else 
					for (int k=0;k<selPref.length;k++) {
						preference.add(new SelectionalPreference(selPref[k]));
				    }
				
			    switch(nome) {
				case "Agent":tipo=Type.AGENT;break;
				case "Asset":tipo=Type.ASSET;break;
				case "Attribute":tipo=Type.ATTRIBUTE;break;
				case "Beneficiary":tipo=Type.BENEFICIARY;break;
				case "Cause":tipo=Type.CAUSE;break;
				case "Co-Agent":tipo=Type.CO_AGENT;break;
				case "Co-Patient":tipo=Type.CO_PATIENT;break;
				case "Co-Theme":tipo=Type.CO_THEME;break;
				case "Destination":tipo=Type.DESTINATION;break;
				case "Experiencer":tipo=Type.EXPERIENCER;break;
				case "Extent":tipo=Type.EXTENT;break;
				case "Goal":tipo=Type.GOAL;break;
				case "Idiom":tipo=Type.IDIOM;break;
				case "Instrument":tipo=Type.INSTRUMENT;break;
				case "Location":tipo=Type.LOCATION;break;
				case "Material":tipo=Type.MATERIAL;break;
				case "Patient":tipo=Type.PATIENT;break;
				case "Product":tipo=Type.PRODUCT;break;
				case "Purpose":tipo=Type.PURPOSE;break;
				case "Recipient":tipo=Type.RECIPIENT;break;
				case "Result":tipo=Type.RESULT;break;
				case "Source":tipo=Type.SOURCE;break;
				case "Stimulus":tipo=Type.STIMULUS;break;
				case "Theme":tipo=Type.THEME;break;
				case "Time":tipo=Type.TIME;break;
				case "Topic":tipo=Type.TOPIC;break;
				case "Value":tipo=Type.VALUE;break;
				}
			}
			/**
			 * 
			 * @param nome
			 * @param frameID
			 * @param selP
			 */
			public Role(String nome,ResourceID frameID,String selP){
				this(nome,frameID);
				String[] selPArray=selP.split("\\|");
				for (int i=0;i<selPArray.length;i++) {
					preference.add(new SelectionalPreference(new BabelNetSynsetID(selPArray[i])));
				}
			}
			@Override
			/**
			 * compara l'oggetto con quello fornito in input
			 * @return un numero positivo se l'oggetto è maggiore di quello in input,
			 * 0 se somo uguali e un numero negativo altrimenti
			 */
			public int compareTo(Role o) {
				return this.nome.compareTo(o.nome);
			}
			/**
			 * 
			 * @param idb
			 * @return l'insieme degli argomenti impliciti del frame focalizzato 
			 * sul BabelNet synset fornito in input
			 */
			public Set<BabelNetSynsetID> getImplicitArguments(BabelNetSynsetID idb) {
				HashMap<String,String> implicit=ApriFile.file2Map("VerbAtlas-1.0.3/VerbAtlas-1.0.3/VA_bn2implicit.tsv");
				if(implicit.get(idb.getId())!=null) {
					String[] implicitArray=implicit.get(idb.getId()).split("\t+");
				    for (int j=0;j<implicitArray.length;j+=2)
				    	if(implicitArray[j].equals(this.nome))
				    		impliciti.add(new BabelNetSynsetID(implicitArray[j+1]));
				} 
				return impliciti;
			}
			public Set<BabelNetSynsetID> getShadowArguments(BabelNetSynsetID idb){
				HashMap<String,String> shadow=ApriFile.file2Map("VerbAtlas-1.0.3/VerbAtlas-1.0.3/VA_bn2shadow.tsv");
				if(shadow.get(idb.getId())!=null) {
					String[] shadowArray=shadow.get(idb.getId()).split("\t+");
					for (int j=0;j<shadowArray.length;j+=2)
						if(shadowArray[j].equals(this.nome))
							ombra.add(new BabelNetSynsetID(shadowArray[j+1]));
				}
				return ombra;
			}
		}
		/**
		 * la classe costruisce l'oggetto VerbAtlasFrame
		 * @author Federica Magliocca
		 *
		 */

		public static class VerbAtlasFrameBuilder{
			/**
			 * nome del frame
			 */
			private String nome;
			/**
			 * id del frame
			 */
			private VerbAtlasFrameID ID;
			/**
			 * lista dei nomi dei ruoli del frame
			 */
			private List<String> roles=new ArrayList<>();
			/**
			 * lista dei synset del frame
			 */
			private List<String> synset=new ArrayList<>();
			/**
			 * imposta il nome del frame da costruire
			 * @param nome
			 * @return un VerbAtlasFrameBuilder
			 */
			public VerbAtlasFrameBuilder addNome(String nome) {
				this.nome=nome;
				return this;
			}
			/**
			 * imposta l'id del frame da costruire
			 * @param id
			 * @return un VerbAtlasFrameBuilder
			 */
			public VerbAtlasFrameBuilder addId(VerbAtlasFrameID id) {
				ID=id;
				return this;
			}
			/**
			 * aggiunge ai ruoli la collezione di ruoli fornita in input
			 * @param roles
			 * @return un VerbAtlasFrameBuilder
			 */
			public VerbAtlasFrameBuilder addRoles(Collection<String> roles) {
				for (String r:roles) {
					if (!this.roles.contains(r)) this.roles.add(r);
				}
				return this;
			}
			/**
			 * aggiunge ai ruoli il ruolo fornito in input
			 * @param roles
			 * @return un VerbAtlasFrameBuilder
			 */
			public VerbAtlasFrameBuilder addRoles(String roles) {
				if (!this.roles.contains(roles)) this.roles.add(roles);
				return this;
			}
			/**
			 * aggiunge ai synset il synset fornito in input
			 * @param synset
			 * @return un VerbAtlasFrameBuilder
			 */
			public VerbAtlasFrameBuilder addSynset(String synset) {
				if (!this.synset.contains(synset)) this.synset.add(synset);
				return this;
			}
			/**
			 * aggiunge ai synset la collezione di synset fornita in input
			 * @param synset
			 * @return un VerbAtlasFrameBuilder
			 */
			public VerbAtlasFrameBuilder addSynset(Collection<String> synset) {
				for (String s:synset) {
					if (!this.synset.contains(s)) this.synset.add(s);
				}
				return this;
			}
			/**
			 * costruisce il frame
			 * @return un'istanza di VerbAtlasFrame
			 */
			public VerbAtlasFrame build(){
				return new VerbAtlasFrame(nome,roles,ID,synset);
			}
		}
		/**
		 * la classe rappresenta un frame focalizzzato su un synset
		 * @author Federica Magliocca
		 *
		 */
		public class VerbAtlasSynsetFrame extends VerbAtlasFrame implements Frame{
			/**
			 * id del BabelNet synset 
			 */
			private BabelNetSynsetID idb;
			/**
			 * id del WordNet synset
			 */
			private WordNetSynsetID idw;
			/**
			 * insieme di ruoli frame
			 */
			private TreeSet<Role> setRoles=new TreeSet<>();
			/**
			 * lista dei nomi dei ruoli del frame
			 */
			private List<String> roles=new ArrayList<>();
	
			/**
			 * 
			 * @param nome
			 * @param roles
			 * @param ID
			 * @param id
			 */
			private VerbAtlasSynsetFrame(String nome,List<String> roles,VerbAtlasFrameID ID,BabelNetSynsetID id){
				super(nome,roles,ID);
				this.idb=id;
				this.roles=roles;
				HashMap<String, String> bn2wn = ApriFile.file2Map("VerbAtlas-1.0.3/VerbAtlas-1.0.3/bn2wn.tsv");
				idw= new WordNetSynsetID(bn2wn.get(id.getId()));
				
				for (int i=0;i<roles.size();i++) {
					this.setRoles.add(new Role(roles.get(i),id));
					
				}
				implicitAndShadow();
				
			}
			/**
			 * 
			 * @param nome
			 * @param roles
			 * @param ID
			 * @param id
			 */
			private VerbAtlasSynsetFrame(String nome,List<String> roles,VerbAtlasFrameID ID,WordNetSynsetID id){
				super(nome,roles,ID);
				this.idw=id;
				this.roles=roles;
				
				HashMap<String, String> wn2bn= ApriFile.file2MapReverse("VerbAtlas-1.0.3/VerbAtlas-1.0.3/bn2wn.tsv");
				idb= new BabelNetSynsetID(wn2bn.get(id.getId().trim()));
				
				for (int i=0;i<roles.size();i++) {
					this.setRoles.add(new Role(roles.get(i),idb));
				}
				implicitAndShadow();
			}
			/**
			 * aggiunge all'insieme di ruoli i ruoli impliciti e ombra
			 */
			private void implicitAndShadow(){
				HashMap<String,String> shadow=ApriFile.file2Map("VerbAtlas-1.0.3/VerbAtlas-1.0.3/VA_bn2shadow.tsv");
				HashMap<String,String> implicit=ApriFile.file2Map("VerbAtlas-1.0.3/VerbAtlas-1.0.3/VA_bn2implicit.tsv");
				if(implicit.get(idb.getId())!=null) {
					String[] implicitArray=implicit.get(idb.getId()).split("\t+");
				    for (int j=0;j<implicitArray.length;j+=2)
				    	this.setRoles.add(new Role(implicitArray[j],idb,implicitArray[j+1]));
				} 
				if(shadow.get(idb.getId())!=null) {
					String[] shadowArray=shadow.get(idb.getId()).split("\t+");
					for (int j=0;j<shadowArray.length;j+=2)
				    	this.setRoles.add(new Role(shadowArray[j],idb,shadowArray[j+1]));
				}
			}
			/**
			 * 
			 * @return un VerbAtlasFrame
			 */
			public VerbAtlasFrame toFrame(){
				return new VerbAtlasFrame(nome,roles,ID);
			}
			/**
			 * @return una sequenza ordinata di ruoli
			 */
			public TreeSet<Role> getRoles(){
				return setRoles;
			}
		}
		/**
		 * la classe rapprsenta l'iteratore sui BabelNetSynsetID
		 * @author Federica Magliocca
		 *
		 */
		public class VerbAtlasFrameIterator implements Iterator<BabelNetSynsetID>{
			private VerbAtlasFrame v;
			/**
			 * contatore
			 */
			private int k;
			/**
			 * 
			 * @param v
			 */
			public VerbAtlasFrameIterator(VerbAtlasFrame v) {
				this.v=v;
			}
			@Override
			/**
			 * 
			 * @return true se esiste l'elemento successivo,false altrimenti
			 */
			public boolean hasNext() {
				return k<v.listSynset.size();
			}
			@Override
			/**
			 *  
			 * @return l'elemento successivo 
			 */
			public BabelNetSynsetID next() {
				return hasNext() ? v.listSynset.get(k++) : null;
			}
		}

	}
	/**
	 * la classe rappresenta la versione di VerbAtlas
	 * @author Federica Magliocca
	 *
	 */
	public static class VerbAtlasVersion{
		private static final String version="V1_0_2";
		/**
		 * 
		 * @return la data di rilascio della versione
		 */
		public LocalDate getReleaseDate() {
			return LocalDate.of(2020,3,23);
		}
		public String toString() {
			return version;
		}
	}
	/**
	 * la classe rappresenta l'iteratore sui VerbAtlasFrame
	 * @author Federica Magliocca
	 *
	 */
	public class VerbAtlasIterator implements Iterator<VerbAtlasFrame>{
		private VerbAtlas verb;
		/**
		 * contatore
		 */
		private int k;
		/**
		 * 
		 * @param verb
		 */
		public VerbAtlasIterator(VerbAtlas verb) {
			this.verb=verb;
		}
		@Override
		/**
		 * 
		 * @return true se esiste l'elemento successivo,false altrimenti
		 */
		public boolean hasNext() {
			return k<verb.chiavi.size();
		}
		@Override
		/**
		 * 
		 * @return l'elemento successivo
		 */
		public VerbAtlasFrame next() {
			return hasNext() ? verb.mapId.get(verb.chiavi.get(k++)): null;
		}
	}
	/**
	 * Segnala la presenza di un frame inesistente
	 * @author Federica Magliocca
	 *
	 */
	public class FrameNonTrovatoException extends Exception{
		public FrameNonTrovatoException(String s) {
			super("il frame corrispondente "+s+" non è stato trovato");
		}
	}
	
}

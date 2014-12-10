
/**
 * A class representing a name example.  A name example consists of a first name
 * and whether or not that first name is male or female.
 * 
 * @author Szeyin Lee and Viona Lam
 * @version 12/8/2014
 *
 */
public class IPAExample {

	private String ipa;
	private Language language;

	/**
	 * Create a new name example
	 * 
	 * @param name the name
	 * @param isMale whether the example is male (false for female)
	 */
	public IPAExample(String ipa, Language language){
		this.ipa = ipa;
		this.language = language;
	}
	
	/**
	 * @return the name associated with this example
	 */
	public String getName() {
		return ipa;
	}

	/**
	 * @return whether or not this ipa example is a chinese
	 */
	public boolean isChinese() {
		return (this.language == Language.CHINESE);
	}

	/**
	 * @return whether or not this ipa example is english
	 */
	public boolean isEnglish(){
		return (this.language == Language.ENGLISH);
	}
	
	/**
	 * @return whether or not this ipa example is japanese
	 */
	public boolean isJapanese(){
		return (this.language == Language.JAPANESE);
	}

	/**
	 * @return whether or not this ipa example is japanese
	 */
	public boolean isSpanish(){
		return (this.language == Language.SPANISH);
	}

	public String toString(){
		if( this.isChinese() ){
			return ipa + "\tchinese";
		} else if ( this.isEnglish()) {
			return ipa + "\tenglish";
		} else if ( this.isSpanish()) {
			return ipa + "\tspanish";
		} else{
			return ipa + "\tjapanese";
		}
	}
}

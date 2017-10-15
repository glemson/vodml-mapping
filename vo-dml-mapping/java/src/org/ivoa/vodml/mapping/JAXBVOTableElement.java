package org.ivoa.vodml.mapping;
/**
 * Common baseclass of all classes generated by JAXB from VOTable schema
 * @author gerard
 *
 */
public abstract class JAXBVOTableElement {

	private int rankInDocument;
	private int countInParent;
	public int getRankInDocument() {
		return rankInDocument;
	}
	public void setRankInDocument(int rankInDocument) {
		this.rankInDocument = rankInDocument;
	}
	public int getCountInParent() {
		return countInParent;
	}
	public void setCountInParent(int countInParent) {
		this.countInParent = countInParent;
	}
}
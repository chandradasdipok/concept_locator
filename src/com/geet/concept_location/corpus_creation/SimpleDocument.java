package com.geet.concept_location.corpus_creation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import com.geet.concept_location.indexing_vsm.Term;
public class SimpleDocument implements Comparable<SimpleDocument>, Serializable{
	protected String article= "";
	public String docInJavaFile;
	public double score = 0.0;
	public SimpleDocument(){
	}
	
	
	public SimpleDocument( String docInJavaFile, String article) {
		super();
		this.article = article;
		this.docInJavaFile = docInJavaFile;
	}


	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public void setArticle(String article) {
		this.article = article;
	}
	public String getArticle() {
		return article;
	}
	public double getTF(String termString){
		double tf = 0.0;
		for (Term term : getTerms()) {
			if (term.isSame(new Term(termString))) {
				return term.termFrequency;
			}
		}
		return tf;
	}
	public List<Term> getTerms() {
		List<Term> terms = new ArrayList<Term>();
		StringTokenizer stringTokenizer = new StringTokenizer(getArticle(), JavaLanguage.getWhiteSpace()+JavaLanguage.PROGRAMING_LANGUAGE_SYNTAX+JavaLanguage.OPERATORS, false);
		while (stringTokenizer.hasMoreTokens()) {
			String token = stringTokenizer.nextToken();
			token = token.toLowerCase();
			if (StopWords.isStopword(token)) {
				continue;
			}
			Term candidateTerm = new Term(token, 1);
			int pass = -1;
			for (int i = 0; i < terms.size(); i++) {
				if (terms.get(i).isSame(candidateTerm)) {
					pass = i;
					terms.get(i).termFrequency++;
					continue;
				}
			}
			if (pass == -1) {
				terms.add(candidateTerm);
			}
		}
		return terms;
	}
	public boolean hasTerm(Term term){
		for (Term trm : getTerms()) {
			if (trm.isSame(term)) {
				return true;
			}
		}
		return false;
	}
	@Override
	public int compareTo(SimpleDocument simpleDocument) {
		return Double.compare(score, simpleDocument.score);
	}
	public List<String> getTermsInString(){
		List<String> termsInString = new ArrayList<String>();
		for (Term term : getTerms()) {
			termsInString.add(term.termString);
		}
		return termsInString;
	}
}
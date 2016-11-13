package com.geet.concept_location.indexing_lsi;
import java.io.Serializable;

import com.geet.concept_location.indexing_vsm.Term;
public class LsiTerm  implements Comparable<LsiTerm>, Serializable{
	public String term;
	public Vector vector = new Vector(2);
	public double score= -1;
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public LsiTerm(String term, Vector vector) {
		setTerm(term);
		setVector(vector);
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public Vector getVector() {
		return vector;
	}
	public void setVector(Vector vector) {
		this.vector = vector;
	}
	public boolean isSame(LsiTerm lsiTerm) {
		if (term.equals(lsiTerm.term)) {
			return true;
		}
		return false;
	}
	public boolean isSame(String stringTerm) {
		if (term.equals(stringTerm)) {
			return true;
		}
		return false;
	}
	public String toCSVString() {
		return term+","+score+","+vector.toCSVString();
	}
	@Override
	public int compareTo(LsiTerm lsiTerm) {
		if (score > lsiTerm.score) {
			return 1;
		}
		return -1;
	}
	
	public boolean isSameInIR(Term trm){
		return new Term(term).isSameInIR(trm);
	}
}

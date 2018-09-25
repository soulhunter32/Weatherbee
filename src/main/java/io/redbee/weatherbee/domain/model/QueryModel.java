package io.redbee.weatherbee.domain.model;

public class QueryModel {

	private ResultsModel results;

	public ResultsModel getResults() {
		return results;
	}
	public void setResults(ResultsModel results) {
		this.results = results;
	}

	@Override
	public String toString() {
		return "QueryModel [results=" + results + "]";
	}
}

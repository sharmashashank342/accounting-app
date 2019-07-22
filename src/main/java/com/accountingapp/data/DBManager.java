package com.accountingapp.data;

// Preserved as Main DB and Test DB can be Segregated based on managers
public interface DBManager {

	// Populate Data can have different Implementations
	void populateData();
}

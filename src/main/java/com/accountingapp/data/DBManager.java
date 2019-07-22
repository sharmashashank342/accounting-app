package com.accountingapp.data;

import com.accountingapp.data.managers.AccountsManager;
import com.accountingapp.data.managers.UserManager;

// Preserved as Main DB and Test DB can be Segregated based on managers
public interface DBManager {

	// Populate Test Data can have different Implementations
	void populateTestData();
}

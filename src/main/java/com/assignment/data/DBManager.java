package com.assignment.data;

import com.assignment.data.managers.AccountsManager;
import com.assignment.data.managers.UserManager;

public interface DBManager {

	UserManager getUserManager();

	AccountsManager getAccountsManager();

	void populateTestData();
}

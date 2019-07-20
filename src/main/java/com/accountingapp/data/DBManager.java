package com.accountingapp.data;

import com.accountingapp.data.managers.AccountsManager;
import com.accountingapp.data.managers.UserManager;

public interface DBManager {

	UserManager getUserManager();

	AccountsManager getAccountsManager();

	void populateTestData();
}

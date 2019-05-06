#ifndef BANK_ICE
#define BANK_ICE

module Bankk
{
	enum ErrorType
	{
		BadPesel = 0,
		AccountAlreadyExist = 1,
		BadPassword = 2,
		BadCurrency = 3,
		NoError = 4,
	}

	enum CURRENCY
	{
		USD = 0,
		EUR = 1,
		PLN = 2,
		RUB = 3,
		KR  = 4,
	};

	enum AccountType
	{
		premium = 0,
		standard = 1,
	};

	struct AccountCreationInfo
	{
		ErrorType error;
		AccountType type;
		long password;		
	};
	
	struct Money
	{
		CURRENCY currency;
		long amount;
	}
	
	class CreditResponse
	{
		bool fAccepted;
		optional(1) Money costNational;
		optional(2) Money costForeign;
	};
	
	interface AccountStandard
	{
		AccountType getAccType();
		long balance();
	};
	
	interface AccountPremium extends AccountStandard
	{
		CreditResponse creditRequest(Money c,int Time);
	};
	
	
  
	interface Bank
	{
		AccountCreationInfo createAccount(string name ,string surname,string pesel,long income);
		AccountStandard* logIn(string pesel);
	};

};

#endif
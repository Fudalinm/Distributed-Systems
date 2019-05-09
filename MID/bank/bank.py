import sys
import grpc

sys.path.append("../Generated")
from currencyProto_pb2 import *
from currencyProto_pb2_grpc import *
from currency_Exchange import *
import re
import random
import Ice
import Bankk

exchange = 0
national = 0


class AccountStandardI(Bankk.AccountStandard):
    def __init__(self, name, surname, pesel, income, password, balance):
        self.typ = Bankk.AccountType.standard
        self.name = name
        self.surname = surname
        self.pesel = pesel
        self.income = income
        self.password = password
        self.bal = balance

    def toString(self):
        return "premium," + self.name + "," + self.surname + "," + self.pesel + "," + str(self.income) + "," + str(
            self.password) + "," + str(self.bal) + "\n"

    def getAccType(self,current):
        return self.typ

    def balance(self, current):
        print(self.bal)
        return self.bal


class AccountPremiumI(AccountStandardI, Bankk.AccountPremium):
    def __init__(self, name, surname, pesel, income, password, balance):
        AccountStandardI.__init__(self, name, surname, pesel, income, password, balance)
        self.typ = Bankk.AccountType.premium
        # something more

    def toString(self):
        return "premium," + self.name + "," + self.surname + "," + self.pesel + "," + str(self.income) + "," + str(
            self.password) + "," + str(self.balance) + "\n"

    def creditRequest(self, c, Time,current):
        global national
        print("Calculating credit offer")
        if c.currency.value not in exchange.currencyList:
            print("Bad currency for this bank")
            return Bankk.CreditResponse(False, None, None)
        m1 = None
        m2 = None
        # print(exchange.currencyRatings)
        for (cc1, cc2, ratio) in exchange.currencyRatings:
            # print("xDDD")
            # print(cc1)
            # print(cc2)
            # print(national)
            # print(c.currency.value)
            # print("xDDD")

            if cc1 == national and cc2 == c.currency.value:
                print("Found 1")
                m1 = Bankk.Money(Bankk.CURRENCY.valueOf(cc1), (c.amount * ((100 + Time) / 100) * ratio)/1000)
            if cc1 == c.currency.value and cc2 == national:
                print("Found 2")
                m2 = Bankk.Money(Bankk.CURRENCY.valueOf(cc1), (c.amount * ((100 + Time) / 100) * ratio)/1000)
        if national == c.currency.value:
            m1 = Bankk.Money(Bankk.CURRENCY.valueOf(cc1), c.amount * ((100 + Time) / 100))
            m2 = Bankk.Money(Bankk.CURRENCY.valueOf(cc1), c.amount * ((100 + Time) / 100))
        response = Bankk.CreditResponse(True, m1, m2)
        return response


class BankImp(Bankk.Bank):
    def __init__(self, name, currencyList, adapter):
        self.name = name
        global exchange
        exchange = Exchange(currencyList, "5005")
        self.clientsDict = {}
        global national
        national = currencyList[0]
        self.adapter = adapter

    def createAccount(self, name, surname, pesel, income, current):
        fPrime = Bankk.AccountType.standard
        if income > 2000:
            fPrime = Bankk.AccountType.premium
        if pesel in self.clientsDict:
            print("ERROR Message client already exist")
            return Bankk.AccountCreationInfo(Bankk.ErrorType.AccountAlreadyExist, Bankk.AccountType.standard, -1)
        if not re.match('[0-9]{11}$', pesel):
            print("ERROR Message bad pesel")
            return Bankk.AccountCreationInfo(Bankk.ErrorType.BadPesel, Bankk.AccountType.standard, -1)
        password = 11 + income

        if not fPrime == Bankk.AccountType.standard:
            account = AccountPremiumI(name, surname, pesel, income, password, random.randint(-1000, 1000))
        else:
            account = AccountStandardI(name, surname, pesel, income, password, random.randint(-1000, 1000))

        current.adapter.add(account, Ice.stringToIdentity(pesel))
        self.clientsDict[pesel] = account
        print("Succesfully added client account " + account.toString())
        return Bankk.AccountCreationInfo(Bankk.ErrorType.NoError, fPrime, password)

    def logIn(self, pesel, current):
        print(self.clientsDict.keys())
        print(pesel)
        if pesel in self.clientsDict.keys():
            print("there is acc in database")
            acc = self.clientsDict[pesel]
            print(str(acc.password))
            print(current.ctx['password'])
            if str(acc.password) == current.ctx['password'].strip():
                accountProxy = Bankk.AccountStandardPrx.checkedCast(current.adapter.createProxy(Ice.stringToIdentity(pesel)))
                print("Returning true proxy to client")
                return accountProxy
            else:
                print("invalid password sending None")
                return None
        else:
            print("No such pesel in database")
            return None

class BankServer():
    def __init__(self, name, currencyList, adapterName,port):
        self.adapter = communicator.createObjectAdapterWithEndpoints(adapterName, "default -p " + port)
        object = BankImp(name, currencyList, self.adapter)
        self.adapter.add(object, communicator.stringToIdentity(name))
        self.adapter.activate()
        communicator.waitForShutdown()


if __name__ == "__main__":
    with Ice.initialize(sys.argv) as communicator:
        bs = BankServer("Bank1", [EUR, USD, PLN], "SimplePrinterAdapter","10000")
        #bs = BankServer("Bank2", [EUR, USD, KR], "SimplePrinterAdapter2","10001")

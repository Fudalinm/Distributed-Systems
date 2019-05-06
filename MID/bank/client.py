import sys

sys.path.append("../Generated")
from currencyProto_pb2 import *
from currencyProto_pb2_grpc import *
from currency_Exchange import *
import Ice

import Bankk

password = 0
pesel = ""
accountProxy = None
fpremium = None

if __name__ == "__main__":
    with Ice.initialize(sys.argv) as communicator:

        print("give pesel")
        pesel = sys.stdin.readline().strip()
        print("Give Bank name")
        bankName = sys.stdin.readline().strip()
        print("Give port number")
        port = sys.stdin.readline().strip()

        base = communicator.stringToProxy(bankName + ":default -p " + port)
        bank_proxy = Bankk.BankPrx.checkedCast(base)
        if not bank_proxy:
            raise RuntimeError("Invalid proxy")

        print("Type C to create account")
        if "C" == sys.stdin.readline().strip():
            print("Give youre income")
            income = int(sys.stdin.readline().strip())
            print("Trying to create account")
            out = bank_proxy.createAccount("Imie", "Nazwisko", pesel, income)
            fpremium = out.type
            if not (out.error == Bankk.ErrorType.NoError):
                print('Couldnt create account maybe try to use it with old password')
            else:
                password = out.password
                print(str(password))
        while accountProxy is None:
            print("\nNow you need to obtain proxy to account give password")
            password = sys.stdin.readline()
            ctx = {'password': password}
            accountProxy = bank_proxy.logIn(pesel, ctx)
            if accountProxy is None:
                print("log in not successful")
            else:
                print("obtaing proxyAcc success")
                print(accountProxy)
        fpremium = accountProxy.getAccType()
        print("Trying to check balance")
        val = accountProxy.balance()
        print(fpremium)
        print(val)
        if fpremium == Bankk.AccountType.premium:
            accountProxy.__class__ = Bankk.AccountPremiumPrx
            response = accountProxy.creditRequest(Bankk.Money(Bankk.CURRENCY.USD, 2000), 2)
            print("Response faccepted %r money1" % (response.fAccepted))
            print(str(response.costNational.currency) + " cost: " + str(response.costNational.amount))
            print("Response faccepted %r money2" % (response.fAccepted))
            print(str(response.costForeign.currency) + " cost: " + str(response.costForeign.amount))

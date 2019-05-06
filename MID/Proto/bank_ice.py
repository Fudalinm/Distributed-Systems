# -*- coding: utf-8 -*-
#
# Copyright (c) ZeroC, Inc. All rights reserved.
#
#
# Ice version 3.7.2
#
# <auto-generated>
#
# Generated from file `bank.ice'
#
# Warning: do not edit this file.
#
# </auto-generated>
#

from sys import version_info as _version_info_
import Ice, IcePy

# Start of module Bankk
_M_Bankk = Ice.openModule('Bankk')
__name__ = 'Bankk'

if 'ErrorType' not in _M_Bankk.__dict__:
    _M_Bankk.ErrorType = Ice.createTempClass()
    class ErrorType(Ice.EnumBase):

        def __init__(self, _n, _v):
            Ice.EnumBase.__init__(self, _n, _v)

        def valueOf(self, _n):
            if _n in self._enumerators:
                return self._enumerators[_n]
            return None
        valueOf = classmethod(valueOf)

    ErrorType.BadPesel = ErrorType("BadPesel", 0)
    ErrorType.AccountAlreadyExist = ErrorType("AccountAlreadyExist", 1)
    ErrorType.BadPassword = ErrorType("BadPassword", 2)
    ErrorType.BadCurrency = ErrorType("BadCurrency", 3)
    ErrorType.NoError = ErrorType("NoError", 4)
    ErrorType._enumerators = { 0:ErrorType.BadPesel, 1:ErrorType.AccountAlreadyExist, 2:ErrorType.BadPassword, 3:ErrorType.BadCurrency, 4:ErrorType.NoError }

    _M_Bankk._t_ErrorType = IcePy.defineEnum('::Bankk::ErrorType', ErrorType, (), ErrorType._enumerators)

    _M_Bankk.ErrorType = ErrorType
    del ErrorType

if 'CURRENCY' not in _M_Bankk.__dict__:
    _M_Bankk.CURRENCY = Ice.createTempClass()
    class CURRENCY(Ice.EnumBase):

        def __init__(self, _n, _v):
            Ice.EnumBase.__init__(self, _n, _v)

        def valueOf(self, _n):
            if _n in self._enumerators:
                return self._enumerators[_n]
            return None
        valueOf = classmethod(valueOf)

    CURRENCY.USD = CURRENCY("USD", 0)
    CURRENCY.EUR = CURRENCY("EUR", 1)
    CURRENCY.PLN = CURRENCY("PLN", 2)
    CURRENCY.RUB = CURRENCY("RUB", 3)
    CURRENCY.KR = CURRENCY("KR", 4)
    CURRENCY._enumerators = { 0:CURRENCY.USD, 1:CURRENCY.EUR, 2:CURRENCY.PLN, 3:CURRENCY.RUB, 4:CURRENCY.KR }

    _M_Bankk._t_CURRENCY = IcePy.defineEnum('::Bankk::CURRENCY', CURRENCY, (), CURRENCY._enumerators)

    _M_Bankk.CURRENCY = CURRENCY
    del CURRENCY

if 'AccountType' not in _M_Bankk.__dict__:
    _M_Bankk.AccountType = Ice.createTempClass()
    class AccountType(Ice.EnumBase):

        def __init__(self, _n, _v):
            Ice.EnumBase.__init__(self, _n, _v)

        def valueOf(self, _n):
            if _n in self._enumerators:
                return self._enumerators[_n]
            return None
        valueOf = classmethod(valueOf)

    AccountType.premium = AccountType("premium", 0)
    AccountType.standard = AccountType("standard", 1)
    AccountType._enumerators = { 0:AccountType.premium, 1:AccountType.standard }

    _M_Bankk._t_AccountType = IcePy.defineEnum('::Bankk::AccountType', AccountType, (), AccountType._enumerators)

    _M_Bankk.AccountType = AccountType
    del AccountType

if 'AccountCreationInfo' not in _M_Bankk.__dict__:
    _M_Bankk.AccountCreationInfo = Ice.createTempClass()
    class AccountCreationInfo(object):
        def __init__(self, error=_M_Bankk.ErrorType.BadPesel, type=_M_Bankk.AccountType.premium, password=0):
            self.error = error
            self.type = type
            self.password = password

        def __hash__(self):
            _h = 0
            _h = 5 * _h + Ice.getHash(self.error)
            _h = 5 * _h + Ice.getHash(self.type)
            _h = 5 * _h + Ice.getHash(self.password)
            return _h % 0x7fffffff

        def __compare(self, other):
            if other is None:
                return 1
            elif not isinstance(other, _M_Bankk.AccountCreationInfo):
                return NotImplemented
            else:
                if self.error is None or other.error is None:
                    if self.error != other.error:
                        return (-1 if self.error is None else 1)
                else:
                    if self.error < other.error:
                        return -1
                    elif self.error > other.error:
                        return 1
                if self.type is None or other.type is None:
                    if self.type != other.type:
                        return (-1 if self.type is None else 1)
                else:
                    if self.type < other.type:
                        return -1
                    elif self.type > other.type:
                        return 1
                if self.password is None or other.password is None:
                    if self.password != other.password:
                        return (-1 if self.password is None else 1)
                else:
                    if self.password < other.password:
                        return -1
                    elif self.password > other.password:
                        return 1
                return 0

        def __lt__(self, other):
            r = self.__compare(other)
            if r is NotImplemented:
                return r
            else:
                return r < 0

        def __le__(self, other):
            r = self.__compare(other)
            if r is NotImplemented:
                return r
            else:
                return r <= 0

        def __gt__(self, other):
            r = self.__compare(other)
            if r is NotImplemented:
                return r
            else:
                return r > 0

        def __ge__(self, other):
            r = self.__compare(other)
            if r is NotImplemented:
                return r
            else:
                return r >= 0

        def __eq__(self, other):
            r = self.__compare(other)
            if r is NotImplemented:
                return r
            else:
                return r == 0

        def __ne__(self, other):
            r = self.__compare(other)
            if r is NotImplemented:
                return r
            else:
                return r != 0

        def __str__(self):
            return IcePy.stringify(self, _M_Bankk._t_AccountCreationInfo)

        __repr__ = __str__

    _M_Bankk._t_AccountCreationInfo = IcePy.defineStruct('::Bankk::AccountCreationInfo', AccountCreationInfo, (), (
        ('error', (), _M_Bankk._t_ErrorType),
        ('type', (), _M_Bankk._t_AccountType),
        ('password', (), IcePy._t_long)
    ))

    _M_Bankk.AccountCreationInfo = AccountCreationInfo
    del AccountCreationInfo

if 'Money' not in _M_Bankk.__dict__:
    _M_Bankk.Money = Ice.createTempClass()
    class Money(object):
        def __init__(self, currency=_M_Bankk.CURRENCY.USD, amount=0):
            self.currency = currency
            self.amount = amount

        def __hash__(self):
            _h = 0
            _h = 5 * _h + Ice.getHash(self.currency)
            _h = 5 * _h + Ice.getHash(self.amount)
            return _h % 0x7fffffff

        def __compare(self, other):
            if other is None:
                return 1
            elif not isinstance(other, _M_Bankk.Money):
                return NotImplemented
            else:
                if self.currency is None or other.currency is None:
                    if self.currency != other.currency:
                        return (-1 if self.currency is None else 1)
                else:
                    if self.currency < other.currency:
                        return -1
                    elif self.currency > other.currency:
                        return 1
                if self.amount is None or other.amount is None:
                    if self.amount != other.amount:
                        return (-1 if self.amount is None else 1)
                else:
                    if self.amount < other.amount:
                        return -1
                    elif self.amount > other.amount:
                        return 1
                return 0

        def __lt__(self, other):
            r = self.__compare(other)
            if r is NotImplemented:
                return r
            else:
                return r < 0

        def __le__(self, other):
            r = self.__compare(other)
            if r is NotImplemented:
                return r
            else:
                return r <= 0

        def __gt__(self, other):
            r = self.__compare(other)
            if r is NotImplemented:
                return r
            else:
                return r > 0

        def __ge__(self, other):
            r = self.__compare(other)
            if r is NotImplemented:
                return r
            else:
                return r >= 0

        def __eq__(self, other):
            r = self.__compare(other)
            if r is NotImplemented:
                return r
            else:
                return r == 0

        def __ne__(self, other):
            r = self.__compare(other)
            if r is NotImplemented:
                return r
            else:
                return r != 0

        def __str__(self):
            return IcePy.stringify(self, _M_Bankk._t_Money)

        __repr__ = __str__

    _M_Bankk._t_Money = IcePy.defineStruct('::Bankk::Money', Money, (), (
        ('currency', (), _M_Bankk._t_CURRENCY),
        ('amount', (), IcePy._t_long)
    ))

    _M_Bankk.Money = Money
    del Money

if 'CreditResponse' not in _M_Bankk.__dict__:
    _M_Bankk.CreditResponse = Ice.createTempClass()
    class CreditResponse(Ice.Value):
        def __init__(self, fAccepted=False, costNational=Ice.Unset, costForeign=Ice.Unset):
            self.fAccepted = fAccepted
            self.costNational = costNational
            self.costForeign = costForeign

        def ice_id(self):
            return '::Bankk::CreditResponse'

        @staticmethod
        def ice_staticId():
            return '::Bankk::CreditResponse'

        def __str__(self):
            return IcePy.stringify(self, _M_Bankk._t_CreditResponse)

        __repr__ = __str__

    _M_Bankk._t_CreditResponse = IcePy.defineValue('::Bankk::CreditResponse', CreditResponse, -1, (), False, False, None, (
        ('fAccepted', (), IcePy._t_bool, False, 0),
        ('costNational', (), _M_Bankk._t_Money, True, 1),
        ('costForeign', (), _M_Bankk._t_Money, True, 2)
    ))
    CreditResponse._ice_type = _M_Bankk._t_CreditResponse

    _M_Bankk.CreditResponse = CreditResponse
    del CreditResponse

_M_Bankk._t_AccountStandard = IcePy.defineValue('::Bankk::AccountStandard', Ice.Value, -1, (), False, True, None, ())

if 'AccountStandardPrx' not in _M_Bankk.__dict__:
    _M_Bankk.AccountStandardPrx = Ice.createTempClass()
    class AccountStandardPrx(Ice.ObjectPrx):

        def getAccType(self, context=None):
            return _M_Bankk.AccountStandard._op_getAccType.invoke(self, ((), context))

        def getAccTypeAsync(self, context=None):
            return _M_Bankk.AccountStandard._op_getAccType.invokeAsync(self, ((), context))

        def begin_getAccType(self, _response=None, _ex=None, _sent=None, context=None):
            return _M_Bankk.AccountStandard._op_getAccType.begin(self, ((), _response, _ex, _sent, context))

        def end_getAccType(self, _r):
            return _M_Bankk.AccountStandard._op_getAccType.end(self, _r)

        def balance(self, context=None):
            return _M_Bankk.AccountStandard._op_balance.invoke(self, ((), context))

        def balanceAsync(self, context=None):
            return _M_Bankk.AccountStandard._op_balance.invokeAsync(self, ((), context))

        def begin_balance(self, _response=None, _ex=None, _sent=None, context=None):
            return _M_Bankk.AccountStandard._op_balance.begin(self, ((), _response, _ex, _sent, context))

        def end_balance(self, _r):
            return _M_Bankk.AccountStandard._op_balance.end(self, _r)

        @staticmethod
        def checkedCast(proxy, facetOrContext=None, context=None):
            return _M_Bankk.AccountStandardPrx.ice_checkedCast(proxy, '::Bankk::AccountStandard', facetOrContext, context)

        @staticmethod
        def uncheckedCast(proxy, facet=None):
            return _M_Bankk.AccountStandardPrx.ice_uncheckedCast(proxy, facet)

        @staticmethod
        def ice_staticId():
            return '::Bankk::AccountStandard'
    _M_Bankk._t_AccountStandardPrx = IcePy.defineProxy('::Bankk::AccountStandard', AccountStandardPrx)

    _M_Bankk.AccountStandardPrx = AccountStandardPrx
    del AccountStandardPrx

    _M_Bankk.AccountStandard = Ice.createTempClass()
    class AccountStandard(Ice.Object):

        def ice_ids(self, current=None):
            return ('::Bankk::AccountStandard', '::Ice::Object')

        def ice_id(self, current=None):
            return '::Bankk::AccountStandard'

        @staticmethod
        def ice_staticId():
            return '::Bankk::AccountStandard'

        def getAccType(self, current=None):
            raise NotImplementedError("servant method 'getAccType' not implemented")

        def balance(self, current=None):
            raise NotImplementedError("servant method 'balance' not implemented")

        def __str__(self):
            return IcePy.stringify(self, _M_Bankk._t_AccountStandardDisp)

        __repr__ = __str__

    _M_Bankk._t_AccountStandardDisp = IcePy.defineClass('::Bankk::AccountStandard', AccountStandard, (), None, ())
    AccountStandard._ice_type = _M_Bankk._t_AccountStandardDisp

    AccountStandard._op_getAccType = IcePy.Operation('getAccType', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, None, (), (), (), ((), _M_Bankk._t_AccountType, False, 0), ())
    AccountStandard._op_balance = IcePy.Operation('balance', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, None, (), (), (), ((), IcePy._t_long, False, 0), ())

    _M_Bankk.AccountStandard = AccountStandard
    del AccountStandard

_M_Bankk._t_AccountPremium = IcePy.defineValue('::Bankk::AccountPremium', Ice.Value, -1, (), False, True, None, ())

if 'AccountPremiumPrx' not in _M_Bankk.__dict__:
    _M_Bankk.AccountPremiumPrx = Ice.createTempClass()
    class AccountPremiumPrx(_M_Bankk.AccountStandardPrx):

        def creditRequest(self, c, Time, context=None):
            return _M_Bankk.AccountPremium._op_creditRequest.invoke(self, ((c, Time), context))

        def creditRequestAsync(self, c, Time, context=None):
            return _M_Bankk.AccountPremium._op_creditRequest.invokeAsync(self, ((c, Time), context))

        def begin_creditRequest(self, c, Time, _response=None, _ex=None, _sent=None, context=None):
            return _M_Bankk.AccountPremium._op_creditRequest.begin(self, ((c, Time), _response, _ex, _sent, context))

        def end_creditRequest(self, _r):
            return _M_Bankk.AccountPremium._op_creditRequest.end(self, _r)

        @staticmethod
        def checkedCast(proxy, facetOrContext=None, context=None):
            return _M_Bankk.AccountPremiumPrx.ice_checkedCast(proxy, '::Bankk::AccountPremium', facetOrContext, context)

        @staticmethod
        def uncheckedCast(proxy, facet=None):
            return _M_Bankk.AccountPremiumPrx.ice_uncheckedCast(proxy, facet)

        @staticmethod
        def ice_staticId():
            return '::Bankk::AccountPremium'
    _M_Bankk._t_AccountPremiumPrx = IcePy.defineProxy('::Bankk::AccountPremium', AccountPremiumPrx)

    _M_Bankk.AccountPremiumPrx = AccountPremiumPrx
    del AccountPremiumPrx

    _M_Bankk.AccountPremium = Ice.createTempClass()
    class AccountPremium(_M_Bankk.AccountStandard):

        def ice_ids(self, current=None):
            return ('::Bankk::AccountPremium', '::Bankk::AccountStandard', '::Ice::Object')

        def ice_id(self, current=None):
            return '::Bankk::AccountPremium'

        @staticmethod
        def ice_staticId():
            return '::Bankk::AccountPremium'

        def creditRequest(self, c, Time, current=None):
            raise NotImplementedError("servant method 'creditRequest' not implemented")

        def __str__(self):
            return IcePy.stringify(self, _M_Bankk._t_AccountPremiumDisp)

        __repr__ = __str__

    _M_Bankk._t_AccountPremiumDisp = IcePy.defineClass('::Bankk::AccountPremium', AccountPremium, (), None, (_M_Bankk._t_AccountStandardDisp,))
    AccountPremium._ice_type = _M_Bankk._t_AccountPremiumDisp

    AccountPremium._op_creditRequest = IcePy.Operation('creditRequest', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, None, (), (((), _M_Bankk._t_Money, False, 0), ((), IcePy._t_int, False, 0)), (), ((), _M_Bankk._t_CreditResponse, False, 0), ())

    _M_Bankk.AccountPremium = AccountPremium
    del AccountPremium

_M_Bankk._t_Bank = IcePy.defineValue('::Bankk::Bank', Ice.Value, -1, (), False, True, None, ())

if 'BankPrx' not in _M_Bankk.__dict__:
    _M_Bankk.BankPrx = Ice.createTempClass()
    class BankPrx(Ice.ObjectPrx):

        def createAccount(self, name, surname, pesel, income, context=None):
            return _M_Bankk.Bank._op_createAccount.invoke(self, ((name, surname, pesel, income), context))

        def createAccountAsync(self, name, surname, pesel, income, context=None):
            return _M_Bankk.Bank._op_createAccount.invokeAsync(self, ((name, surname, pesel, income), context))

        def begin_createAccount(self, name, surname, pesel, income, _response=None, _ex=None, _sent=None, context=None):
            return _M_Bankk.Bank._op_createAccount.begin(self, ((name, surname, pesel, income), _response, _ex, _sent, context))

        def end_createAccount(self, _r):
            return _M_Bankk.Bank._op_createAccount.end(self, _r)

        def logIn(self, pesel, context=None):
            return _M_Bankk.Bank._op_logIn.invoke(self, ((pesel, ), context))

        def logInAsync(self, pesel, context=None):
            return _M_Bankk.Bank._op_logIn.invokeAsync(self, ((pesel, ), context))

        def begin_logIn(self, pesel, _response=None, _ex=None, _sent=None, context=None):
            return _M_Bankk.Bank._op_logIn.begin(self, ((pesel, ), _response, _ex, _sent, context))

        def end_logIn(self, _r):
            return _M_Bankk.Bank._op_logIn.end(self, _r)

        @staticmethod
        def checkedCast(proxy, facetOrContext=None, context=None):
            return _M_Bankk.BankPrx.ice_checkedCast(proxy, '::Bankk::Bank', facetOrContext, context)

        @staticmethod
        def uncheckedCast(proxy, facet=None):
            return _M_Bankk.BankPrx.ice_uncheckedCast(proxy, facet)

        @staticmethod
        def ice_staticId():
            return '::Bankk::Bank'
    _M_Bankk._t_BankPrx = IcePy.defineProxy('::Bankk::Bank', BankPrx)

    _M_Bankk.BankPrx = BankPrx
    del BankPrx

    _M_Bankk.Bank = Ice.createTempClass()
    class Bank(Ice.Object):

        def ice_ids(self, current=None):
            return ('::Bankk::Bank', '::Ice::Object')

        def ice_id(self, current=None):
            return '::Bankk::Bank'

        @staticmethod
        def ice_staticId():
            return '::Bankk::Bank'

        def createAccount(self, name, surname, pesel, income, current=None):
            raise NotImplementedError("servant method 'createAccount' not implemented")

        def logIn(self, pesel, current=None):
            raise NotImplementedError("servant method 'logIn' not implemented")

        def __str__(self):
            return IcePy.stringify(self, _M_Bankk._t_BankDisp)

        __repr__ = __str__

    _M_Bankk._t_BankDisp = IcePy.defineClass('::Bankk::Bank', Bank, (), None, ())
    Bank._ice_type = _M_Bankk._t_BankDisp

    Bank._op_createAccount = IcePy.Operation('createAccount', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, None, (), (((), IcePy._t_string, False, 0), ((), IcePy._t_string, False, 0), ((), IcePy._t_string, False, 0), ((), IcePy._t_long, False, 0)), (), ((), _M_Bankk._t_AccountCreationInfo, False, 0), ())
    Bank._op_logIn = IcePy.Operation('logIn', Ice.OperationMode.Normal, Ice.OperationMode.Normal, False, None, (), (((), IcePy._t_string, False, 0),), (), ((), _M_Bankk._t_AccountStandardPrx, False, 0), ())

    _M_Bankk.Bank = Bank
    del Bank

# End of module Bankk
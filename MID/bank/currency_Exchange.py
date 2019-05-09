import sys
import grpc

sys.path.append("../Generated")

import time
from currencyProto_pb2 import *
from currencyProto_pb2_grpc import *
import threading


class Exchange:
    def __init__(self, currList, port):
        self.address = "localhost:" + port
        self.currencyList = currList
        self.channel = grpc.insecure_channel(self.address)
        self.stub = CurrencyExchangeStub(self.channel)
        self.currencyRatings = []
        self.lock = threading.RLock()

        thread = threading.Thread(target=self.run)
        thread.start()

    def run(self):
        ICR = CurrencyRequest()
        ICR.currList.extend(self.currencyList)
        for currencyResponse in self.stub.getCurrencies(ICR):
            self.updateCurrencies(currencyResponse)

    def updateCurrencies(self, currencyResponse):
        with self.lock:
            self.currencyRatings = []
            for cr in currencyResponse.currRatioList:
                self.currencyRatings.append((cr.first, cr.second, cr.ratio_F_S))
            print(self.currencyRatings)


if __name__ == "__main__":
    e = Exchange([EUR, USD, PLN], "5005")

    print("hello wordl xd")
    time.sleep(100 * 1000)

import generated.proto.CurrencyExchangeGrpc;
import generated.proto.CurrencyMid;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.concurrent.locks.Condition;

public class CurrencyExchangeService extends CurrencyExchangeGrpc.CurrencyExchangeImplBase{


    private class sendCurrenciesRate extends Thread{
        private List<CurrencyMid.CURRENCY> list;
        private StreamObserver<CurrencyMid.CurrencyResponse> responseObserver;
        private CurrencyExchange currencyExchange = CurrencyExchange.getInstance();
        private Condition condition = CurrencyExchange.getCondition();

        public sendCurrenciesRate(CurrencyMid.CurrencyRequest request, StreamObserver<CurrencyMid.CurrencyResponse> responseObserver){
            this.list = request.getCurrListList();
            this.responseObserver = responseObserver;
        }
        public void run(){
            while(true){
//                try{
//                    condition.await();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
                CurrencyExchange.waitForChange();
                /**jak oczekiwac na nowego pinga */
                CurrencyMid.CurrencyResponse.Builder currencyResponseBuilder= CurrencyMid.CurrencyResponse.newBuilder();
                for (CurrencyMid.CURRENCY currency: list)
                {
                    CurrencyMid.CurrencyResponse.CurrencyRatio.Builder currencyResponseRatioBuilder =CurrencyMid.CurrencyResponse.CurrencyRatio.newBuilder();
                    for (CurrencyMid.CURRENCY currency2: list){
                        if(currency.equals(currency2)){continue;}
                        currencyResponseRatioBuilder.setFirst(currency);
                        currencyResponseRatioBuilder.setSecond(currency2);
                        currencyResponseRatioBuilder.setRatioFS(currencyExchange.calculateRatio(currency,currency2));
                        currencyResponseBuilder.addCurrRatioList(currencyResponseRatioBuilder.build());
                    }
                }
                responseObserver.onNext(currencyResponseBuilder.build());
                System.out.println("Sending changesQ");
            }
        }

    }

    @Override
    public void getCurrencies(CurrencyMid.CurrencyRequest request, StreamObserver<CurrencyMid.CurrencyResponse> responseObserver) {
        sendCurrenciesRate newSendCurrenciesRate = new sendCurrenciesRate(request,responseObserver);
        newSendCurrenciesRate.start();
    }
}

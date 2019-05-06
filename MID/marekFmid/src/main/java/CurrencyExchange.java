import generated.proto.CurrencyMid;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CurrencyExchange extends Thread{
    private static CurrencyExchange currencyExchange = null;
    private static List<Money> currenciesRate;
    private static ReentrantLock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();

    public static Condition getCondition(){
        return condition;
    }

    private class Money{
        generated.proto.CurrencyMid.CURRENCY currency;
        long value;
        public Money(generated.proto.CurrencyMid.CURRENCY currency, long value){
            this.currency = currency;
            this.value = value;
        }
        public long calculateRatio(Money other){
            return this.value*1000/other.value;
        }
    }

    private CurrencyExchange(){
        if(currencyExchange == null){
            System.out.println("Creating new Currency Exchange");
            currenciesRate = new ArrayList<>();
            currenciesRate.add(new Money(CurrencyMid.CURRENCY.USD, 1000));
            currenciesRate.add(new Money(CurrencyMid.CURRENCY.EUR, 887));
            currenciesRate.add(new Money(CurrencyMid.CURRENCY.PLN, 3806 ));
            currenciesRate.add(new Money(CurrencyMid.CURRENCY.RUB,60447 ));
            currenciesRate.add(new Money(CurrencyMid.CURRENCY.KR,8604 ));
        }
    }

    private static void simulateFluctuation(){
        Random randGenerator = new Random();
        for(Money m:currenciesRate){
            int fluctuation = randGenerator.nextInt(100) - 50;
            m.value = (m.value * (1000 - fluctuation) )/1000;
        }
    }

    private static void awakingCall(){
        boolean fawaking = false;
        while (!fawaking){
            lock.lock();
            condition.signalAll();
            fawaking = true;
            lock.unlock();
        }
    }

    public static void waitForChange(){
        lock.lock();
        try{
            condition.await();
        }catch (Exception e){
            System.out.println("Error while waiting for ping");
        }

        lock.unlock();
    }

    public static CurrencyExchange getInstance(){
        if (currencyExchange == null){
            currencyExchange = new CurrencyExchange();
            currencyExchange.start();
        }
        return currencyExchange;
    }

    public long calculateRatio(CurrencyMid.CURRENCY c1,CurrencyMid.CURRENCY c2){
        Money m1 = null;
        Money m2 = null;
        for (Money m : currenciesRate){
            if(m.currency.equals(c1)){m1 = m;}
            if(m.currency.equals(c2)){m2 = m;}
        }
        if(m1 == null || m2 == null){
            return -1;
        }else{
            return m1.calculateRatio(m2);
        }
    }

    public void run() {
        while(true){
            try{
                sleep(5*1000);
            }catch (Exception e){
                System.out.println("Serious error while sleeping");
            }
            simulateFluctuation();
            awakingCall();
        }
    }


}
package bla.bla.pacote;

import java.util.Random;
import java.util.concurrent.Callable;

public class FictionalService implements Callable<MyValue> {
    private String metric;

    public FictionalService(String metric) {
        this.metric = metric;
    }

    public MyValue call(){
        MyValue myValue = new MyValue();
        long start = System.currentTimeMillis();
        try {

            myValue.setMetrica(metric);
            Integer valor = new Random().nextInt(30);
            if(valor<10){
                throw new Exception("Menor que 10 exception ");
            }
            myValue.setValor(valor);
            Integer time = new Random().nextInt(30);
            Thread.sleep(time*1000);
        }catch (Exception ex){
            myValue.setException(ex);
            myValue.setHasErrors(true);
        }finally {
            myValue.setTime(System.currentTimeMillis()-start);
            return myValue;
        }
    }
}

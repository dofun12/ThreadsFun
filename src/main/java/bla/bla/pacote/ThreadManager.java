package bla.bla.pacote;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadManager {

    //private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    String[] list;
    int familia = 0;
    int position = 0;
    int finished = 0;
    int errors = 0;
    float totalTime = 0;
    float avgTime = 0f;

    List<Future<MyValue>> completions = new ArrayList<Future<MyValue>>();
    boolean isrunning =true;

    public ThreadManager(){
        ExecutorService service = Executors.newCachedThreadPool();
        //ExecutorService service = Executors.newCachedThreadPool();
        update();
        Storage storage = new Storage();
        while (isrunning) {
            String next = getNext();
            if(next!=null) {
                completions.add(service.submit(new FictionalService(next)));
            }

            try {
                Iterator<Future<MyValue>> it = completions.iterator();
                while(it.hasNext()){
                    Future<MyValue> meuValorFuture = it.next();
                    if(meuValorFuture.isDone()){
                        try {
                            MyValue myValue = meuValorFuture.get();
                            if(myValue.isHasErrors()){
                                errors++;
                                //System.out.println("Erro ["+myValue.getMetrica()+"] with error "+myValue.getException().getMessage()+" "+myValue.getTime()+"ms");
                            }else{
                                totalTime = totalTime+ myValue.getTime();
                                storage.store(myValue.getMetrica(),myValue.getValor());
                                //System.out.println("O valor ["+myValue.getMetrica()+"="+myValue.getValor()+"] esta pronto ;) "+myValue.getTime()+"ms");
                            }
                            finished++;
                            if((finished - errors)>0) {
                                avgTime = (totalTime / ((float) finished));
                            }
                            storage.store("Finished",finished);
                            storage.store("Errors",errors);
                            System.out.println("Finished: "+finished+" ; Errors: "+errors+" ; Avg: "+avgTime);
                            it.remove();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                    }
                }

                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void update(){
        if(familia<10) {
            familia++;
            list = new String[ALPHABET.length()];
            for (int i = 0; i < 10; i++) {
                list[i] = generate(familia);
            }
            position = 0;
        }else{
            familia=1;
        }
    }

    public String getNext(){
        if(list.length<=position){
            update();
            return null;
        }
        String next = list[position];
        position++;
        return next;
    }


    public static String generate(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; ++i) {
            sb.append(ALPHABET.charAt(new Random().nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

}

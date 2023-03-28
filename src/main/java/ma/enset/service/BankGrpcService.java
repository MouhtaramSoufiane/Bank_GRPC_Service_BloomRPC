package ma.enset.service;

import io.grpc.stub.StreamObserver;
import ma.enset.stubs.Bank;
import ma.enset.stubs.BankServiceGrpc;

import java.util.Timer;
import java.util.TimerTask;

public class BankGrpcService extends BankServiceGrpc.BankServiceImplBase {
    @Override
    public void convert(Bank.ConverCurrencyRequest request, StreamObserver<Bank.ConvertCurrencyResponse> responseObserver) {
        String currencyFrom= request.getCurrencyFrom();
        String currencyTo=request.getCurrencyTo();
        double amount=request.getAmount();
        double result=amount*12;
        Bank.ConvertCurrencyResponse response= Bank.ConvertCurrencyResponse.newBuilder()
                .setCurrencyFrom(currencyFrom)
                .setCurrencyTo(currencyTo)
                .setAmount(amount)
                .setResult(result)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getCurrencyStream(Bank.ConverCurrencyRequest request, StreamObserver<Bank.ConvertCurrencyResponse> responseObserver) {
        String currencyFrom=request.getCurrencyFrom();
        String currencyTo= request.getCurrencyTo();
        double amount= request.getAmount();
        double result=amount*11;


        Timer timer=new Timer("Timer");
        timer.schedule(new TimerTask() {
            int count=0;
            @Override
            public void run() {
                Bank.ConvertCurrencyResponse response= Bank.ConvertCurrencyResponse.newBuilder()
                        .setCurrencyFrom(currencyFrom)
                        .setCurrencyTo(currencyTo)
                        .setAmount(amount)
                        .setResult(result*Math.random()*100)
                        .build();
                responseObserver.onNext(response);
                ++count;
                if (count==20){
                    responseObserver.onCompleted();
                    timer.cancel();
                }

            }
        }, 1000, 1000);
    }

    @Override
    public StreamObserver<Bank.ConverCurrencyRequest> performStream(StreamObserver<Bank.ConvertCurrencyResponse> responseObserver) {
        return new StreamObserver<Bank.ConverCurrencyRequest>() {
            double sum=0;
            @Override
            public void onNext(Bank.ConverCurrencyRequest converCurrencyRequest) {
                sum+=converCurrencyRequest.getAmount();

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                Bank.ConvertCurrencyResponse response= Bank.ConvertCurrencyResponse.newBuilder()
                        .setResult(sum*11.4)
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();

            }
        };
    }

    @Override
    public StreamObserver<Bank.ConverCurrencyRequest> fullCurrencyStream(StreamObserver<Bank.ConvertCurrencyResponse> responseObserver) {
        return new StreamObserver<Bank.ConverCurrencyRequest>() {
            @Override
            public void onNext(Bank.ConverCurrencyRequest converCurrencyRequest) {
                Bank.ConvertCurrencyResponse currencyResponse= Bank.ConvertCurrencyResponse.newBuilder()
                        .setResult(converCurrencyRequest.getAmount()*Math.random()*1100)
                        .build();
                responseObserver.onNext(currencyResponse);

            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();

            }
        };
    }
}



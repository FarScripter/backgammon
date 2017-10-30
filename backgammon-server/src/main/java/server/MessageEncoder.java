package server;


import com.google.gson.Gson;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;


public class MessageEncoder implements Encoder.Text<AbstractMessage>{

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public String encode(AbstractMessage myPackage) throws EncodeException {

        Gson gson = new Gson();
        System.out.println( "sending "+gson.toJson(myPackage));
        return gson.toJson(myPackage);


    }
}

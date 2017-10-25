package server;

import game.logics.Player;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler {
    private static volatile RequestHandler requestHandler;
//TODO violatile collection and hub??
    HashMap<Integer, MySession> sessions = new HashMap<>();
    Hub currentHub = new Hub();

    private RequestHandler(){

    }
    public static RequestHandler getInstance(){
        if(requestHandler == null)synchronized (RequestHandler.class){
            if(requestHandler == null){
                requestHandler = new RequestHandler();
            }
        }
        return requestHandler;
    }
//TODO синхронизация!!!!
    public AbstractMessage request(AbstractMessage pack, Session session){
        AbstractMessage message = null;
        MySession thisSession = null;
        if(!sessions.containsKey(session.getId())){
            MySession mySession;
            System.out.println("request "+ currentHub.getIter());

            thisSession = new MySession(currentHub, session, currentHub.getIter());
            Player player = new Player();
            player.setGame(currentHub.getGame());
            thisSession.setPlayer(player);
            System.out.println("dfdfd   "+ currentHub.getIter()+"    "+ thisSession.getNumber());

            currentHub.setSession(thisSession);
            message = pack.apply(thisSession);

            sessions.put(Integer.parseInt(session.getId()), thisSession);

            if(currentHub.getIter() == 2){
                this.sendGameStart(currentHub);
                currentHub = new Hub();
            }
        }else{
            for (Map.Entry<Integer, MySession> entry: sessions.entrySet()) {
                if(entry.getKey()==Integer.parseInt(session.getId())){
                    thisSession = entry.getValue(); //cought
                    break;
                }
            }
            message = pack.apply(thisSession);
        }

        Hub hub = thisSession.getHub();
        if(hub.getIter()==2){
            MySession secondMySession = hub.getSecondSessions(thisSession.getNumber());
            Session secondSession = secondMySession.getSession();
        }

        return message;
    }

    private void sendGameStart(Hub hub){
        MySession sessions[] = hub.getSessions();
        GameStart gameStart = new GameStart();
        gameStart.setEnemyUserName(sessions[1].getPlayer().getName());
        try {
            sessions[0].getSession().getBasicRemote().sendObject(gameStart);
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }
        gameStart.setEnemyUserName(sessions[0].getPlayer().getName());
        try {
            sessions[1].getSession().getBasicRemote().sendObject(gameStart);
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }
    }
}
